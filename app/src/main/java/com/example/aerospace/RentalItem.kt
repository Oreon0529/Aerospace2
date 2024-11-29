package com.example.aerospace

data class RentalItem(
    val name: String,
    var isAvailable: Boolean = true,
    var borrowerId: String? = null,
    var borrowerName: String? = null
)
