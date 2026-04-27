package com.kunpitech.shayariwala.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kunpitech.shayariwala.data.model.Shayari
import com.kunpitech.shayariwala.data.model.UserProfile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProfileRepository {

    private val db   = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val uid get() = auth.currentUser?.uid ?: "anonymous"

    // ── Profile ───────────────────────────────────────────
    fun getProfile(): Flow<UserProfile> = callbackFlow {
        val listener = db.collection("users").document(uid)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val profile = snap?.toObject(UserProfile::class.java)
                    ?: UserProfile(uid = uid, displayName = "Shayar")
                trySend(profile)
            }
        awaitClose { listener.remove() }
    }

    // ── Saved shayari ─────────────────────────────────────
    fun getSavedShayari(savedIds: List<String>): Flow<List<Shayari>> = callbackFlow {
        if (savedIds.isEmpty()) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }
        val ids = savedIds.take(30)
        val listener = db.collection("shayari")
            .whereIn(com.google.firebase.firestore.FieldPath.documentId(), ids)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val list = snap?.documents?.mapNotNull { doc ->
                    doc.toObject(Shayari::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    // ── Written shayari ───────────────────────────────────
    fun getWrittenShayari(): Flow<List<Shayari>> = callbackFlow {
        val listener = db.collection("shayari")
            .whereEqualTo("authorUid", uid)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val list = snap?.documents?.mapNotNull { doc ->
                    doc.toObject(Shayari::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    // ── Save shayari ──────────────────────────────────────
    // KEY FIX: use set+merge so document is created if missing
    suspend fun saveShayari(shayariId: String) {
        try {
            val currentUid = ensureAuth() ?: return

            // First ensure user document exists with set+merge
            // then arrayUnion the shayariId
            val userRef = db.collection("users").document(currentUid)

            // set with merge creates doc if not exists,
            // updates if exists — never throws NOT_FOUND
            userRef.set(
                mapOf("savedIds" to FieldValue.arrayUnion(shayariId)),
                SetOptions.merge(),
            ).await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ── Unsave shayari ────────────────────────────────────
    suspend fun unsaveShayari(shayariId: String) {
        try {
            val currentUid = ensureAuth() ?: return

            val userRef = db.collection("users").document(currentUid)

            userRef.set(
                mapOf("savedIds" to FieldValue.arrayRemove(shayariId)),
                SetOptions.merge(),
            ).await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ── Update profile ────────────────────────────────────
    suspend fun updateProfile(name: String, bio: String) {
        try {
            val currentUid = ensureAuth() ?: return

            db.collection("users").document(currentUid)
                .set(
                    mapOf(
                        "displayName" to name,
                        "bio"         to bio,
                        "uid"         to currentUid,
                    ),
                    SetOptions.merge(),
                ).await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ── Helper: ensure signed in ──────────────────────────
    private suspend fun ensureAuth(): String? {
        return try {
            val user = auth.currentUser
                ?: auth.signInAnonymously().await().user
            user?.uid
        } catch (e: Exception) {
            null
        }
    }
}