package com.yourcompany.rubberglueapp.data

import java.util.Date

// Repository for managing batches, customers, and sales
// In-memory for now, can be extended to use database or file storage
object Repository {
    private val batches = mutableListOf<Batch>()
    private val customers = mutableListOf<Customer>()
    private var nextBatchNumber = 1
    private var nextCustomerId = 1

    fun addBatch(date: Date, latexUsedKg: Double, glueProducedKg: Double, costLKR: Double, notes: String?): Batch {
        val batch = Batch(
            batchNumber = nextBatchNumber++,
            date = date,
            latexUsedKg = latexUsedKg,
            glueProducedKg = glueProducedKg,
            costLKR = costLKR,
            notes = notes
        )
        batches.add(batch)
        return batch
    }

    fun getBatches(): List<Batch> = batches

    fun addCustomer(name: String, contact: String?): Customer {
        val customer = Customer(id = nextCustomerId++, name = name, contact = contact)
        customers.add(customer)
        return customer
    }

    fun getCustomers(): List<Customer> = customers

    fun addSale(batchNumber: Int, customer: Customer, quantityKg: Double, pricePerKg: Double, dateOfSale: Date, profit: Double?): Sale? {
        val batch = batches.find { it.batchNumber == batchNumber }
        return if (batch != null) {
            val sale = Sale(customer, quantityKg, pricePerKg, dateOfSale, profit)
            batch.sales.add(sale)
            sale
        } else null
    }
}
