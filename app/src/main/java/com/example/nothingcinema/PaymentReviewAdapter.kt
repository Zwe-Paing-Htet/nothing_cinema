package com.example.nothingcinema

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class PaymentReviewAdapter(
    private val bookingList: List<DatabaseHelper.Booking>,
    private val dbHelper: DatabaseHelper,
    private val onDataChanged: () -> Unit
) : RecyclerView.Adapter<PaymentReviewAdapter.PaymentReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentReviewViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment_review, parent, false)
        return PaymentReviewViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PaymentReviewViewHolder, position: Int) {
        val booking = bookingList[position]

        val showtime = dbHelper.getShowtimeById(booking.showtimeId)
        val movie = dbHelper.getMovieById(showtime?.movieId ?: -1)
        val theatre = dbHelper.getTheatreById(showtime?.theatreId ?: -1)
        val cinema = theatre?.let { dbHelper.getCinemaById(it.cinemaId) }

        val bookedBy = if (booking.userId != null) {
            val user = dbHelper.getUserById(booking.userId)
            user?.username ?: "Unknown User"
        } else {
            "Guest"
        }

        val seatIds = dbHelper.getSeatIdsByBookingId(booking.id)
        val seatLabels = mutableListOf<String>()
        var totalPrice = 0.0

        for (seatId in seatIds) {
            val seat = dbHelper.getSeatById(seatId)
            if (seat != null) {
                seatLabels.add(seat.seatLabel)
                val seatType = dbHelper.getSeatTypeById(seat.seatTypeId)
                totalPrice += seatType?.price ?: 0.0
            }
        }

//        holder.bookingInfoTextView.text = """
//            Booked By: $bookedBy
//            Customer: ${booking.customerName}
//            Phone: ${booking.customerPhone}
//
//            Movie: ${movie?.name ?: "Unknown Movie"}
//            Theatre: ${theatre?.name ?: "Unknown Theatre"}
//            Cinema: ${cinema?.name ?: "Unknown Cinema"}
//
//            Seats: ${if (seatLabels.isEmpty()) "None" else seatLabels.sorted().joinToString(", ")}
//            Total Price: $totalPrice
//
//            Showtime Date: ${showtime?.showtimeDate ?: "Unknown Date"}
//            Showtime Time: ${showtime?.showtimeTime ?: "Unknown Time"}
//
//            Payment Method: ${booking.paymentMethod ?: "Unknown"}
//            Payment Review Status: ${booking.paymentReviewStatus}
//        """.trimIndent()

        holder.paymentStatusTextView.text = booking.paymentReviewStatus ?: "PENDING"
        holder.movieNameTextView.text = movie?.name ?: "Unknown Movie"
        holder.theatreCinemaTextView.text =
            "${theatre?.name ?: "Unknown Theatre"} • ${cinema?.name ?: "Unknown Cinema"}"
        holder.showtimeTextView.text =
            "${showtime?.showtimeDate ?: "Unknown Date"} • ${showtime?.showtimeTime ?: "Unknown Time"}"

        holder.bookingInfoTextView.text = """
        Booked by • $bookedBy
        Customer • ${booking.customerName}
        Phone • ${booking.customerPhone}
        
        Seats • ${if (seatLabels.isEmpty()) "None" else seatLabels.sorted().joinToString(", ")}
        Total • %.2f MMK
        
        Payment Method • ${booking.paymentMethod ?: "Unknown"}
        Review Status • ${booking.paymentReviewStatus ?: "PENDING"}
        """.trimIndent().format(totalPrice)

        if (booking.paymentScreenshot != null) {
            val bitmap = BitmapFactory.decodeByteArray(
                booking.paymentScreenshot,
                0,
                booking.paymentScreenshot.size
            )
            holder.paymentImageView.setImageBitmap(bitmap)
        } else {
            holder.paymentImageView.setImageResource(R.drawable.ic_placeholder)
        }

        holder.acceptButton.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Accept Payment")
                .setMessage("Are you sure you want to accept this buy payment?")
                .setPositiveButton("Yes") { _, _ ->
                    val success = dbHelper.approveBuyPayment(booking.id)
                    if (success) {
                        Toast.makeText(
                            holder.itemView.context,
                            "Payment accepted successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        onDataChanged()
                    } else {
                        Toast.makeText(
                            holder.itemView.context,
                            "Failed to accept payment",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }

        holder.rejectButton.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Reject Payment")
                .setMessage("Are you sure you want to reject this buy payment?")
                .setPositiveButton("Yes") { _, _ ->
                    val success = dbHelper.rejectBuyPayment(booking.id)
                    if (success) {
                        Toast.makeText(
                            holder.itemView.context,
                            "Payment rejected successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        onDataChanged()
                    } else {
                        Toast.makeText(
                            holder.itemView.context,
                            "Failed to reject payment",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }

        holder.paymentImageView.setOnClickListener {
            if (booking.paymentScreenshot != null) {
                ImagePreviewDialog.show(holder.itemView.context, booking.paymentScreenshot)
            }
        }
    }

    override fun getItemCount(): Int = bookingList.size

//    class PaymentReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val bookingInfoTextView: TextView = itemView.findViewById(R.id.bookingInfoTextView)
//        val paymentImageView: ImageView = itemView.findViewById(R.id.paymentImageView)
//        val acceptButton: Button = itemView.findViewById(R.id.acceptButton)
//        val rejectButton: Button = itemView.findViewById(R.id.rejectButton)
//    }

    class PaymentReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val paymentStatusTextView: TextView = itemView.findViewById(R.id.paymentStatusTextView)
        val movieNameTextView: TextView = itemView.findViewById(R.id.movieNameTextView)
        val theatreCinemaTextView: TextView = itemView.findViewById(R.id.theatreCinemaTextView)
        val showtimeTextView: TextView = itemView.findViewById(R.id.showtimeTextView)
        val bookingInfoTextView: TextView = itemView.findViewById(R.id.bookingInfoTextView)
        val paymentImageView: ImageView = itemView.findViewById(R.id.paymentImageView)
        val acceptButton: Button = itemView.findViewById(R.id.acceptButton)
        val rejectButton: Button = itemView.findViewById(R.id.rejectButton)
    }
}