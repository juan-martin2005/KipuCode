package com.kipucode.data.repository

import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.kipucode.data.local.dao.UserDao
import com.kipucode.data.local.dao.UserProgressDao
import com.kipucode.data.mapper.toDomain
import com.kipucode.data.mapper.toDto
import com.kipucode.data.mapper.toEntity
import com.kipucode.data.remote.firebase.service.AuthRemoteDataSource
import com.kipucode.data.remote.firebase.service.UserRemoteDataSource
import com.kipucode.domain.model.ErrorType
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.UserDomain
import com.kipucode.domain.model.UserProgressDomain
import com.kipucode.domain.repository.AuthRepository
import com.kipucode.domain.repository.CourseRepository
import javax.inject.Inject

// ================================================================================================
//  IMPLEMENTACIÓN DEL CONTRATO AUTHREPOSITORY
// ================================================================================================
internal class AuthRepositoryImpl @Inject constructor(
    // ============================================================================================
    //  Instancia de AuthRemoteDataSource   ->  Acceso a datos de FirebaseAuth
    //  Instancia de UserRemoteDataSource   ->  Acceso a datos de FireStore (USER & USER_PROGRESS)
    //  Instancia de UserDao                ->  Acceso a datos de UserDao (Room)
    //  Instancia de UserProgressDao        ->  Acceso a datos de UserProgressDao (Room)
    // ============================================================================================
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userDao: UserDao,
    private val userProgressDao: UserProgressDao,
    private val courseRepository: CourseRepository
): AuthRepository {     // Equivalente en java a hacer el (implements)

    // ============================================================================================
    //  Iniciar Sesión -> FirebaseAuth autentificación (Correo y Contraseña)
    // ============================================================================================
    override suspend fun login(email: String, password: String): Response<UserDomain> {
        return try {
            // AUTENTICACIÓN solo usa (Correo y Contraseña)
            val authResult = authRemoteDataSource.signInWithEmail(email, password)
            val currentUser = authResult.user

            // Si el usuario existe y las credenciales son correctas
            if (currentUser != null) {
                // Validación extra -> tener su correo verificado en FirebaseAuth
                if (currentUser.isEmailVerified) {
                    // FIRESTORE si esta verificado obtenemos los datos y progreso del usuario
                    val userDto = userRemoteDataSource.getUserProfile()
                    val progressDto = userRemoteDataSource.getUserProgress()

                    if (userDto != null && progressDto != null) {
                        userDao.insert(userDto.toEntity())

                        val syncResult = courseRepository.refreshCoursesAndLessons()
                        if (syncResult is Response.Success) {
                            userProgressDao.insert(progressDto.toEntity())

                            return Response.Success(userDto.toDomain())
                        } else {
                            return Response.Error(
                                "Error syncing courses during login",
                                ErrorType.FIRESTORE_ERROR
                            )
                        }

                    } else {
                        Response.Error(
                            "User profile data not found in database",
                            ErrorType.FIRESTORE_ERROR
                        )
                    }
                } else {
                    Response.Error(
                        "Email verification required",
                        ErrorType.EMAIL_NOT_VERIFIED
                    )
                }
            } else {
                Response.Error(
                    "An unexpected error occurred while signing in",
                    ErrorType.FIRESTORE_ERROR
                )
            }
        } catch (ex: FirebaseAuthInvalidCredentialsException) {
            Log.d("FIREBASE_LOGIN_ERROR", ex.toString())

            Response.Error(
                "The credential is invalid",
                ErrorType.CREDENTIAL_INVALID
            )
        } catch (ex: FirebaseNetworkException) {
            Log.d("FIREBASE_LOGIN_ERROR", ex.toString())

            Response.Error(
                "Please check your network and try again",
                ErrorType.NETWORK_ERROR
            )
        } catch (ex: Exception) {
            Log.d("FIREBASE_LOGIN_ERROR", ex.toString())

            Response.Error(
                "An unexpected error occurred while signing in",
                ErrorType.FIRESTORE_ERROR
            )
        }
    }

    // ============================================================================================
    //  Registro de Usuario -> FirebaseAuth autentificación / Firestore almacenar datos extra.
    // ============================================================================================
    override suspend fun register(userDomain: UserDomain, password: String): Response<UserDomain> {
        return try {
            // AUTENTICACIÓN solo usa (Correo y Contraseña)
            val authResult = authRemoteDataSource.registerUserWithEmail(userDomain.email, password)
            val currentUser = authResult.user // Almacenamos el usuario registrado

            // Si se registro correctamente
            if (currentUser != null) {
                // FIRESTORE creamos una hoja con los datos del usuario
                val updatedUser = userDomain.copy(id = currentUser.uid)
                userRemoteDataSource.saveUserProfile(updatedUser.toDto())

                // FIRESTORE creamos una hoja con el progreso del usuario
                val initialProgress = UserProgressDomain(
                    id = updatedUser.id,
                    userId = updatedUser.id,
                    currentLessonId = "python_lesson_01",
                    status = "IN_PROGRESS"
                )
                userRemoteDataSource.createUserProgress(initialProgress.toDto())

                // Punto importante -> Eliminar el UID almacenado de FirebaseAuth por defecto
                authRemoteDataSource.logoutUser()
                Response.Success(userDomain)
            } else {
                Response.Error(
                    "An unexpected error occurred during registration",
                    ErrorType.FIRESTORE_ERROR
                )
            }
        } catch (ex: FirebaseAuthUserCollisionException) {
            Log.d("FIREBASE_REGISTER_ERROR", ex.toString())

            Response.Error(
                "The email is already registered",
                ErrorType.EMAIL_ALREADY_EXIST
            )
        } catch (ex: FirebaseNetworkException) {
            Log.d("FIREBASE_REGISTER_ERROR", ex.toString())

            Response.Error(
                "Please check your network and try again",
                ErrorType.NETWORK_ERROR
            )
        } catch (ex: Exception) {
            Log.d("FIREBASE_REGISTER_ERROR", ex.toString())

            Response.Error(
                "An unexpected error occurred during registration",
                ErrorType.FIRESTORE_ERROR
            )
        }
    }

    // ============================================================================================
    //  Recuperar Contraseña -> Solicitar envío de correo para restablecer la contraseña
    // ============================================================================================
    override suspend fun resetPassword(email: String): Response<Unit> {
        return try {
            authRemoteDataSource.sendPasswordReset(email)
            Response.Success(Unit)
        } catch (ex: Exception) {
            Log.d("FIREBASE_RESET_PASSWORD_ERROR", ex.toString())

            Response.Error(
                "An error occurred while sending the email",
                ErrorType.FIRESTORE_ERROR
            )
        }
    }

    // ============================================================================================
    //  Estado de la Sesión -> Verificar si hay un usuario logueado
    // ============================================================================================
    override fun isUserLoggedIn(): Boolean = authRemoteDataSource.isUserLoggedIn()

    // ============================================================================================
    //  Cerrar Sesión -> Limpiar UID en FirebaseAuth y remover de la DB Local (Room)
    // ============================================================================================
    override suspend fun logout() {
        authRemoteDataSource.logoutUser()
        userProgressDao.clearProgressData()
        userDao.clearUserData()
    }
}
