package com.example.nothingcinema

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class SeatTypeAdapter(
    private val seatTypeList: List<DatabaseHelper.SeatType>,
    private val dbHelper: DatabaseHelper,
    private val onDataChanged: () -> Unit
) : RecyclerView.Adapter<SeatTypeAdapter.SeatTypeViewHolder>() {

    class SeatTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val seatTypeIdTextView: TextView = itemView.findViewById(R.id.seatTypeIdTextView)
        val seatTypeNameTextView: TextView = itemView.findViewById(R.id.seatTypeNameTextView)
        val seatTypePriceTextView: TextView = itemView.findViewById(R.id.seatTypePriceTextView)
        val editSeatTypeButton: Button = itemView.findViewById(R.id.editSeatTypeButton)
        val deleteSeatTypeButton: Button = itemView.findViewById(R.id.deleteSeatTypeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatTypeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_seat_type, parent, false)
        return SeatTypeViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeatTypeViewHolder, position: Int) {
        val seatType = seatTypeList[position]

//        holder.seatTypeIdTextView.text = "ID: ${seatType.id}"
//        holder.seatTypeNameTextView.text = "Seat Type Name: ${seatType.name}"
//        holder.seatTypePriceTextView.text = "Price: ${seatType.price}"

        holder.seatTypeIdTextView.text = "ID • ${seatType.id}"
        holder.seatTypeNameTextView.text = seatType.name
        holder.seatTypePriceTextView.text = "%.2f MMK".format(seatType.price)

        holder.editSeatTypeButton.setOnClickListener {
            showEditDialog(holder.itemView, seatType)
        }

        holder.deleteSeatTypeButton.setOnClickListener {
            val deleted = dbHelper.deleteSeatType(seatType.id)
            if (deleted) {
                Toast.makeText(holder.itemView.context, "Seat type deleted", Toast.LENGTH_SHORT).show()
                onDataChanged()
            } else {
                Toast.makeText(holder.itemView.context, "Failed to delete seat type", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = seatTypeList.size

    private fun showEditDialog(view: View, seatType: DatabaseHelper.SeatType) {
        val context = view.context
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_seat_type, null)

        val editSeatTypeNameEditText =
            dialogView.findViewById<EditText>(R.id.editSeatTypeNameEditText)
        val editSeatTypePriceEditText =
            dialogView.findViewById<EditText>(R.id.editSeatTypePriceEditText)

        editSeatTypeNameEditText.setText(seatType.name)
        editSeatTypePriceEditText.setText(seatType.price.toString())

//        AlertDialog.Builder(context)
////            .setTitle("Edit Seat Type")
//            .setView(dialogView)
//            .setPositiveButton("Save") { _, _ ->
//                val newName = editSeatTypeNameEditText.text.toString().trim()
//                val newPriceText = editSeatTypePriceEditText.text.toString().trim()
//
//                if (newName.isEmpty() || newPriceText.isEmpty()) {
//                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
//                    return@setPositiveButton
//                }
//
//                val newPrice = newPriceText.toDoubleOrNull()
//                if (newPrice == null) {
//                    Toast.makeText(context, "Invalid price", Toast.LENGTH_SHORT).show()
//                    return@setPositiveButton
//                }
//
//                val updated = dbHelper.updateSeatType(seatType.id, newName, newPrice)
//                if (updated) {
//                    Toast.makeText(context, "Seat type updated", Toast.LENGTH_SHORT).show()
//                    onDataChanged()
//                } else {
//                    Toast.makeText(context, "Failed to update seat type", Toast.LENGTH_SHORT).show()
//                }
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
//    }
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
        val saveButton = dialogView.findViewById<Button>(R.id.saveButton)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        dialog.window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.92).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        saveButton.setOnClickListener {
            val newName = editSeatTypeNameEditText.text.toString().trim()
            val newPriceText = editSeatTypePriceEditText.text.toString().trim()

            if (newName.isEmpty() || newPriceText.isEmpty()) {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newPrice = newPriceText.toDoubleOrNull()
            if (newPrice == null) {
                Toast.makeText(context, "Invalid price", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updated = dbHelper.updateSeatType(seatType.id, newName, newPrice)
            if (updated) {
                Toast.makeText(context, "Seat type updated", Toast.LENGTH_SHORT).show()
                onDataChanged()
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Failed to update seat type", Toast.LENGTH_SHORT).show()
            }
        }
    }
}