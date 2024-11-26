package com.example.tradefunc

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tradefunc.data.RentalHistory
import com.example.tradefunc.repository.RentalRepository
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RentalHistoryScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val rentalRepository = RentalRepository(context)
    var historyList by remember { mutableStateOf<List<RentalHistory>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Firestore에서 대여 이력 가져오기
    LaunchedEffect(Unit) {
        rentalRepository.getUserRentalHistory(
            userId = "testUser",
            onSuccess = { history ->
                historyList = history
                isLoading = false
            },
            onFailure = { exception ->
                Toast.makeText(context, "Failed to get history: ${exception.message}", Toast.LENGTH_LONG).show()
                isLoading = false
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isLoading) {
            Text("Loading...")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(historyList) { history ->
                    RentalHistoryRow(history)
                }
            }
        }
    }
}

@Composable
fun RentalHistoryRow(history: RentalHistory) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    val rentalDateString = history.rentalDate?.let {
        dateFormat.format(it.toDate())
    } ?: "Unknown"

    val returnDateString = history.returnDate?.let {
        dateFormat.format(it.toDate())
    } ?: "Not yet returned"

    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Item: ${history.itemName}")
            Text(text = "Rented on: $rentalDateString")
            Text(text = "Returned on: $returnDateString")
        }
    }
}
