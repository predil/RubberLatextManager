package com.yourcompany.rubberglueapp.ui


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yourcompany.rubberglueapp.R
import com.yourcompany.rubberglueapp.utils.TxtExporter
import com.yourcompany.rubberglueapp.utils.ExcelExporter
import java.io.File

class ReportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        val btnExportTxt = findViewById<Button>(R.id.btnExportTxtReport)
        val btnExportExcel = findViewById<Button>(R.id.btnExportExcelReport)
        val txtReportContent = findViewById<android.widget.TextView>(R.id.txtReportContent)

        btnExportTxt.setOnClickListener {
            exportData(isExcel = false)
        }
        btnExportExcel.setOnClickListener {
            exportData(isExcel = true)
        }

        txtReportContent.text = generateReport()
    }

    private fun generateReport(): String {
        val batches = com.yourcompany.rubberglueapp.data.Repository.getBatches()
        val customers = com.yourcompany.rubberglueapp.data.Repository.getCustomers()
        val totalBatches = batches.size
        val totalLatex = batches.sumOf { it.latexUsedKg }
        val totalGlue = batches.sumOf { it.glueProducedKg }
        val totalCost = batches.sumOf { it.costLKR }
        val totalSales = batches.flatMap { it.sales }
        val totalRevenue = totalSales.sumOf { it.quantityKg * it.pricePerKg }
        val totalProfit = totalSales.sumOf { it.profit ?: 0.0 }

        val sb = StringBuilder()
        sb.appendLine("Total Batches: $totalBatches")
        sb.appendLine("Total Latex Used: $totalLatex kg")
        sb.appendLine("Total Glue Produced: $totalGlue kg")
        sb.appendLine("Total Cost: LKR $totalCost")
        sb.appendLine("Total Revenue: LKR $totalRevenue")
        sb.appendLine("Total Profit: LKR $totalProfit")
        sb.appendLine()
        sb.appendLine("Sales by Customer:")
        customers.forEach { customer ->
            val custSales = totalSales.filter { it.customer.id == customer.id }
            val custQty = custSales.sumOf { it.quantityKg }
            val custRevenue = custSales.sumOf { it.quantityKg * it.pricePerKg }
            sb.appendLine("- ${customer.name}: $custQty kg, LKR $custRevenue")
        }
        sb.appendLine()
        sb.appendLine("Batch-wise Performance:")
        batches.forEach { batch ->
            val batchRevenue = batch.sales.sumOf { it.quantityKg * it.pricePerKg }
            val batchProfit = batch.sales.sumOf { it.profit ?: 0.0 }
            sb.appendLine("Batch #${batch.batchNumber}: Revenue LKR $batchRevenue, Profit LKR $batchProfit")
        }
        return sb.toString()
    }

    private fun exportData(isExcel: Boolean) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 101)
            Toast.makeText(this, "Please grant storage permission and try again.", Toast.LENGTH_SHORT).show()
            return
        }
        val dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: filesDir
        val file = File(dir, if (isExcel) "rubber_glue_data.xlsx" else "rubber_glue_data.txt")
        val success = if (isExcel) ExcelExporter.exportToExcel(file) else TxtExporter.exportToTxt(file)
        if (success) {
            Toast.makeText(this, "Exported to: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Export failed", Toast.LENGTH_SHORT).show()
        }
    }
}
