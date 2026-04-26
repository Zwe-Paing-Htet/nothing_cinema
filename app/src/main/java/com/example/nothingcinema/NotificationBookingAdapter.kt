package com.example.nothingcinema

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationBookingAdapter(
    private val bookingList: List<DatabaseHelper.Booking>,
    private val dbHelper: DatabaseHelper,
    private val onDataChanged: () -> Unit
) : RecyclerView.Adapter<NotificationBookingAdapter.BookingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookingList[position]

        val showtime = dbHelper.getShowtimeById(booking.showtimeId)
        val movie = dbHelper.getMovieById(showtime?.movieId ?: -1)
        val theatre = dbHelper.getTheatreById(showtime?.theatreId ?: -1)

        val bookedBy = if (booking.userId != null) {
            val user = dbHelper.getUserById(booking.userId)
            user?.username ?: "Unknown User"
        } else {
            "Guest"
        }

        val seatIds = dbHelper.getSeatIdsByBookingId(booking.id)
        val seatPriceDetails = mutableListOf<String>()
        var totalPrice = 0.0

        for (seatId in seatIds) {
            val seat = dbHelper.getSeatById(seatId)
            if (seat != null) {
                val seatType = dbHelper.getSeatTypeById(seat.seatTypeId)
                val price = seatType?.price ?: 0.0
                totalPrice += price
                seatPriceDetails.add("${seat.seatLabel} (${price})")
            }
        }

//        holder.bookingTimeTextView.text = "Booking Time: ${booking.bookingTime}"
//        holder.movieNameTextView.text = "Movie: ${movie?.name ?: "Unknown Movie"}"
//        holder.theatreNameTextView.text = "Theatre: ${theatre?.name ?: "Unknown Theatre"}"
//        holder.showtimeDateTextView.text = "Showtime: ${showtime?.showtimeDate ?: "Unknown Date"}"
//        holder.showtimeTimeTextView.text = "Time: ${showtime?.showtimeTime ?: "Unknown Time"}"
//        holder.bookedByTextView.text = "Booked By: $bookedBy"
//        holder.customerNameTextView.text = "Customer: ${booking.customerName}"
//        holder.customerPhoneTextView.text = "Phone: ${booking.customerPhone}"

        holder.bookingTimeTextView.text = "Booked at • ${booking.bookingTime}"
        holder.movieNameTextView.text = movie?.name ?: "Unknown Movie"
        holder.theatreNameTextView.text = theatre?.name ?: "Unknown Theatre"
        holder.showtimeDateTextView.text = showtime?.showtimeDate ?: "Unknown Date"
        holder.showtimeTimeTextView.text = showtime?.showtimeTime ?: "Unknown Time"
        holder.bookedByTextView.text = "Booked by • $bookedBy"
        holder.customerNameTextView.text = "Customer • ${booking.customerName}"
        holder.customerPhoneTextView.text = "Phone • ${booking.customerPhone}"

//        holder.reservationTypeTextView.text = when {
//            booking.reservationType == "BUY" && booking.paymentReviewStatus == "PENDING" ->
//                "Type: BOUGHT - PENDING"
//            booking.reservationType == "BUY" && booking.paymentReviewStatus == "APPROVED" ->
//                "Type: BOUGHT - APPROVED"
//            booking.reservationType == "BUY" && booking.paymentReviewStatus == "REJECTED" ->
//                "Type: BOUGHT - REJECTED"
//            booking.reservationType == "BUY" ->
//                "Type: BOUGHT"
//            else ->
//                "Type: BOOKED"
//        }

            holder.reservationTypeTextView.text = when {
                booking.reservationType == "BUY" && booking.paymentReviewStatus == "PENDING" ->
                    "BOUGHT • PENDING"
                booking.reservationType == "BUY" && booking.paymentReviewStatus == "APPROVED" ->
                    "BOUGHT • APPROVED"
                booking.reservationType == "BUY" && booking.paymentReviewStatus == "REJECTED" ->
                    "BOUGHT • REJECTED"
                booking.reservationType == "BUY" ->
                    "BOUGHT"
                else ->
                    "BOOKED"
            }
//
//        holder.bookedSeatsTextView.text =
//            "Booked Seats: ${if (seatPriceDetails.isEmpty()) "None" else seatPriceDetails.joinToString(", ")}"
//        holder.totalPriceTextView.text = "Total Price: $totalPrice"
//        holder.bookingStatusTextView.text = "Status: ${booking.bookingStatus}"
//        holder.canceledAtTextView.text = "Canceled At: ${booking.canceledAt ?: "-"}"

        holder.bookedSeatsTextView.text =
            "Seats • ${if (seatPriceDetails.isEmpty()) "None" else seatPriceDetails.joinToString(", ")}"
        holder.totalPriceTextView.text = "%.2f MMK".format(totalPrice)
        holder.bookingStatusTextView.text = "Status • ${booking.bookingStatus}"
        holder.canceledAtTextView.text = "Canceled at • ${booking.canceledAt ?: "-"}"

        if (booking.bookingStatus == "CANCELED") {
            holder.cancelBookingButton.visibility = View.GONE
        } else if (booking.reservationType == "BUY") {
            holder.cancelBookingButton.visibility = View.GONE
        } else {
            holder.cancelBookingButton.visibility = View.VISIBLE
            holder.cancelBookingButton.setOnClickListener {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Cancel Booking")
                    .setMessage("Are you sure you want to cancel this booking?")
                    .setPositiveButton("Yes") { _, _ ->
                        val canceledAt = SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss",
                            Locale.getDefault()
                        ).format(Date())

                        val canceled = dbHelper.cancelBooking(booking.id, canceledAt)
                        if (canceled) {
                            Toast.makeText(
                                holder.itemView.context,
                                "Booking canceled successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            onDataChanged()
                        } else {
                            Toast.makeText(
                                holder.itemView.context,
                                "Failed to cancel booking",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        }
    }

    override fun getItemCount(): Int = bookingList.size

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookingTimeTextView: TextView = itemView.findViewById(R.id.bookingTimeTextView)
        val movieNameTextView: TextView = itemView.findViewById(R.id.movieNameTextView)
        val theatreNameTextView: TextView = itemView.findViewById(R.id.theatreNameTextView)
        val showtimeDateTextView: TextView = itemView.findViewById(R.id.showtimeDateTextView)
        val showtimeTimeTextView: TextView = itemView.findViewById(R.id.showtimeTimeTextView)
        val bookedByTextView: TextView = itemView.findViewById(R.id.bookedByTextView)
        val customerNameTextView: TextView = itemView.findViewById(R.id.customerNameTextView)
        val customerPhoneTextView: TextView = itemView.findViewById(R.id.customerPhoneTextView)
        val reservationTypeTextView: TextView = itemView.findViewById(R.id.reservationTypeTextView)
        val bookedSeatsTextView: TextView = itemView.findViewById(R.id.bookedSeatsTextView)
        val totalPriceTextView: TextView = itemView.findViewById(R.id.totalPriceTextView)
        val bookingStatusTextView: TextView = itemView.findViewById(R.id.bookingStatusTextView)
        val canceledAtTextView: TextView = itemView.findViewById(R.id.canceledAtTextView)
        val cancelBookingButton: Button = itemView.findViewById(R.id.cancelBookingButton)
    }
}