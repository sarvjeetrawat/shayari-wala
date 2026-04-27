package com.kunpitech.shayariwala.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kunpitech.shayariwala.data.model.Shayari
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ShayariRepository {

    private val db         = FirebaseFirestore.getInstance()
    private val collection = db.collection("shayari")

    // Real-time feed — all or filtered by category
    fun getShayari(category: String = "all"): Flow<List<Shayari>> = callbackFlow {
        val query = if (category == "all") {
            collection.orderBy("createdAt", Query.Direction.DESCENDING)
        } else {
            collection
                .whereEqualTo("category", category)
                .orderBy("createdAt", Query.Direction.DESCENDING)
        }

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) { close(error); return@addSnapshotListener }
            val list = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Shayari::class.java)?.copy(id = doc.id)
            } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    // Toggle like (simple increment — replace with transaction for production)
    // Replace the toggleLike function with this
    suspend fun toggleLike(shayariId: String, isLiked: Boolean) {
        try {
            val ref = db.collection("shayari").document(shayariId)
            // FieldValue.increment is atomic — no race condition
            // no read needed — Firestore handles it server-side
            ref.update(
                "likes",
                com.google.firebase.firestore.FieldValue.increment(
                    if (isLiked) 1L else -1L
                )
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Single shayari real-time
    fun getShayariById(id: String): Flow<Shayari?> = callbackFlow {
        val listener = db.collection("shayari")
            .document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val shayari = snapshot?.toObject(Shayari::class.java)?.copy(id = snapshot.id)
                trySend(shayari)
            }
        awaitClose { listener.remove() }
    }

    // Related shayari — same category, exclude current
    fun getRelated(category: String, excludeId: String): Flow<List<Shayari>> = callbackFlow {
        val listener = db.collection("shayari")
            .whereEqualTo("category", category)
            .orderBy("likes", Query.Direction.DESCENDING)
            .limit(5)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val list = snapshot?.documents
                    ?.mapNotNull { doc ->
                        doc.toObject(Shayari::class.java)?.copy(id = doc.id)
                    }
                    ?.filter { it.id != excludeId }
                    ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    // Full text search by hindiText prefix
    fun searchShayari(query: String): Flow<List<Shayari>> = callbackFlow {
        if (query.isBlank()) { trySend(emptyList()); awaitClose { }; return@callbackFlow }
        val listener = db.collection("shayari")
            .orderBy("hindiText")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .limit(20)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Shayari::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    // Shayari by poet name
    fun getShayariByPoet(poetName: String): Flow<List<Shayari>> = callbackFlow {
        val listener = db.collection("shayari")
            .whereEqualTo("poet", poetName)
            .orderBy("likes", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Shayari::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }
}