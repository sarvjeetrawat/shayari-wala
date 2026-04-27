package com.kunpitech.shayariwala.ui.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kunpitech.shayariwala.data.model.Shayari
import com.kunpitech.shayariwala.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class SavedUiState(
    val isLoading    : Boolean        = true,
    val savedShayari : List<Shayari>  = emptyList(),
    val error        : String?        = null,
)

class SavedViewModel(
    private val repo: ProfileRepository = ProfileRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedUiState())
    val uiState: StateFlow<SavedUiState> = _uiState.asStateFlow()

    init { observeProfile() }

    private fun observeProfile() {
        repo.getProfile()
            .onEach { profile ->
                observeSaved(profile.savedIds)
            }
            .catch { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error     = e.message,
                )
            }
            .launchIn(viewModelScope)
    }

    private fun observeSaved(ids: List<String>) {
        if (ids.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                isLoading    = false,
                savedShayari = emptyList(),
            )
            return
        }
        repo.getSavedShayari(ids)
            .onEach { list ->
                _uiState.value = _uiState.value.copy(
                    isLoading    = false,
                    savedShayari = list,
                )
            }
            .catch { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error     = e.message,
                )
            }
            .launchIn(viewModelScope)
    }

    fun unsave(shayariId: String) {
        // Optimistic remove from UI instantly
        _uiState.value = _uiState.value.copy(
            savedShayari = _uiState.value.savedShayari.filter { it.id != shayariId }
        )
        viewModelScope.launch {
            repo.unsaveShayari(shayariId)
        }
    }
}