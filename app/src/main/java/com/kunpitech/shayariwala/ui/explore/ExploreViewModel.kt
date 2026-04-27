package com.kunpitech.shayariwala.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kunpitech.shayariwala.data.model.Poet
import com.kunpitech.shayariwala.data.model.Shayari
import com.kunpitech.shayariwala.data.repository.PoetRepository
import com.kunpitech.shayariwala.data.repository.ShayariRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

// Which tab is active in Explore
enum class ExploreTab { DISCOVER, POETS, MOODS }

data class ExploreUiState(
    val activeTab        : ExploreTab    = ExploreTab.DISCOVER,
    val searchQuery      : String        = "",
    val isSearching      : Boolean       = false,
    val searchResults    : List<Shayari> = emptyList(),

    // Discover tab
    val trendingShayari  : List<Shayari> = emptyList(),
    val isTrendingLoading: Boolean       = true,

    // Poets tab
    val poets            : List<Poet>    = emptyList(),
    val isPoetsLoading   : Boolean       = true,
    val selectedPoet     : Poet?         = null,
    val poetShayari      : List<Shayari> = emptyList(),
    val isPoetShayariLoading: Boolean    = false,

    val error            : String?       = null,
)

val moodCategories = listOf(
    Triple("ishq",    "♥ Ishq",    "Mohabbat aur dil ki baat"),
    Triple("dard",    "💧 Dard",   "Dil ka dard, aankhon ka paani"),
    Triple("zindagi", "✦ Zindagi", "Jeene ka andaaz"),
    Triple("khushi",  "☀ Khushi",  "Khushiyon ki baarish"),
    Triple("judai",   "☽ Judai",   "Bichadne ka gham"),
    Triple("wafa",    "◈ Wafa",    "Wafadaari ki kahani"),
)

@OptIn(FlowPreview::class)
class ExploreViewModel(
    private val shayariRepo : ShayariRepository = ShayariRepository(),
    private val poetRepo    : PoetRepository    = PoetRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    // Internal raw query flow for debounce
    private val _rawQuery = MutableStateFlow("")

    init {
        loadTrending()
        loadPoets()
        observeSearch()
    }

    fun setTab(tab: ExploreTab) {
        _uiState.value = _uiState.value.copy(activeTab = tab, searchQuery = "")
        _rawQuery.value = ""
    }

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query, isSearching = query.isNotBlank())
        _rawQuery.value = query
    }

    fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            searchQuery   = "",
            isSearching   = false,
            searchResults = emptyList(),
        )
        _rawQuery.value = ""
    }

    fun selectPoet(poet: Poet) {
        _uiState.value = _uiState.value.copy(
            selectedPoet          = poet,
            isPoetShayariLoading  = true,
        )
        shayariRepo.getShayariByPoet(poet.name)
            .onEach { list ->
                _uiState.value = _uiState.value.copy(
                    poetShayari           = list,
                    isPoetShayariLoading  = false,
                )
            }
            .catch { _uiState.value = _uiState.value.copy(isPoetShayariLoading = false) }
            .launchIn(viewModelScope)
    }

    fun clearSelectedPoet() {
        _uiState.value = _uiState.value.copy(
            selectedPoet = null,
            poetShayari  = emptyList(),
        )
    }

    private fun loadTrending() {
        shayariRepo.getShayari("all")
            .onEach { list ->
                _uiState.value = _uiState.value.copy(
                    trendingShayari   = list.filter { it.isTrending }.take(10),
                    isTrendingLoading = false,
                )
            }
            .catch { e ->
                _uiState.value = _uiState.value.copy(
                    isTrendingLoading = false,
                    error             = e.message,
                )
            }
            .launchIn(viewModelScope)
    }

    private fun loadPoets() {
        poetRepo.getAllPoets()
            .onEach { list ->
                _uiState.value = _uiState.value.copy(poets = list, isPoetsLoading = false)
            }
            .catch { e ->
                _uiState.value = _uiState.value.copy(isPoetsLoading = false, error = e.message)
            }
            .launchIn(viewModelScope)
    }

    private fun observeSearch() {
        _rawQuery
            .debounce(400)
            .distinctUntilChanged()
            .flatMapLatest { query -> shayariRepo.searchShayari(query) }
            .onEach { results ->
                _uiState.value = _uiState.value.copy(searchResults = results)
            }
            .catch { /* ignore search errors silently */ }
            .launchIn(viewModelScope)
    }
}