package com.kunpitech.shayariwala.ui.home

import androidx.lifecycle.ViewModel
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

data class HomeUiState(
    val isLoading        : Boolean       = true,
    val shayariList      : List<Shayari> = emptyList(),
    val selectedCategory : String        = "all",
    val likedIds         : Set<String>   = emptySet(),
    val error            : String?       = null,
)

val categories = listOf("all", "ishq", "dard", "zindagi", "khushi", "judai", "wafa")

class HomeViewModel(
    private val repo: ShayariRepository = ShayariRepository()
) : ViewModel() {

    private val _shayariList      = MutableStateFlow<List<Shayari>>(emptyList())
    private val _selectedCategory = MutableStateFlow("all")
    private val _isLoading        = MutableStateFlow(true)
    private val _error            = MutableStateFlow<String?>(null)

    // Combine shayari list + global likedIds into one UI state
    val uiState: StateFlow<HomeUiState> = combine(
        _shayariList,
        _selectedCategory,
        _isLoading,
        _error,
        LikeRepository.likedIds,           // ← global singleton
    ) { shayariList, category, loading, error, likedIds ->
        HomeUiState(
            isLoading        = loading,
            shayariList      = shayariList,
            selectedCategory = category,
            likedIds         = likedIds,
            error            = error,
        )
    }.stateIn(
        scope            = viewModelScope,
        started          = SharingStarted.WhileSubscribed(5_000),
        initialValue     = HomeUiState(),
    )

    init { loadShayari("all") }

    fun selectCategory(category: String) {
        if (category == _selectedCategory.value) return
        _selectedCategory.value = category
        _isLoading.value        = true
        loadShayari(category)
    }

    private fun loadShayari(category: String) {
        repo.getShayari(category)
            .onEach { list ->
                _shayariList.value = list
                _isLoading.value   = false
                _error.value       = null
            }
            .catch { e ->
                _isLoading.value = false
                _error.value     = e.message
            }
            .launchIn(viewModelScope)
    }

    // Delegate to singleton — updates all screens instantly
    fun toggleLike(shayariId: String) {
        LikeRepository.toggleLike(shayariId)
    }
}