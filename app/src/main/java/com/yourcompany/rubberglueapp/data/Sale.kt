package com.yourcompany.rubberglueapp.data

import java.util.Date

// Data class for a sale record

data class Sale(
    val customer: Customer,
    var quantityKg: Double,
    var pricePerKg: Double,
    var dateOfSale: Date,
    var profit: Double? = null
)
