package com.example.tradefunc.data

import com.google.firebase.Timestamp

data class RentalHistory(
    val userId: String = "",
    val itemId: String = "",
    val itemName: String = "",
    val rentalDate: Timestamp = Timestamp.now(),
    val returnDate: Timestamp? = null,
    val dueDate: Timestamp? = null // 반납 예정일 필드 추가
)
