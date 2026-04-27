package com.kunpitech.shayariwala.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kunpitech.shayariwala.data.repository.LikeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class SplashState {
    object Loading  : SplashState()
    object Ready    : SplashState()
    object Error    : SplashState()
}

class SplashViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow<SplashState>(SplashState.Loading)
    val state: StateFlow<SplashState> = _state.asStateFlow()

    init { prepare() }

    private fun prepare() {
        viewModelScope.launch {
            try {
                // Ensure minimum splash duration for branding
                val minSplashJob = launch { delay(2500) }

                // Sign in anonymously if needed
                if (auth.currentUser == null) {
                    auth.signInAnonymously().await()
                }

                // Init like repository
                LikeRepository.init()

                // Wait for minimum splash time
                minSplashJob.join()

                _state.value = SplashState.Ready

            } catch (e: Exception) {
                // Even on error, proceed after delay
                delay(2500)
                _state.value = SplashState.Ready
            }
        }
    }
}