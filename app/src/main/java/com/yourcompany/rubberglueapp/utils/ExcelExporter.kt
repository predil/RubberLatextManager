package com.yourcompany.rubberglueapp.utils

import com.yourcompany.rubberglueapp.data.Repository
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

object ExcelExporter {
    fun exportToExcel(file: File): Boolean {
        return try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Batches")
            var rowIdx = 0
            val header = sheet.createRow(rowIdx++)
            header.createCell(0).setCellValue("Batch #")
            header.createCell(1).setCellValue("Date")
            header.createCell(2).setCellValue("Latex Used (kg)")
            header.createCell(3).setCellValue("Glue Produced (kg)")
            header.createCell(4).setCellValue("Cost (LKR)")
            header.createCell(5).setCellValue("Notes")
            header.createCell(6).setCellValue("Sale Customer")
            header.createCell(7).setCellValue("Sale Qty (kg)")
            header.createCell(8).setCellValue("Sale Price/kg")
            header.createCell(9).setCellValue("Sale Date")
            header.createCell(10).setCellValue("Sale Profit")
            Repository.getBatches().forEach { batch ->
                if (batch.sales.isEmpty()) {
                    val row = sheet.createRow(rowIdx++)
                    row.createCell(0).setCellValue(batch.batchNumber.toDouble())
                    row.createCell(1).setCellValue(batch.date.toString())
                    row.createCell(2).setCellValue(batch.latexUsedKg)
                    row.createCell(3).setCellValue(batch.glueProducedKg)
                    row.createCell(4).setCellValue(batch.costLKR)
                    row.createCell(5).setCellValue(batch.notes ?: "")
                } else {
                    batch.sales.forEach { sale ->
                        val row = sheet.createRow(rowIdx++)
                        row.createCell(0).setCellValue(batch.batchNumber.toDouble())
                        row.createCell(1).setCellValue(batch.date.toString())
                        row.createCell(2).setCellValue(batch.latexUsedKg)
                        row.createCell(3).setCellValue(batch.glueProducedKg)
                        row.createCell(4).setCellValue(batch.costLKR)
                        row.createCell(5).setCellValue(batch.notes ?: "")
                        row.createCell(6).setCellValue(sale.customer.name)
                        row.createCell(7).setCellValue(sale.quantityKg)
                        row.createCell(8).setCellValue(sale.pricePerKg)
                        row.createCell(9).setCellValue(sale.dateOfSale.toString())
                        row.createCell(10).setCellValue(sale.profit ?: 0.0)
                    }
                }
            }
            FileOutputStream(file).use { workbook.write(it) }
            workbook.close()
            true
        } catch (e: Exception) {
            false
        }
    }
}
