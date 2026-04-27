package com.kunpitech.shayariwala.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kunpitech.shayariwala.data.model.Shayari
import com.kunpitech.shayariwala.data.repository.LikeRepository
import com.kunpitech.shayariwala.data.repository.ProfileRepository
import com.kunpitech.shayariwala.data.repository.ShayariRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DetailUiState(
    val isLoading    : Boolean       = true,
    val shayari      : Shayari?      = null,
    val related      : List<Shayari> = emptyList(),
    val isLiked      : Boolean       = false,
    val isSaved      : Boolean       = false,
    val error        : String?       = null,
)

class DetailViewModel(
    private val shayariId : String,
    private val repo      : ShayariRepository = ShayariRepository(),
) : ViewModel() {

    private val _shayari  = MutableStateFlow<Shayari?>(null)
    private val _related  = MutableStateFlow<List<Shayari>>(emptyList())
    private val _isLoading= MutableStateFlow(true)
    private val _isSaved  = MutableStateFlow(false)
    private val _error    = MutableStateFlow<String?>(null)

    // Combine shayari + global likedIds
    val uiState: StateFlow<DetailUiState> = combine(
        _shayari,
        _related,
        _isLoading,
        _isSaved,
        _error,
        LikeRepository.likedIds,           // ← global singleton
    ) { values ->
        val shayari  = values[0] as? Shayari
        val related  = @Suppress("UNCHECKED_CAST") (values[1] as List<Shayari>)
        val loading  = values[2] as Boolean
        val saved    = values[3] as Boolean
        val error    = values[4] as? String
        val likedIds = @Suppress("UNCHECKED_CAST") (values[5] as Set<String>)
        DetailUiState(
            isLoading = loading,
            shayari   = shayari,
            related   = related,
            isLiked   = shayariId in likedIds,
            isSaved   = saved,
            error     = error,
        )
    }.stateIn(
        scope        = viewModelScope,
        started      = SharingStarted.WhileSubscribed(5_000),
        initialValue = DetailUiState(),
    )

    init { loadShayari() }

    private fun loadShayari() {
        repo.getShayariById(shayariId)
            .onEach { shayari ->
                _shayari.value  = shayari
                _isLoading.value= false
                _error.value    = null
                if (shayari != null) loadRelated(shayari.category)
            }
            .catch { e ->
                _isLoading.value = false
                _error.value     = e.message
            }
            .launchIn(viewModelScope)
    }

    private fun loadRelated(category: String) {
        repo.getRelated(category = category, excludeId = shayariId)
            .onEach { _related.value = it }
            .catch { }
            .launchIn(viewModelScope)
    }

    // Delegate to singleton
    fun toggleLike() {
        LikeRepository.toggleLike(shayariId)
    }

    fun toggleSave() {
        val newSaved = !_isSaved.value
        _isSaved.value = newSaved
        viewModelScope.launch {
            val profileRepo = ProfileRepository()
            if (newSaved) profileRepo.saveShayari(shayariId)
            else          profileRepo.unsaveShayari(shayariId)
        }
    }

    class Factory(private val shayariId: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DetailViewModel(shayariId) as T
    }
}