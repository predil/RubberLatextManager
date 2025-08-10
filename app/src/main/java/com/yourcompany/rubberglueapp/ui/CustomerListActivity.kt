package com.yourcompany.rubberglueapp.ui


import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.yourcompany.rubberglueapp.R
import com.yourcompany.rubberglueapp.data.Repository

class CustomerListActivity : AppCompatActivity() {
    private lateinit var customerAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_list)

        val btnAddCustomer = findViewById<Button>(R.id.btnAddCustomer)
        val listCustomers = findViewById<ListView>(R.id.listCustomers)

        customerAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, getCustomerStrings())
        listCustomers.adapter = customerAdapter

        btnAddCustomer.setOnClickListener {
            showAddCustomerDialog()
        }

        listCustomers.setOnItemClickListener { _, _, position, _ ->
            showEditCustomerDialog(position)
        }
    }
    private fun showEditCustomerDialog(position: Int) {
        val customer = Repository.getCustomers().getOrNull(position) ?: return
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_customer, null)
        val edtName = dialogView.findViewById<EditText>(R.id.edtCustomerName)
        val edtContact = dialogView.findViewById<EditText>(R.id.edtCustomerContact)

        edtName.setText(customer.name)
        edtContact.setText(customer.contact ?: "")

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btnUpdateCustomer).setOnClickListener {
            val name = edtName.text.toString().trim()
            val contact = edtContact.text.toString().trim().takeIf { it.isNotBlank() }
            if (name.isNotBlank()) {
                customer.name = name
                customer.contact = contact
                customerAdapter.clear()
                customerAdapter.addAll(getCustomerStrings())
                customerAdapter.notifyDataSetChanged()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Name required", Toast.LENGTH_SHORT).show()
            }
        }

        dialogView.findViewById<Button>(R.id.btnDeleteCustomer).setOnClickListener {
            Repository.getCustomers().removeAt(position)
            customerAdapter.clear()
            customerAdapter.addAll(getCustomerStrings())
            customerAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun getCustomerStrings(): List<String> {
        return Repository.getCustomers().map {
            "${it.name}${if (!it.contact.isNullOrBlank()) " | ${it.contact}" else ""}"
        }
    }

    private fun showAddCustomerDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_customer, null)
        val edtName = dialogView.findViewById<EditText>(R.id.edtCustomerName)
        val edtContact = dialogView.findViewById<EditText>(R.id.edtCustomerContact)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btnSaveCustomer).setOnClickListener {
            val name = edtName.text.toString().trim()
            val contact = edtContact.text.toString().trim().takeIf { it.isNotBlank() }
            if (name.isNotBlank()) {
                Repository.addCustomer(name, contact)
                customerAdapter.clear()
                customerAdapter.addAll(getCustomerStrings())
                customerAdapter.notifyDataSetChanged()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Name required", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}
