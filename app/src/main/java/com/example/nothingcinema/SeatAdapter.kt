package com.example.nothingcinema

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class SeatAdapter(
    private val seatList: List<DatabaseHelper.Seat>,
    private val dbHelper: DatabaseHelper,
    private val onDataChanged: () -> Unit
) : RecyclerView.Adapter<SeatAdapter.SeatViewHolder>() {

    class SeatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val seatIdTextView: TextView = itemView.findViewById(R.id.seatIdTextView)
        val seatLabelTextView: TextView = itemView.findViewById(R.id.seatLabelTextView)
        val theatreNameTextView: TextView = itemView.findViewById(R.id.theatreNameTextView)
        val seatTypeNameTextView: TextView = itemView.findViewById(R.id.seatTypeNameTextView)
        val seatPriceTextView: TextView = itemView.findViewById(R.id.seatPriceTextView)
        val editSeatButton: Button = itemView.findViewById(R.id.editSeatButton)
        val deleteSeatButton: Button = itemView.findViewById(R.id.deleteSeatButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_seat, parent, false)
        return SeatViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
        val seat = seatList[position]
        val theatre = dbHelper.getTheatreById(seat.theatreId)
        val seatType = dbHelper.getSeatTypeById(seat.seatTypeId)


        holder.seatIdTextView.text = "ID • ${seat.id}"
        holder.seatLabelTextView.text = seat.seatLabel
        holder.theatreNameTextView.text = theatre?.name ?: "Unknown Theatre"
        holder.seatTypeNameTextView.text = seatType?.name ?: "Unknown Seat Type"
        holder.seatPriceTextView.text = "%.2f MMK".format(seatType?.price ?: 0.0)

        holder.editSeatButton.setOnClickListener {
            showEditDialog(holder.itemView, seat)
        }

        holder.deleteSeatButton.setOnClickListener {
            val deleted = dbHelper.deleteSeat(seat.id)
            if (deleted) {
                Toast.makeText(holder.itemView.context, "Seat deleted", Toast.LENGTH_SHORT).show()
                onDataChanged()
            } else {
                Toast.makeText(holder.itemView.context, "Failed to delete seat", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = seatList.size

    private fun showEditDialog(view: View, seat: DatabaseHelper.Seat) {
        val context = view.context
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_seat, null)

        val editSeatLabelEditText = dialogView.findViewById<EditText>(R.id.editSeatLabelEditText)
        val editTheatreSpinner = dialogView.findViewById<Spinner>(R.id.editTheatreSpinner)
        val editSeatTypeSpinner = dialogView.findViewById<Spinner>(R.id.editSeatTypeSpinner)

        val theatreList = dbHelper.getAllTheatres()
        val seatTypeList = dbHelper.getAllSeatTypes()

        val theatreNames = theatreList.map { it.name }
        val seatTypeNames = seatTypeList.map { "${it.name} - ${it.price}" }

        editSeatLabelEditText.setText(seat.seatLabel)

        val theatreAdapter =
            ArrayAdapter(context, R.layout.item_spinner_selected, theatreNames)
        theatreAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        editTheatreSpinner.adapter = theatreAdapter

        val seatTypeAdapter =
            ArrayAdapter(context, R.layout.item_spinner_selected, seatTypeNames)
        seatTypeAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        editSeatTypeSpinner.adapter = seatTypeAdapter

        val theatreIndex = theatreList.indexOfFirst { it.id == seat.theatreId }
        val seatTypeIndex = seatTypeList.indexOfFirst { it.id == seat.seatTypeId }

        if (theatreIndex >= 0) editTheatreSpinner.setSelection(theatreIndex)
        if (seatTypeIndex >= 0) editSeatTypeSpinner.setSelection(seatTypeIndex)

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
            val newSeatLabel = editSeatLabelEditText.text.toString().trim()

            if (newSeatLabel.isEmpty()) {
                Toast.makeText(context, "Please enter seat label", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedTheatre = theatreList[editTheatreSpinner.selectedItemPosition]
            val selectedSeatType = seatTypeList[editSeatTypeSpinner.selectedItemPosition]

            val updated = dbHelper.updateSeat(
                seatId = seat.id,
                newSeatLabel = newSeatLabel,
                newTheatreId = selectedTheatre.id,
                newSeatTypeId = selectedSeatType.id
            )

            if (updated) {
                Toast.makeText(context, "Seat updated", Toast.LENGTH_SHORT).show()
                onDataChanged()
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Failed to update seat", Toast.LENGTH_SHORT).show()
            }
        }
    }
}