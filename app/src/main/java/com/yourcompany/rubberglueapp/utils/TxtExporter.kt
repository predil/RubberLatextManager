package com.yourcompany.rubberglueapp.utils

import com.yourcompany.rubberglueapp.data.Repository
import java.io.File

object TxtExporter {
    fun exportToTxt(file: File): Boolean {
        return try {
            val sb = StringBuilder()
            sb.appendLine("Rubber Glue Production Data\n")
            Repository.getBatches().forEach { batch ->
                sb.appendLine("Batch #${batch.batchNumber} | Date: ${batch.date} | Latex: ${batch.latexUsedKg}kg | Glue: ${batch.glueProducedKg}kg | Cost: ${batch.costLKR} | Notes: ${batch.notes ?: "-"}")
                batch.sales.forEach { sale ->
                    sb.appendLine("  Sale: ${sale.customer.name} | Qty: ${sale.quantityKg}kg | Price: ${sale.pricePerKg} | Date: ${sale.dateOfSale} | Profit: ${sale.profit ?: "-"}")
                }
                sb.appendLine()
            }
            file.writeText(sb.toString())
            true
        } catch (e: Exception) {
            false
        }
    }
}
