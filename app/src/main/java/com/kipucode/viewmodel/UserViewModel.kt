package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.ErrorType
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.UserDomain
import com.kipucode.domain.model.UserProgressDomain
import com.kipucode.domain.usecase.user.GetUserProfileUseCase
import com.kipucode.domain.usecase.user.GetUserProgressUseCase
import com.kipucode.domain.usecase.user.RefreshUserProfileUseCase
import com.kipucode.domain.usecase.user.RefreshUserProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
// ================================================================================================
//  VIEWMODEL DE USUARIO -> CAPA DE PRESENTACIÓN (ARQ. MVVM - MODEL VIEW VIEW_MODEL)
// ================================================================================================
@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getUserProgressUseCase : GetUserProgressUseCase,
    private val refreshUserProfileUseCase : RefreshUserProfileUseCase,
    private val refreshUserProgressUseCase: RefreshUserProgressUseCase
) : ViewModel() {

    //  ! IMPORTANTE
    //  ENCAPSULAMIENTO: Uso de Patrón de Flujo Unidireccional. Expone 'StateFlow' inmutables
    //     de cara a la UI y manipula estados internos con 'MutableStateFlow'.

    //  (viewModelScope): El consumo de los flujos ('collect') y ejecuciones remotas se anclan
    //     al ciclo de vida del ViewModel, cancelando automáticamente las corrutinas al destruirse.

    //  OBSERVER AUTOMÁTICO: El bloque 'init' inicializa la escucha activa e inmediata de
    //     la DB local (Room), garantizando datos frescos en la pantalla desde el primer segundo.


    // ============================================================================================
    //  Estados Mutables Internos
    // ============================================================================================
    private val _userProfileState = MutableStateFlow<UserDomain?>(null)
    private val _userProgressState = MutableStateFlow<UserProgressDomain?>(null)
    private val _refreshState = MutableStateFlow<Response<Unit>?>(null)

    // ============================================================================================
    //  Estados Públicos Inmutables -> Solo Lectura por la UI (Compose)
    // ============================================================================================
    val userProfileState: StateFlow<UserDomain?> = _userProfileState
    val userProgressState: StateFlow<UserProgressDomain?> = _userProgressState
    val refreshState: StateFlow<Response<Unit>?> = _refreshState

    // ============================================================================================
    //  Init -> Automatiza los datos en pantalla ni bien se crea el componente
    // ============================================================================================
    init {
        startObservingUser()
        startObservingUserProgress()
    }

    // ============================================================================================
    //  Perfil -> Recolecta los cambios de Room y cambia el estado del Perfil
    // ============================================================================================
    fun startObservingUser() {
        viewModelScope.launch {
            getUserProfileUseCase.invoke().collect { userLocal ->
                _userProfileState.value = userLocal
            }
        }

    }

    // ============================================================================================
    //  Progreso del Usuario -> Recolecta los cambios de Room y cambia el estado del Progreso
    // ============================================================================================
    fun startObservingUserProgress(){
        viewModelScope.launch {
            getUserProgressUseCase.invoke().collect { userProgressLocal ->
                _userProgressState.value = userProgressLocal
            }
        }
    }

    // ============================================================================================
    //  Lógica de Sincronización Remota -> Descarga el Perfil y Progreso desde Firestore
    // ============================================================================================
    fun swipeToRefresh() {
        viewModelScope.launch {
            _refreshState.value = Response.Loading
            val profileResult = refreshUserProfileUseCase.invoke()
            val progressResult = refreshUserProgressUseCase.invoke()

            if(profileResult is Response.Success && progressResult is Response.Success) {
                _refreshState.value = Response.Success(Unit)
            } else {
                _refreshState.value = Response.Error("Error refreshing", ErrorType.NETWORK_ERROR)
            }
        }
    }

    // ============================================================================================
    //  Resetea el canal de refresco a nulo para evitar repeticiones de eventos
    // ============================================================================================
    fun resetRefreshState() {
        _refreshState.value = null
    }
}