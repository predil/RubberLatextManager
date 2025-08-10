package com.yourcompany.rubberglueapp.ui


import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.yourcompany.rubberglueapp.R
import com.yourcompany.rubberglueapp.data.Repository
import java.text.SimpleDateFormat
import java.util.*

class BatchListActivity : AppCompatActivity() {
    private lateinit var batchAdapter: ArrayAdapter<String>
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_batch_list)

        val btnAddBatch = findViewById<Button>(R.id.btnAddBatch)
        val listBatches = findViewById<ListView>(R.id.listBatches)

        batchAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, getBatchStrings())
        listBatches.adapter = batchAdapter

        btnAddBatch.setOnClickListener {
            showAddBatchDialog()
        }

        listBatches.setOnItemClickListener { _, _, position, _ ->
            showBatchOptionsDialog(position)
        }
    private fun showBatchOptionsDialog(position: Int) {
        val options = arrayOf("Edit Batch", "Add Sale", "View Sales")
        AlertDialog.Builder(this)
            .setTitle("Batch Options")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditBatchDialog(position)
                    1 -> showAddSaleDialog(position)
                    2 -> showSalesListDialog(position)
                }
            }
            .show()
    }

    private fun showAddSaleDialog(batchPosition: Int) {
        val batch = Repository.getBatches().getOrNull(batchPosition) ?: return
        val customers = Repository.getCustomers()
        if (customers.isEmpty()) {
            Toast.makeText(this, "No customers found. Add a customer first.", Toast.LENGTH_SHORT).show()
            return
        }
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_sale, null)
        val spinnerCustomer = dialogView.findViewById<Spinner>(R.id.spinnerCustomer)
        val edtQuantityKg = dialogView.findViewById<EditText>(R.id.edtQuantityKg)
        val edtPricePerKg = dialogView.findViewById<EditText>(R.id.edtPricePerKg)
        val edtDateOfSale = dialogView.findViewById<EditText>(R.id.edtDateOfSale)
        val edtProfit = dialogView.findViewById<EditText>(R.id.edtProfit)

        val customerNames = customers.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, customerNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCustomer.adapter = adapter

        edtDateOfSale.setText(dateFormat.format(Date()))

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btnSaveSale).setOnClickListener {
            try {
                val customer = customers[spinnerCustomer.selectedItemPosition]
                val quantity = edtQuantityKg.text.toString().toDoubleOrNull() ?: 0.0
                val price = edtPricePerKg.text.toString().toDoubleOrNull() ?: 0.0
                val date = dateFormat.parse(edtDateOfSale.text.toString()) ?: Date()
                val profit = edtProfit.text.toString().toDoubleOrNull()
                Repository.addSale(batch.batchNumber, customer, quantity, price, date, profit)
                Toast.makeText(this, "Sale added", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } catch (e: Exception) {
                Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun showSalesListDialog(batchPosition: Int) {
        val batch = Repository.getBatches().getOrNull(batchPosition) ?: return
        val sales = batch.sales
        if (sales.isEmpty()) {
            Toast.makeText(this, "No sales for this batch.", Toast.LENGTH_SHORT).show()
            return
        }
        val salesStrings = sales.map {
            "${it.customer.name} | Qty: ${it.quantityKg}kg | Price: ${it.pricePerKg} | Date: ${dateFormat.format(it.dateOfSale)}" +
            (if (it.profit != null) " | Profit: ${it.profit}" else "")
        }
        AlertDialog.Builder(this)
            .setTitle("Sales for Batch #${batch.batchNumber}")
            .setItems(salesStrings.toTypedArray(), null)
            .setPositiveButton("OK", null)
            .show()
    }
    }
    private fun showEditBatchDialog(position: Int) {
        val batch = Repository.getBatches().getOrNull(position) ?: return
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_batch, null)
        val edtDate = dialogView.findViewById<EditText>(R.id.edtDate)
        val edtLatexUsed = dialogView.findViewById<EditText>(R.id.edtLatexUsed)
        val edtGlueProduced = dialogView.findViewById<EditText>(R.id.edtGlueProduced)
        val edtCost = dialogView.findViewById<EditText>(R.id.edtCost)
        val edtNotes = dialogView.findViewById<EditText>(R.id.edtNotes)

        edtDate.setText(dateFormat.format(batch.date))
        edtLatexUsed.setText(batch.latexUsedKg.toString())
        edtGlueProduced.setText(batch.glueProducedKg.toString())
        edtCost.setText(batch.costLKR.toString())
        edtNotes.setText(batch.notes ?: "")

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btnUpdateBatch).setOnClickListener {
            try {
                val date = dateFormat.parse(edtDate.text.toString()) ?: batch.date
                val latexUsed = edtLatexUsed.text.toString().toDoubleOrNull() ?: batch.latexUsedKg
                val glueProduced = edtGlueProduced.text.toString().toDoubleOrNull() ?: batch.glueProducedKg
                val cost = edtCost.text.toString().toDoubleOrNull() ?: batch.costLKR
                val notes = edtNotes.text.toString().takeIf { it.isNotBlank() }
                batch.date = date
                batch.latexUsedKg = latexUsed
                batch.glueProducedKg = glueProduced
                batch.costLKR = cost
                batch.notes = notes
                batchAdapter.clear()
                batchAdapter.addAll(getBatchStrings())
                batchAdapter.notifyDataSetChanged()
                dialog.dismiss()
            } catch (e: Exception) {
                Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show()
            }
        }

        dialogView.findViewById<Button>(R.id.btnDeleteBatch).setOnClickListener {
            Repository.getBatches().removeAt(position)
            batchAdapter.clear()
            batchAdapter.addAll(getBatchStrings())
            batchAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun getBatchStrings(): List<String> {
        return Repository.getBatches().map {
            "Batch #${it.batchNumber} | Date: ${dateFormat.format(it.date)} | Latex: ${it.latexUsedKg}kg | Glue: ${it.glueProducedKg}kg"
        }
    }

    private fun showAddBatchDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_batch, null)
        val edtDate = dialogView.findViewById<EditText>(R.id.edtDate)
        val edtLatexUsed = dialogView.findViewById<EditText>(R.id.edtLatexUsed)
        val edtGlueProduced = dialogView.findViewById<EditText>(R.id.edtGlueProduced)
        val edtCost = dialogView.findViewById<EditText>(R.id.edtCost)
        val edtNotes = dialogView.findViewById<EditText>(R.id.edtNotes)

        edtDate.setText(dateFormat.format(Date()))

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btnSaveBatch).setOnClickListener {
            try {
                val date = dateFormat.parse(edtDate.text.toString()) ?: Date()
                val latexUsed = edtLatexUsed.text.toString().toDoubleOrNull() ?: 0.0
                val glueProduced = edtGlueProduced.text.toString().toDoubleOrNull() ?: 0.0
                val cost = edtCost.text.toString().toDoubleOrNull() ?: 0.0
                val notes = edtNotes.text.toString().takeIf { it.isNotBlank() }
                Repository.addBatch(date, latexUsed, glueProduced, cost, notes)
                batchAdapter.clear()
                batchAdapter.addAll(getBatchStrings())
                batchAdapter.notifyDataSetChanged()
                dialog.dismiss()
            } catch (e: Exception) {
                Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}
