package com.kunpitech.shayariwala.ui.moodfeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kunpitech.shayariwala.data.model.Shayari
import com.kunpitech.shayariwala.data.repository.LikeRepository
import com.kunpitech.shayariwala.data.repository.ShayariRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MoodFeedUiState(
    val isLoading   : Boolean       = true,
    val shayariList : List<Shayari> = emptyList(),
    val likedIds    : Set<String>   = emptySet(),  // from singleton
    val error       : String?       = null,
)

class MoodFeedViewModel(
    private val category: String,
    private val repo    : ShayariRepository = ShayariRepository(),
) : ViewModel() {

    private val _shayariList = MutableStateFlow<List<Shayari>>(emptyList())
    private val _isLoading   = MutableStateFlow(true)
    private val _error       = MutableStateFlow<String?>(null)

    val uiState: StateFlow<MoodFeedUiState> = combine(
        _shayariList,
        _isLoading,
        _error,
        LikeRepository.likedIds,
    ) { list, loading, error, likedIds ->
        MoodFeedUiState(
            isLoading   = loading,
            shayariList = list,
            likedIds    = likedIds,
            error       = error,
        )
    }.stateIn(
        scope        = viewModelScope,
        started      = SharingStarted.WhileSubscribed(5_000),
        initialValue = MoodFeedUiState(),
    )

    init { load() }

    private fun load() {
        repo.getShayari(category)
            .onEach { list ->
                _shayariList.value = list
                _isLoading.value   = false
            }
            .catch { e ->
                _isLoading.value = false
                _error.value     = e.message
            }
            .launchIn(viewModelScope)
    }

    fun toggleLike(id: String) {
        LikeRepository.toggleLike(id)
    }

    class Factory(private val category: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MoodFeedViewModel(category) as T
    }
}