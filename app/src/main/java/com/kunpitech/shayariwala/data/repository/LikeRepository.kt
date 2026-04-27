package com.kunpitech.shayariwala.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object LikeRepository {

    private val db    = FirebaseFirestore.getInstance()
    private val auth  = FirebaseAuth.getInstance()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _likedIds = MutableStateFlow<Set<String>>(emptySet())
    val likedIds: StateFlow<Set<String>> = _likedIds.asStateFlow()

    private val uid get() = auth.currentUser?.uid ?: "anonymous"

    // Track pending jobs per shayariId to debounce rapid taps
    private val pendingJobs = mutableMapOf<String, Job>()

    // Track the FINAL desired state per id to avoid double fire
    private val pendingState = mutableMapOf<String, Boolean>()

    fun init() {
        scope.launch {
            try {
                val doc = db.collection("users")
                    .document(uid)
                    .get()
                    .await()
                @Suppress("UNCHECKED_CAST")
                val liked = doc.get("likedIds") as? List<String> ?: emptyList()
                _likedIds.value = liked.toSet()
            } catch (e: Exception) {
                // silent
            }
        }
    }

    fun toggleLike(shayariId: String) {
        val current    = _likedIds.value
        val isNowLiked = shayariId !in current

        // 1. Update UI instantly
        _likedIds.value = if (isNowLiked) current + shayariId
        else            current - shayariId

        // 2. Record desired final state
        pendingState[shayariId] = isNowLiked

        // 3. Cancel any existing pending job for this id
        //    This prevents double-fire if user taps rapidly
        pendingJobs[shayariId]?.cancel()

        // 4. Debounce — wait 300ms before writing to Firestore
        //    If user taps again within 300ms, previous job is cancelled
        pendingJobs[shayariId] = scope.launch {
            delay(300)

            val finalState = pendingState[shayariId] ?: return@launch

            try {
                // ── Atomic increment — single server-side operation ──
                db.collection("shayari")
                    .document(shayariId)
                    .update(
                        "likes",
                        FieldValue.increment(if (finalState) 1L else -1L)
                    )
                    .await()

                // ── Persist likedIds to user doc ─────────────────────
                db.collection("users")
                    .document(uid)
                    .set(
                        mapOf(
                            "likedIds" to if (finalState)
                                FieldValue.arrayUnion(shayariId)
                            else
                                FieldValue.arrayRemove(shayariId)
                        ),
                        SetOptions.merge(),
                    )
                    .await()

            } catch (e: Exception) {
                // Revert UI on failure
                val reverted = _likedIds.value
                _likedIds.value = if (finalState) reverted - shayariId
                else            reverted + shayariId
            } finally {
                pendingJobs.remove(shayariId)
                pendingState.remove(shayariId)
            }
        }
    }
}