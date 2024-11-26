package com.example.tradefunc.repository

import android.content.Context
import android.widget.Toast
import com.example.tradefunc.data.RentalHistory
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class RentalRepository(private val context: Context) {
    private val db = FirebaseFirestore.getInstance()

    fun addRentalHistory(
        userId: String,
        itemId: String,
        itemName: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val rentalHistory = RentalHistory(
            userId = userId,
            itemId = itemId,
            itemName = itemName,
            rentalDate = Timestamp.now(),
            returnDate = null
        )

        db.collection("rentalHistory")
            .add(rentalHistory)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun updateItemAvailability(
        itemId: String,
        available: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("items")
            .document(itemId)
            .update("available", available)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun returnItem(
        itemId: String,
        userId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("rentalHistory")
            .whereEqualTo("itemId", itemId)
            .whereEqualTo("userId", userId)
            .whereEqualTo("returnDate", null)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    onFailure(Exception("No active rental found"))
                    return@addOnSuccessListener
                }
                val document = result.documents.first()
                db.collection("rentalHistory")
                    .document(document.id)
                    .update("returnDate", Timestamp.now())
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { exception -> onFailure(exception) }
            }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun getUserRentalHistory(
        userId: String,
        onSuccess: (List<RentalHistory>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("rentalHistory")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    onFailure(exception)
                    return@addSnapshotListener
                }

                val historyList = snapshot?.documents?.mapNotNull {
                    it.toObject(RentalHistory::class.java)
                } ?: emptyList()
                onSuccess(historyList)
            }
    }
}
