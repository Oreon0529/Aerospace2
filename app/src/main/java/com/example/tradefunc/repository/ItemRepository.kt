package com.example.tradefunc.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.example.tradefunc.data.Item

class ItemRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getItems(
        onSuccess: (List<Item>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("items")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    onFailure(exception)
                    return@addSnapshotListener
                }

                val itemList = snapshot?.documents?.mapNotNull { it.toObject(Item::class.java)?.copy(id = it.id) }
                    ?: emptyList()
                onSuccess(itemList)
            }
    }
}
