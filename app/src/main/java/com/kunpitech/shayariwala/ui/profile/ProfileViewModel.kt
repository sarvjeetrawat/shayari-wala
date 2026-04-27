package com.kunpitech.shayariwala.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kunpitech.shayariwala.data.model.Shayari
import com.kunpitech.shayariwala.data.model.UserProfile
import com.kunpitech.shayariwala.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

enum class ProfileTab { SAVED, WRITTEN }

data class ProfileUiState(
    val isLoading      : Boolean       = true,
    val profile        : UserProfile   = UserProfile(),
    val activeTab      : ProfileTab    = ProfileTab.SAVED,
    val savedShayari   : List<Shayari> = emptyList(),
    val writtenShayari : List<Shayari> = emptyList(),
    val error          : String?       = null,
)

class ProfileViewModel(
    private val repo: ProfileRepository = ProfileRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeProfile()
        observeWritten()
    }

    private fun observeProfile() {
        repo.getProfile()
            .onEach { profile ->
                _uiState.value = _uiState.value.copy(
                    profile   = profile,
                    isLoading = false,
                )
                observeSaved(profile.savedIds)
            }
            .catch { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
            .launchIn(viewModelScope)
    }

    private fun observeSaved(ids: List<String>) {
        repo.getSavedShayari(ids)
            .onEach { list ->
                _uiState.value = _uiState.value.copy(savedShayari = list)
            }
            .catch { /* ignore */ }
            .launchIn(viewModelScope)
    }

    private fun observeWritten() {
        repo.getWrittenShayari()
            .onEach { list ->
                _uiState.value = _uiState.value.copy(writtenShayari = list)
            }
            .catch { /* ignore */ }
            .launchIn(viewModelScope)
    }

    fun setTab(tab: ProfileTab) {
        _uiState.value = _uiState.value.copy(activeTab = tab)
    }

    fun unsave(shayariId: String) {
        viewModelScope.launch { repo.unsaveShayari(shayariId) }
    }
}