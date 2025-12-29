package com.projects.a122mmtv.auth

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projects.a122mmtv.dataclass.AuthNetwork
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repo: AuthRepository) : ViewModel() {
    val ui = MutableStateFlow<LoginUiState>(LoginUiState.Idle)

    fun doLogin(context: Context, email: String, password: String) = viewModelScope.launch {
        ui.value = LoginUiState.Loading
        repo.login(context, email, password)
            .onSuccess {
                ui.value = LoginUiState.Success
            }
            .onFailure { e ->
                ui.value = LoginUiState.Error(e.message ?: "Login failed")
            }
    }

}
sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data object Success : LoginUiState
    data class Error(val msg: String) : LoginUiState
}

class SignUpViewModel(private val repo: AuthRepository) : ViewModel() {
    val ui = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)

    fun doSignUp(email: String, name: String, password: String) = viewModelScope.launch {
        ui.value = SignUpUiState.Loading
        repo.signup(email, name, password)
            .onSuccess { ui.value = SignUpUiState.Success }
            .onFailure { e -> ui.value = SignUpUiState.Error(e.message ?: "Sign up error") }
    }
}

sealed interface SignUpUiState {
    data object Idle : SignUpUiState
    data object Loading : SignUpUiState
    data object Success : SignUpUiState
    data class Error(val msg: String) : SignUpUiState
}

class BannerViewModel(
    private val context: Context
) : ViewModel() {

    private val repo: AuthRepository by lazy {
        AuthRepository(
            publicApi = AuthNetwork.publicAuthApi,
            authedApi = AuthNetwork.authedAuthApi(context),
            store = TokenStore(context)
        )
    }

    fun setBannerFromCache(banner: AuthApiService.BannerDto) {
        bannerState.value = BannerUiState.Success(banner)
    }



    val bannerState = MutableStateFlow<BannerUiState>(BannerUiState.Idle)

    fun loadBanner(type: String) {
        viewModelScope.launch {
            bannerState.value = BannerUiState.Loading
            repo.getBanner(type)
                .onSuccess {
                    bannerState.value = BannerUiState.Success(it)
                }
                .onFailure {
                    bannerState.value =
                        BannerUiState.Error(it.message ?: "Failed to load banner")
                }
        }
    }
}



sealed interface BannerUiState {
    data object Idle : BannerUiState
    data object Loading : BannerUiState
    data class Success(val data: AuthApiService.BannerDto) : BannerUiState
    data class Error(val msg: String) : BannerUiState
}

class HomeSessionViewModel : ViewModel() {
    var userId by mutableStateOf<Int?>(null)
        private set

    var userName by mutableStateOf<String?>(null)
        private set

    var pplink by mutableStateOf<String?>(null)
        private set

    fun setUser(id: Int, name: String, pplink: String) {
        userId = id
        userName = name
        this.pplink = pplink
    }
}

