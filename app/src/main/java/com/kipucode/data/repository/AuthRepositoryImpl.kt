package com.kipucode.data.repository

import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.kipucode.data.local.dao.DailyActivityDao
import com.kipucode.data.local.dao.ExerciseDao
import com.kipucode.data.local.dao.LearningProgressDao
import com.kipucode.data.local.dao.UserDao
import com.kipucode.data.local.dao.UserProgressDao
import com.kipucode.data.mapper.toCompletedCoursesEntities
import com.kipucode.data.mapper.toCompletedLessonsEntities
import com.kipucode.data.mapper.toDomain
import com.kipucode.data.mapper.toDto
import com.kipucode.data.mapper.toEntity
import com.kipucode.data.remote.firebase.service.AuthRemoteDataSource
import com.kipucode.data.remote.firebase.service.UserRemoteDataSource
import com.kipucode.domain.model.ServerErrorType
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
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userDao: UserDao,
    private val userProgressDao: UserProgressDao,
    private val learningProgressDao: LearningProgressDao,
    private val exerciseDao: ExerciseDao,
    private val databaseSeedService: com.kipucode.data.local.DatabaseSeedService,
    private val dailyActivityDao: DailyActivityDao
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
            Response.Error("The credential is invalid", ServerErrorType.CREDENTIAL_INVALID)
        } catch (ex: FirebaseAuthUserCollisionException) {
            Log.e(logTag, "User Collision", ex)
            Response.Error("The email is already registered", ServerErrorType.EMAIL_ALREADY_EXIST)
        } catch (ex: FirebaseNetworkException) {
            Log.e(logTag, "Network Error", ex)
            Response.Error("Please check your network and try again", ServerErrorType.NETWORK_ERROR)
        } catch (ex: Exception) {
            Log.e(logTag, "Unexpected Error: ${ex::class.java.simpleName} - ${ex.message}", ex)
            Response.Error(
                message = "${ex::class.java.simpleName}: ${ex.localizedMessage}",
                error = ServerErrorType.FIRESTORE_ERROR
            )
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
                    .Error("An unexpected error occurred while signing in", ServerErrorType.FIRESTORE_ERROR)

            if (!currentUser.isEmailVerified) {
                authRemoteDataSource.logoutUser()
                return@safeFirebaseCall Response
                    .Error("Email verification required", ServerErrorType.EMAIL_NOT_VERIFIED)
            }


            val (userDto, progressDto) = coroutineScope {
                val userDtoDeferred = async { userRemoteDataSource.getUserProfile() }
                val progressDtoDeferred = async { userRemoteDataSource.getUserProgress() }

                Pair(userDtoDeferred.await(), progressDtoDeferred.await())
            }

            if(userDto == null || progressDto == null) {
                return@safeFirebaseCall Response.Error(
                    "User profile data not found", ServerErrorType.FIRESTORE_ERROR
                )
            }

            // Aseguramos que el catálogo local de cursos y lecciones esté pre-poblado antes de vincular el progreso
            databaseSeedService.seedIfNeeded()

            userDao.insert(userDto.toEntity())
            userProgressDao.insertFullProgress(
                progressDto.toEntity(),
                progressDto.toCompletedLessonsEntities(),
                progressDto.toCompletedCoursesEntities()
            )

            // Sincronizar de inmediato el progreso de ejercicios FSRS que correspondan al catálogo local
            try {
                val remoteList = userRemoteDataSource.getAllLearningProgress()
                if (remoteList.isNotEmpty()) {
                    val localExerciseIds = exerciseDao.getAllExerciseIds().toSet()
                    val validEntities = remoteList
                        .filter { localExerciseIds.contains(it.exerciseId) }
                        .map { it.toEntity(currentUser.uid) }

                    if (validEntities.isNotEmpty()) {
                        learningProgressDao.insertAll(validEntities)
                        Log.d("AuthRepository", "Sincronizados ${validEntities.size} ejercicios desde Firestore en login.")
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthRepository", "Error al precargar progreso de ejercicios en login: ${e.message}", e)
            }

            // Sincronizar calendario de actividad anual desde Firestore
            try {
                val currentYear = java.time.LocalDate.now().year
                val remoteDays = userRemoteDataSource.getYearActivity(currentYear)
                if (remoteDays.isNotEmpty()) {
                    val entities = remoteDays.map { (date, dto) ->
                        dto.toEntity(userId = currentUser.uid, date = date, year = currentYear)
                    }
                    dailyActivityDao.insertAll(entities)
                    Log.d("AuthRepository", "Sincronizados ${entities.size} días de actividad en login.")
                }
            } catch (e: Exception) {
                Log.e("AuthRepository", "Error al precargar calendario de actividad en login: ${e.message}", e)
            }

            Response.Success(userDto.toDomain())
        }
    }

    // ============================================================================================
    //  Registro de Usuario -> FirebaseAuth autentificación / Firestore almacenar datos extra.
    // ============================================================================================
    override suspend fun register(userDomain: UserDomain, password: String): Response<UserDomain> {
        return safeFirebaseCall("REGISTER_ERROR"){
            val initialLessonId = "csharp_lesson_01"

            val authResult = authRemoteDataSource.registerUserWithEmail(userDomain.email, password)
            val currentUser = authResult.user
                ?: return@safeFirebaseCall Response
                    .Error("An unexpected error occurred while signing in", ServerErrorType.FIRESTORE_ERROR)

            val user = userDomain.copy(id = currentUser.uid)

            val initialProgress = UserProgressDomain(
                id = user.id,
                userId = user.id,
                activeTrack = "c_sharp",
                lastVisitedLessonId = initialLessonId,
                streakDay = 1
            )

            userRemoteDataSource.saveUserProfile(user.toDto())
            userRemoteDataSource.createUserProgress(initialProgress.toDto())

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
                "An error occurred while sending the email", ServerErrorType.FIRESTORE_ERROR )
        }
    }

    override suspend fun reAutenticateUser(currentPassword: String): Response<Unit> {
       return try {
           authRemoteDataSource.reAutenticateUser(currentPassword)
           Response.Success(Unit)
       }
       catch (ex: FirebaseAuthInvalidCredentialsException) {
           Log.d("FIREBASE_REAUTENTICATE", ex.toString())

           Response.Error(
               "Error with your autenticate", ServerErrorType.FIRESTORE_ERROR )
       }
       catch (ex: Exception) {
           Log.d("FIREBASE_REAUTENTICATE", ex.toString())

           Response.Error(
               "An error occurred", ServerErrorType.FIRESTORE_ERROR )
       }
    }

    override suspend fun changePassword(newPassword: String): Response<Unit> {

        return try {
            authRemoteDataSource.changePassword(newPassword)
            Response.Success(Unit)
        } catch (ex: Exception) {
            Log.d("FIREBASE_RESET_PASSWORD_ERROR", ex.toString())

            Response.Error(
                "An error occurred", ServerErrorType.FIRESTORE_ERROR )
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
        val currentUid = userRemoteDataSource.currentUserId
        if (currentUid != null) {
            dailyActivityDao.clearUserActivity(currentUid)
        }
        authRemoteDataSource.logoutUser()
        userProgressDao.clearProgressData()
        userDao.clearUserData()
    }
}
