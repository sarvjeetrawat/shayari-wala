package com.kunpitech.shayariwala.ui.write

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class WriteUiState(
    val hindiText       : String  = "",
    val urduText        : String  = "",
    val poet            : String  = "",
    val selectedCategory: String  = "ishq",
    val isSubmitting    : Boolean = false,
    val isSubmitted     : Boolean = false,
    val error           : String? = null,
)

val writeCategories = listOf("ishq", "dard", "zindagi", "khushi", "judai", "wafa")

class WriteViewModel : ViewModel() {

    private val db   = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(WriteUiState())
    val uiState: StateFlow<WriteUiState> = _uiState.asStateFlow()

    fun onHindiTextChange(text: String) {
        _uiState.value = _uiState.value.copy(hindiText = text, error = null)
    }

    fun onUrduTextChange(text: String) {
        _uiState.value = _uiState.value.copy(urduText = text)
    }

    fun onPoetChange(text: String) {
        _uiState.value = _uiState.value.copy(poet = text)
    }

    fun onCategoryChange(cat: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = cat)
    }

    fun submit() {
        val state = _uiState.value
        if (state.hindiText.isBlank()) {
            _uiState.value = state.copy(error = "Shayari likho pehle!")
            return
        }
        if (state.poet.isBlank()) {
            _uiState.value = state.copy(error = "Shayar ka naam likhna zaroori hai")
            return
        }

        _uiState.value = state.copy(isSubmitting = true, error = null)

        viewModelScope.launch {
            try {
                val uid = auth.currentUser?.uid ?: "anonymous"
                val doc = mapOf(
                    "hindiText"   to state.hindiText.trim(),
                    "urduText"    to state.urduText.trim(),
                    "poet"        to state.poet.trim(),
                    "category"    to state.selectedCategory,
                    "likes"       to 0,
                    "comments"    to 0,
                    "isTrending"  to false,
                    "authorUid"   to uid,
                    "createdAt"   to System.currentTimeMillis(),
                )
                db.collection("shayari").add(doc).await()
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    isSubmitted  = true,
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    error        = e.message ?: "Kuch gadbad ho gayi",
                )
            }
        }
    }

    fun reset() {
        _uiState.value = WriteUiState()
    }
}