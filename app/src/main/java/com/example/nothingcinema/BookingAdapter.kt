package com.example.nothingcinema

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookingAdapter(
    private val bookingList: List<DatabaseHelper.Booking>,
    private val dbHelper: DatabaseHelper,
    private val showCanceledInfo: Boolean
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

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

        holder.reservationTypeTextView.text = if (booking.reservationType == "BUY") {
            "Type: BOUGHT"
        } else {
            "Type: BOOKED"
        }

//        holder.bookedSeatsTextView.text =
//            "Booked Seats: ${if (seatPriceDetails.isEmpty()) "None" else seatPriceDetails.joinToString(", ")}"
//        holder.totalPriceTextView.text = "Total Price: $totalPrice"

        holder.bookedSeatsTextView.text =
            "Seats • ${if (seatPriceDetails.isEmpty()) "None" else seatPriceDetails.joinToString(", ")}"
        holder.totalPriceTextView.text = "%.2f MMK".format(totalPrice)

        holder.cancelBookingButton.visibility = View.GONE

        if (showCanceledInfo) {
            holder.bookingStatusTextView.visibility = View.VISIBLE
            holder.canceledAtTextView.visibility = View.VISIBLE
//            holder.bookingStatusTextView.text = "Status: ${booking.bookingStatus}"
//            holder.canceledAtTextView.text = "Canceled At: ${booking.canceledAt ?: "-"}"

            holder.bookingStatusTextView.text = "Status • ${booking.bookingStatus}"
            holder.canceledAtTextView.text = "Canceled at • ${booking.canceledAt ?: "-"}"
        } else {
            holder.bookingStatusTextView.visibility = View.GONE
            holder.canceledAtTextView.visibility = View.GONE


        }
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }

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