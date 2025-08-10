package com.yourcompany.rubberglueapp.ui


import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yourcompany.rubberglueapp.utils.TxtExporter
import com.yourcompany.rubberglueapp.utils.ExcelExporter
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnBatches = findViewById<Button>(R.id.btnBatches)
        val btnCustomers = findViewById<Button>(R.id.btnCustomers)
        val btnReports = findViewById<Button>(R.id.btnReports)
        val btnExportTxt = findViewById<Button>(R.id.btnExportTxt)
        val btnExportExcel = findViewById<Button>(R.id.btnExportExcel)

        btnBatches.setOnClickListener {
            startActivity(Intent(this, BatchListActivity::class.java))
        }
        btnCustomers.setOnClickListener {
            startActivity(Intent(this, CustomerListActivity::class.java))
        }
        btnReports.setOnClickListener {
            startActivity(Intent(this, ReportActivity::class.java))
        }

        btnExportTxt.setOnClickListener {
            exportData(isExcel = false)
        }
        btnExportExcel.setOnClickListener {
            exportData(isExcel = true)
        }
    }

    private fun exportData(isExcel: Boolean) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
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
