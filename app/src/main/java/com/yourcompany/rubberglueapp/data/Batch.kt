package com.yourcompany.rubberglueapp.data

import java.util.Date

// Data class for a production batch
// Easily extendable for future features

data class Batch(
    val batchNumber: Int,
    val date: Date,
    var latexUsedKg: Double,
    var glueProducedKg: Double,
    var costLKR: Double,
    val sales: MutableList<Sale> = mutableListOf(),
    var notes: String? = null
)
