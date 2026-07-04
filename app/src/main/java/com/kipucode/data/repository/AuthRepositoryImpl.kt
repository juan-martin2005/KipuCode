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
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
    //  Instancia de CourseRepository       ->  Sincronización de cursos al iniciar sesión
    // ============================================================================================
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userDao: UserDao,
    private val userProgressDao: UserProgressDao,
): AuthRepository {     // Equivalente en java a hacer él (implements)

    //  ! IMPORTANTE
    //  Early Returns: Se utiliza `retornos tempranos` (return@safeFirebaseCall) en funciones
    //     como login() y register(). Esto evita anidar múltiples if/else, validando primero
    //     los errores y dejando el camino exitoso (SUCCESS) al final del bloque.

    //  safeFirebaseCall: Es una función envoltorio (wrapper) que centraliza la captura de
    //     excepciones de Firebase.

    private suspend fun <T> safeFirebaseCall(
        logTag: String,
        apiCall: suspend () -> Response<T>
    ): Response<T> {
        return try {
            apiCall()
        } catch (ex: FirebaseAuthInvalidCredentialsException) {
            Log.e(logTag, "Invalid Credentials", ex)
            Response.Error("The credential is invalid", ErrorType.CREDENTIAL_INVALID)
        } catch (ex: FirebaseAuthUserCollisionException) {
            Log.e(logTag, "User Collision", ex)
            Response.Error("The email is already registered", ErrorType.EMAIL_ALREADY_EXIST)
        } catch (ex: FirebaseNetworkException) {
            Log.e(logTag, "Network Error", ex)
            Response.Error("Please check your network and try again", ErrorType.NETWORK_ERROR)
        } catch (ex: Exception) {
            Log.e(logTag, "Unexpected Error", ex)
            Response.Error("An unexpected error occurred", ErrorType.FIRESTORE_ERROR)
        }
    }

    // ============================================================================================
    //  Iniciar Sesión -> FirebaseAuth autentificación (Correo y Contraseña)
    // ============================================================================================
    override suspend fun login(email: String, password: String): Response<UserDomain> {
        return safeFirebaseCall("LOGIN_ERROR"){
            val authResult = authRemoteDataSource.signInWithEmail(email, password)

            val currentUser = authResult.user
                ?: return@safeFirebaseCall Response
                    .Error("An unexpected error occurred while signing in", ErrorType.FIRESTORE_ERROR)

            if(!currentUser.isEmailVerified)
                return@safeFirebaseCall Response
                    .Error("Email verification required", ErrorType.EMAIL_NOT_VERIFIED)


            val (userDto, progressDto) = coroutineScope {
                val userDtoDeferred = async { userRemoteDataSource.getUserProfile() }
                val progressDtoDeferred = async { userRemoteDataSource.getUserProgress() }

                Pair(userDtoDeferred.await(), progressDtoDeferred.await())
            }

            if(userDto == null || progressDto == null) {
                return@safeFirebaseCall Response.Error(
                    "User profile data not found", ErrorType.FIRESTORE_ERROR
                )
            }

//            val syncResult = courseRepository.refreshCoursesAndLessons()
//            if(syncResult !is Response.Success)
//                return@safeFirebaseCall Response
//                    .Error("Error syncing courses during login", ErrorType.FIRESTORE_ERROR)

            userDao.insert(userDto.toEntity())
            userProgressDao.insert(progressDto.toEntity())

            Response.Success(userDto.toDomain())
        }
    }

    // ============================================================================================
    //  Registro de Usuario -> FirebaseAuth autentificación / Firestore almacenar datos extra.
    // ============================================================================================
    override suspend fun register(userDomain: UserDomain, password: String, courseSelected: String): Response<UserDomain> {
        return safeFirebaseCall("REGISTER_ERROR"){
            val initialLessonId = "${courseSelected.lowercase()}_lesson_01"

            val authResult = authRemoteDataSource.registerUserWithEmail(userDomain.email, password)
            val currentUser = authResult.user
                ?: return@safeFirebaseCall Response
                    .Error("An unexpected error occurred while signing in", ErrorType.FIRESTORE_ERROR)

            val user = userDomain.copy(id = currentUser.uid)

            val initialProgress = UserProgressDomain(
                id = user.id,
                userId = user.id,
                currentLessonId = initialLessonId,
                status = "IN_PROGRESS",
                streakDay = 1
            )

            userRemoteDataSource.saveUserProfile(user.toDto())
            userRemoteDataSource.createUserProgress(initialProgress.toDto())

//            userDao.insert(user.toEntity())
//            userProgressDao.insert(initialProgress.toEntity())
            logout()

            Response.Success(user)
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
                "An error occurred while sending the email", ErrorType.FIRESTORE_ERROR )
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
