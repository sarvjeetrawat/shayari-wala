package com.kunpitech.shayariwala.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kunpitech.shayariwala.data.model.Poet
import com.kunpitech.shayariwala.data.model.Shayari
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class PoetRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getAllPoets(): Flow<List<Poet>> = callbackFlow {
        val listener = db.collection("poets")
            .orderBy("shayariCount", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Poet::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }
}