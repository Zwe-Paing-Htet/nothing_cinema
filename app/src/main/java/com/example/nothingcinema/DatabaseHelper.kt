package com.example.nothingcinema

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "cinema_database.db"
        const val DATABASE_VERSION = 2

        // Role Table Constants
        const val ROLE_TABLE_NAME = "role"
        const val COLUMN_ROLE_ID = "role_id"
        const val COLUMN_ROLE_NAME = "role_name"

        // User Table Constants
        const val USER_TABLE_NAME = "user"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD_HASH = "password_hash"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_ROLE_ID_FK = "role_id"

        // Movie Table Constants
        const val MOVIE_TABLE_NAME = "movie"
        const val COLUMN_MOVIE_ID = "movie_id"
        const val COLUMN_MOVIE_NAME = "movie_name"
        const val COLUMN_MOVIE_RUNTIME = "runtime"
        const val COLUMN_MOVIE_GENRE = "genre"
        const val COLUMN_MOVIE_TRAILER = "movie_trailer"
        const val COLUMN_MOVIE_STATUS = "movie_status"
        const val COLUMN_MOVIE_POSTER = "movie_poster"
        const val COLUMN_MOVIE_DIRECTOR = "director"
        const val COLUMN_MOVIE_CAST = "movie_cast"
        const val COLUMN_MOVIE_DESCRIPTION = "description"
        const val COLUMN_MOVIE_COVER_POSTER = "cover_poster"

        // Cinema Table Constants
        const val CINEMA_TABLE_NAME = "cinema"
        const val COLUMN_CINEMA_ID = "cinema_id"
        const val COLUMN_CINEMA_NAME = "cinema_name"
        const val COLUMN_CINEMA_TOWNSHIP = "township"
        const val COLUMN_CINEMA_FULL_ADDRESS = "full_address"
        const val COLUMN_CINEMA_GOOGLE_MAP_URL = "google_map_url"
        const val COLUMN_CINEMA_IMAGE = "cinema_image" // Store image as byte array (BLOB)

        // Theatre Table Constants
        const val THEATRE_TABLE_NAME = "theatre"
        const val COLUMN_THEATRE_ID = "theatre_id"
        const val COLUMN_THEATRE_NAME = "theatre_name"
        const val COLUMN_THEATRE_TYPE = "theatre_type"
        const val COLUMN_THEATRE_IMAGE = "theatre_image"
        const val COLUMN_CINEMA_ID_FK = "cinema_id" // Foreign key for Cinema

        // showtime
        const val SHOWTIME_TABLE_NAME = "showtime"
        const val COLUMN_SHOWTIME_ID = "showtime_id"
        const val COLUMN_SHOWTIME_DATE = "showtime_date"
        const val COLUMN_SHOWTIME_TIME = "showtime_time"  // New column for the showtime time
        const val COLUMN_MOVIE_ID_FK = "movie_id"
        const val COLUMN_THEATRE_ID_FK = "theatre_id"

        // Seat Type Table Constants
        const val SEAT_TYPE_TABLE_NAME = "seat_type"
        const val COLUMN_SEAT_TYPE_ID = "seat_type_id"
        const val COLUMN_SEAT_TYPE_NAME = "seat_type_name"
        const val COLUMN_SEAT_PRICE = "seat_price"

        // Seat Table Constants
        const val SEAT_TABLE_NAME = "seat"
        const val COLUMN_SEAT_ID = "seat_id"
        const val COLUMN_SEAT_LABEL = "seat_label"
        const val COLUMN_SEAT_TYPE_ID_FK = "seat_type_id"
//        const val COLUMN_THEATRE_ID_FK_SEAT = "theatre_id"

        // Booking Table Constants
        const val BOOKING_TABLE_NAME = "booking"
        const val COLUMN_BOOKING_ID = "booking_id"
        const val COLUMN_BOOKING_TIME = "booking_time"
        const val COLUMN_NAME = "name" // New column for customer name
        const val COLUMN_PHONE_NUMBER = "phone_number" // New column for phone number
        const val COLUMN_BOOKING_STATUS = "booking_status"
        const val COLUMN_CANCELED_AT = "canceled_at"
        const val COLUMN_RESERVATION_TYPE = "reservation_type"
        const val COLUMN_PAYMENT_METHOD = "payment_method"
        const val COLUMN_PAYMENT_SCREENSHOT = "payment_screenshot"
        const val COLUMN_PAYMENT_REVIEW_STATUS = "payment_review_status"
        const val COLUMN_SHOWTIME_ID_FK = "showtime_id"  // Foreign key for Showtime
        const val COLUMN_USER_ID_FK = "user_id"

        // Booking Seat Table Constants
        const val BOOKING_SEAT_TABLE_NAME = "booking_seat"
        const val COLUMN_BOOKING_SEAT_ID = "booking_seat_id"
        const val COLUMN_BOOKING_ID_FK = "booking_id"
        const val COLUMN_SEAT_ID_FK = "seat_id"

    }

    override fun onCreate(db: SQLiteDatabase?) {

        // Create Role Table
        val createRoleTable = """
            CREATE TABLE $ROLE_TABLE_NAME (
                $COLUMN_ROLE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ROLE_NAME TEXT NOT NULL
            )
        """.trimIndent()
        db?.execSQL(createRoleTable)

        // Create User Table
        val createUserTable = """
            CREATE TABLE $USER_TABLE_NAME (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD_HASH TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL UNIQUE,
                $COLUMN_ROLE_ID_FK INTEGER,
                FOREIGN KEY($COLUMN_ROLE_ID_FK) REFERENCES $ROLE_TABLE_NAME($COLUMN_ROLE_ID)
            )
        """.trimIndent()
        db?.execSQL(createUserTable)

//        // Create the Movie table with the new movie_poster column (image)
        val createMovieTable = """
    CREATE TABLE $MOVIE_TABLE_NAME (
        $COLUMN_MOVIE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COLUMN_MOVIE_NAME TEXT,
        $COLUMN_MOVIE_RUNTIME INTEGER,
        $COLUMN_MOVIE_GENRE TEXT,
        $COLUMN_MOVIE_DIRECTOR TEXT,
        $COLUMN_MOVIE_CAST TEXT,
        $COLUMN_MOVIE_DESCRIPTION TEXT,
        $COLUMN_MOVIE_TRAILER TEXT,
        $COLUMN_MOVIE_STATUS TEXT,
        $COLUMN_MOVIE_POSTER BLOB,
        $COLUMN_MOVIE_COVER_POSTER BLOB
    )
""".trimIndent()
        db?.execSQL(createMovieTable)

        // Create the Cinema table
        val createCinemaTable = """
            CREATE TABLE $CINEMA_TABLE_NAME (
                $COLUMN_CINEMA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CINEMA_NAME TEXT,
                $COLUMN_CINEMA_TOWNSHIP TEXT,
                $COLUMN_CINEMA_FULL_ADDRESS TEXT,
                $COLUMN_CINEMA_GOOGLE_MAP_URL TEXT,
                $COLUMN_CINEMA_IMAGE BLOB  -- BLOB for storing the image
            )
        """.trimIndent()
        db?.execSQL(createCinemaTable)

        // Create the Theatre table with a foreign key to Cinema
        val createTheatreTable = """
            CREATE TABLE $THEATRE_TABLE_NAME (
                $COLUMN_THEATRE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_THEATRE_NAME TEXT,
                $COLUMN_THEATRE_TYPE TEXT,
                $COLUMN_THEATRE_IMAGE BLOB,
                $COLUMN_CINEMA_ID_FK INTEGER,
                FOREIGN KEY($COLUMN_CINEMA_ID_FK) REFERENCES $CINEMA_TABLE_NAME($COLUMN_CINEMA_ID)
            )
        """.trimIndent()
        db?.execSQL(createTheatreTable)

        // Create the Showtime table
        val createShowtimeTable = """
            CREATE TABLE $SHOWTIME_TABLE_NAME (
                $COLUMN_SHOWTIME_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SHOWTIME_DATE TEXT,
                $COLUMN_SHOWTIME_TIME TEXT,  -- New column for the showtime time
                $COLUMN_MOVIE_ID_FK INTEGER,
                $COLUMN_THEATRE_ID_FK INTEGER,
                FOREIGN KEY($COLUMN_MOVIE_ID_FK) REFERENCES $MOVIE_TABLE_NAME($COLUMN_MOVIE_ID),
                FOREIGN KEY($COLUMN_THEATRE_ID_FK) REFERENCES $THEATRE_TABLE_NAME($COLUMN_THEATRE_ID)
            )
        """.trimIndent()
        db?.execSQL(createShowtimeTable)

//        seat type
        val createSeatTypeTable = """
    CREATE TABLE $SEAT_TYPE_TABLE_NAME (
        $COLUMN_SEAT_TYPE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COLUMN_SEAT_TYPE_NAME TEXT NOT NULL UNIQUE,
        $COLUMN_SEAT_PRICE REAL NOT NULL
    )
""".trimIndent()
        db?.execSQL(createSeatTypeTable)

//        seat
        val createSeatTable = """
    CREATE TABLE $SEAT_TABLE_NAME (
        $COLUMN_SEAT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COLUMN_SEAT_LABEL TEXT NOT NULL,
        $COLUMN_THEATRE_ID_FK INTEGER NOT NULL,
        $COLUMN_SEAT_TYPE_ID_FK INTEGER NOT NULL,
        UNIQUE($COLUMN_SEAT_LABEL, $COLUMN_THEATRE_ID_FK),
        FOREIGN KEY($COLUMN_THEATRE_ID_FK) REFERENCES $THEATRE_TABLE_NAME($COLUMN_THEATRE_ID),
        FOREIGN KEY($COLUMN_SEAT_TYPE_ID_FK) REFERENCES $SEAT_TYPE_TABLE_NAME($COLUMN_SEAT_TYPE_ID)
    )
""".trimIndent()
        db?.execSQL(createSeatTable)

        val createBookingTable = """
    CREATE TABLE $BOOKING_TABLE_NAME (
        $COLUMN_BOOKING_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COLUMN_BOOKING_TIME TEXT,
        $COLUMN_NAME TEXT,
        $COLUMN_PHONE_NUMBER TEXT,
        $COLUMN_SHOWTIME_ID_FK INTEGER,
        $COLUMN_USER_ID_FK INTEGER NULL,
        $COLUMN_BOOKING_STATUS TEXT NOT NULL DEFAULT 'ACTIVE',
        $COLUMN_CANCELED_AT TEXT NULL,
        $COLUMN_RESERVATION_TYPE TEXT NOT NULL DEFAULT 'BOOK',
        $COLUMN_PAYMENT_METHOD TEXT NULL,
        $COLUMN_PAYMENT_SCREENSHOT BLOB NULL,
        $COLUMN_PAYMENT_REVIEW_STATUS TEXT NOT NULL DEFAULT 'NONE',
        FOREIGN KEY($COLUMN_SHOWTIME_ID_FK) REFERENCES $SHOWTIME_TABLE_NAME($COLUMN_SHOWTIME_ID),
        FOREIGN KEY($COLUMN_USER_ID_FK) REFERENCES $USER_TABLE_NAME($COLUMN_USER_ID)
    )
""".trimIndent()
        db?.execSQL(createBookingTable)

        val createBookingSeatTable = """
    CREATE TABLE $BOOKING_SEAT_TABLE_NAME (
        $COLUMN_BOOKING_SEAT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COLUMN_BOOKING_ID_FK INTEGER NOT NULL,
        $COLUMN_SEAT_ID_FK INTEGER NOT NULL,
        UNIQUE($COLUMN_BOOKING_ID_FK, $COLUMN_SEAT_ID_FK),
        FOREIGN KEY($COLUMN_BOOKING_ID_FK) REFERENCES $BOOKING_TABLE_NAME($COLUMN_BOOKING_ID),
        FOREIGN KEY($COLUMN_SEAT_ID_FK) REFERENCES $SEAT_TABLE_NAME($COLUMN_SEAT_ID)
    )
""".trimIndent()
        db?.execSQL(createBookingSeatTable)

        fun hashPassword(password: String): String {
            val messageDigest = java.security.MessageDigest.getInstance("SHA-256")
            val hashedBytes = messageDigest.digest(password.toByteArray())
            return hashedBytes.joinToString("") { "%02x".format(it) }
        }

        // Insert default roles
        db?.execSQL("""
    INSERT INTO $ROLE_TABLE_NAME ($COLUMN_ROLE_ID, $COLUMN_ROLE_NAME)
    VALUES (1, 'customer'), (2, 'admin')
""".trimIndent())

// Insert predefined customer account
        val customerPasswordHash = hashPassword("123")
        db?.execSQL("""
    INSERT INTO $USER_TABLE_NAME (
        $COLUMN_USERNAME,
        $COLUMN_EMAIL,
        $COLUMN_PASSWORD_HASH,
        $COLUMN_ROLE_ID_FK
    ) VALUES (
        'zwe paing htet',
        'hahaheehee23@gmail.com',
        '$customerPasswordHash',
        1
    )
""".trimIndent())

// Insert predefined admin account
        val adminPasswordHash = hashPassword("123admin")
        db?.execSQL("""
    INSERT INTO $USER_TABLE_NAME (
        $COLUMN_USERNAME,
        $COLUMN_EMAIL,
        $COLUMN_PASSWORD_HASH,
        $COLUMN_ROLE_ID_FK
    ) VALUES (
        'admin',
        'admin@jcinplex',
        '$adminPasswordHash',
        2
    )
""".trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $BOOKING_SEAT_TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $BOOKING_TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $SHOWTIME_TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $SEAT_TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $SEAT_TYPE_TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $THEATRE_TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $CINEMA_TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $MOVIE_TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $USER_TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $ROLE_TABLE_NAME")
        onCreate(db)
    }

//    seat type
data class SeatType(
    val id: Long,
    val name: String,
    val price: Double
)

    fun insertSeatType(seatTypeName: String, seatPrice: Double): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SEAT_TYPE_NAME, seatTypeName)
            put(COLUMN_SEAT_PRICE, seatPrice)
        }
        return db.insert(SEAT_TYPE_TABLE_NAME, null, values)
    }

    fun getAllSeatTypes(): List<SeatType> {
        val seatTypeList = mutableListOf<SeatType>()
        val db = readableDatabase

        val cursor = db.query(
            SEAT_TYPE_TABLE_NAME,
            arrayOf(COLUMN_SEAT_TYPE_ID, COLUMN_SEAT_TYPE_NAME, COLUMN_SEAT_PRICE),
            null, null, null, null,
            "$COLUMN_SEAT_TYPE_ID DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                val seatType = SeatType(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SEAT_TYPE_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEAT_TYPE_NAME)),
                    price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SEAT_PRICE))
                )
                seatTypeList.add(seatType)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return seatTypeList
    }

    fun updateSeatType(seatTypeId: Long, newName: String, newPrice: Double): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SEAT_TYPE_NAME, newName)
            put(COLUMN_SEAT_PRICE, newPrice)
        }

        val result = db.update(
            SEAT_TYPE_TABLE_NAME,
            values,
            "$COLUMN_SEAT_TYPE_ID = ?",
            arrayOf(seatTypeId.toString())
        )

        db.close()
        return result > 0
    }

    fun deleteSeatType(seatTypeId: Long): Boolean {
        val db = writableDatabase
        val result = db.delete(
            SEAT_TYPE_TABLE_NAME,
            "$COLUMN_SEAT_TYPE_ID = ?",
            arrayOf(seatTypeId.toString())
        )
        db.close()
        return result > 0
    }

//    seat
data class Seat(
    val id: Long,
    val seatLabel: String,
    val theatreId: Long,
    val seatTypeId: Long
)

    fun insertSeat(seatLabel: String, theatreId: Long, seatTypeId: Long): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SEAT_LABEL, seatLabel)
            put(COLUMN_THEATRE_ID_FK, theatreId)
            put(COLUMN_SEAT_TYPE_ID_FK, seatTypeId)
        }
        return db.insert(SEAT_TABLE_NAME, null, values)
    }

    fun isSeatRowUsedInTheatre(theatreId: Long, seatRow: String): Boolean {
        val db = readableDatabase
        val query = """
        SELECT COUNT(*) FROM $SEAT_TABLE_NAME
        WHERE $COLUMN_THEATRE_ID_FK = ?
        AND SUBSTR($COLUMN_SEAT_LABEL, 1, 1) = ?
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(theatreId.toString(), seatRow))
        var exists = false

        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0
        }

        cursor.close()
        db.close()
        return exists
    }

    fun getAllSeats(): List<Seat> {
        val seatList = mutableListOf<Seat>()
        val db = readableDatabase

        val cursor = db.query(
            SEAT_TABLE_NAME,
            arrayOf(COLUMN_SEAT_ID, COLUMN_SEAT_LABEL, COLUMN_THEATRE_ID_FK, COLUMN_SEAT_TYPE_ID_FK),
            null, null, null, null,
            "$COLUMN_SEAT_ID DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                val seat = Seat(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SEAT_ID)),
                    seatLabel = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEAT_LABEL)),
                    theatreId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_THEATRE_ID_FK)),
                    seatTypeId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SEAT_TYPE_ID_FK))
                )
                seatList.add(seat)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return seatList
    }

    fun updateSeat(seatId: Long, newSeatLabel: String, newTheatreId: Long, newSeatTypeId: Long): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SEAT_LABEL, newSeatLabel)
            put(COLUMN_THEATRE_ID_FK, newTheatreId)
            put(COLUMN_SEAT_TYPE_ID_FK, newSeatTypeId)
        }

        val result = db.update(
            SEAT_TABLE_NAME,
            values,
            "$COLUMN_SEAT_ID = ?",
            arrayOf(seatId.toString())
        )

        db.close()
        return result > 0
    }

    fun deleteSeat(seatId: Long): Boolean {
        val db = writableDatabase
        val result = db.delete(
            SEAT_TABLE_NAME,
            "$COLUMN_SEAT_ID = ?",
            arrayOf(seatId.toString())
        )
        db.close()
        return result > 0
    }

    fun getSeatTypeById(seatTypeId: Long): SeatType? {
        val db = readableDatabase
        val cursor = db.query(
            SEAT_TYPE_TABLE_NAME,
            arrayOf(COLUMN_SEAT_TYPE_ID, COLUMN_SEAT_TYPE_NAME, COLUMN_SEAT_PRICE),
            "$COLUMN_SEAT_TYPE_ID = ?",
            arrayOf(seatTypeId.toString()),
            null, null, null
        )

        var seatType: SeatType? = null

        if (cursor.moveToFirst()) {
            seatType = SeatType(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SEAT_TYPE_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEAT_TYPE_NAME)),
                price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SEAT_PRICE))
            )
        }

        cursor.close()
        db.close()
        return seatType
    }

    fun getTheatresWithSeats(): List<Theatre> {
        val theatreList = mutableListOf<Theatre>()
        val db = readableDatabase

        val query = """
        SELECT DISTINCT t.*
        FROM $THEATRE_TABLE_NAME t
        INNER JOIN $SEAT_TABLE_NAME s
        ON t.$COLUMN_THEATRE_ID = s.$COLUMN_THEATRE_ID_FK
        ORDER BY t.$COLUMN_THEATRE_NAME ASC
    """.trimIndent()

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val theatre = Theatre(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_THEATRE_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_THEATRE_NAME)),
                    type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_THEATRE_TYPE)),
                    image = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_THEATRE_IMAGE)),
                    cinemaId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CINEMA_ID_FK))
                )
                theatreList.add(theatre)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return theatreList
    }

    fun getSeatsByTheatreId(theatreId: Long): List<Seat> {
        val seatList = mutableListOf<Seat>()
        val db = readableDatabase

        val cursor = db.query(
            SEAT_TABLE_NAME,
            arrayOf(COLUMN_SEAT_ID, COLUMN_SEAT_LABEL, COLUMN_THEATRE_ID_FK, COLUMN_SEAT_TYPE_ID_FK),
            "$COLUMN_THEATRE_ID_FK = ?",
            arrayOf(theatreId.toString()),
            null, null,
            "$COLUMN_SEAT_TYPE_ID_FK ASC, $COLUMN_SEAT_LABEL ASC"
        )

        if (cursor.moveToFirst()) {
            do {
                val seat = Seat(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SEAT_ID)),
                    seatLabel = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEAT_LABEL)),
                    theatreId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_THEATRE_ID_FK)),
                    seatTypeId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SEAT_TYPE_ID_FK))
                )
                seatList.add(seat)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return seatList
    }

//    seat row
    fun insertSeatRow(seatRow: String, seatCount: Int, theatreId: Long, seatTypeId: Long): Boolean {
        val db = writableDatabase

        db.beginTransaction()
        return try {
            for (i in 1..seatCount) {
                val seatLabel = "$seatRow$i"
                val values = ContentValues().apply {
                    put(COLUMN_SEAT_LABEL, seatLabel)
                    put(COLUMN_THEATRE_ID_FK, theatreId)
                    put(COLUMN_SEAT_TYPE_ID_FK, seatTypeId)
                }

                val result = db.insert(SEAT_TABLE_NAME, null, values)
                if (result == -1L) {
                    throw Exception("Failed to insert seat $seatLabel")
                }
            }

            db.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            false
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun deleteSeatRow(theatreId: Long, seatRow: String): Boolean {
        val db = writableDatabase

        val result = db.delete(
            SEAT_TABLE_NAME,
            "$COLUMN_THEATRE_ID_FK = ? AND SUBSTR($COLUMN_SEAT_LABEL, 1, 1) = ?",
            arrayOf(theatreId.toString(), seatRow)
        )

        db.close()
        return result > 0
    }

    fun getSeatCountByTheatreId(theatreId: Long): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM $SEAT_TABLE_NAME WHERE $COLUMN_THEATRE_ID_FK = ?",
            arrayOf(theatreId.toString())
        )

        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        db.close()
        return count
    }

//    login pagw
    fun isUsernameTaken(username: String): Boolean {
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM $USER_TABLE_NAME WHERE $COLUMN_USERNAME = ?"
        val cursor = db.rawQuery(query, arrayOf(username))
        cursor?.moveToFirst()
        val count = cursor?.getInt(0) ?: 0
        cursor?.close()

        return count > 0
    }

    fun isEmailTaken(email: String): Boolean {
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM $USER_TABLE_NAME WHERE $COLUMN_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(email))

        cursor?.moveToFirst()
        val count = cursor?.getInt(0) ?: 0
        cursor?.close()

        return count > 0
    }

    fun registerUser(username: String, email: String, passwordHash: String) {
        val db = writableDatabase
        val query = """
        INSERT INTO $USER_TABLE_NAME ($COLUMN_USERNAME, $COLUMN_EMAIL, $COLUMN_PASSWORD_HASH, $COLUMN_ROLE_ID_FK) 
        VALUES (?, ?, ?, 1)  -- Default role_id = 1 (customer)
    """
        val stmt = db.compileStatement(query)
        stmt.bindString(1, username)
        stmt.bindString(2, email)
        stmt.bindString(3, passwordHash)

        stmt.executeInsert()
    }

    fun registerAdmin(username: String, email: String, passwordHash: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD_HASH, passwordHash)
            put(COLUMN_ROLE_ID_FK, 2) // Admin role
        }

        return db.insert(USER_TABLE_NAME, null, values)
    }

    fun getAllAdmins(): List<User> {
        val adminList = mutableListOf<User>()
        val db = this.readableDatabase

        val query = """
        SELECT * FROM $USER_TABLE_NAME
        WHERE $COLUMN_ROLE_ID_FK = 2
        ORDER BY $COLUMN_USER_ID DESC
    """.trimIndent()

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val user = User(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                    username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                    passwordHash = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD_HASH)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    roleId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ROLE_ID_FK))
                )
                adminList.add(user)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return adminList
    }

    data class User(
        val id: Long,           // User ID
        val username: String,   // Username of the user
        val passwordHash: String, // Hashed password
        val email: String,      // Email address of the user
        val roleId: Long        // Foreign key linking to the role table (customer/admin)
    )

    fun getUserByUsername(username: String): User? {
        val db = readableDatabase
        val query = "SELECT * FROM $USER_TABLE_NAME WHERE $COLUMN_USERNAME = ?"
        val cursor = db.rawQuery(query, arrayOf(username))

        if (cursor != null && cursor.moveToFirst()) {
            val userId = cursor.getLong(cursor.getColumnIndex(COLUMN_USER_ID))
            val userUsername = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME))
            val passwordHash = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD_HASH))
            val email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL))
            val roleId = cursor.getLong(cursor.getColumnIndex(COLUMN_ROLE_ID_FK))

            cursor.close()
            return User(userId, userUsername, passwordHash, email, roleId)
        }

        cursor?.close()
        return null
    }

    fun getUserByUsernameOrEmail(loginInput: String): User? {
        val db = readableDatabase
        val query = """
        SELECT * FROM $USER_TABLE_NAME
        WHERE $COLUMN_USERNAME = ? OR $COLUMN_EMAIL = ?
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(loginInput, loginInput))

        if (cursor.moveToFirst()) {
            val userId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
            val userUsername = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
            val passwordHash = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD_HASH))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
            val roleId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ROLE_ID_FK))

            cursor.close()
            return User(userId, userUsername, passwordHash, email, roleId)
        }

        cursor.close()
        return null
    }

    fun getAllCustomers(): List<User> {
        val customerList = mutableListOf<User>()
        val db = this.readableDatabase

        val query = """
        SELECT * FROM $USER_TABLE_NAME
        WHERE $COLUMN_ROLE_ID_FK = 1
        ORDER BY $COLUMN_USER_ID DESC
    """.trimIndent()

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val user = User(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                    username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                    passwordHash = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD_HASH)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    roleId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ROLE_ID_FK))
                )
                customerList.add(user)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return customerList
    }

    fun getUserById(userId: Long): User? {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $USER_TABLE_NAME WHERE $COLUMN_USER_ID = ?",
            arrayOf(userId.toString())
        )

        var user: User? = null

        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                passwordHash = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD_HASH)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                roleId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ROLE_ID_FK))
            )
        }

        cursor.close()
        db.close()
        return user
    }

    fun updateUserProfile(userId: Long, newUsername: String, newEmail: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, newUsername)
            put(COLUMN_EMAIL, newEmail)
        }

        val result = db.update(
            USER_TABLE_NAME,
            values,
            "$COLUMN_USER_ID = ?",
            arrayOf(userId.toString())
        )

        db.close()
        return result > 0
    }

    fun deleteUserAccount(userId: Long): Boolean {
        val db = this.writableDatabase
        val result = db.delete(
            USER_TABLE_NAME,
            "$COLUMN_USER_ID = ?",
            arrayOf(userId.toString())
        )
        db.close()
        return result > 0
    }

    data class Booking(
        val id: Long,
        val bookingTime: String,
        val showtimeId: Long,
        val customerName: String,
        val customerPhone: String,
        val userId: Long? = null,
        val bookingStatus: String = "ACTIVE",
        val canceledAt: String? = null,
        val reservationType: String = "BOOK",
        val paymentMethod: String? = null,
        val paymentScreenshot: ByteArray? = null,
        val paymentReviewStatus: String = "NONE"
    )

    fun insertBooking(
        showtimeId: Long,
        bookingTime: String,
        name: String,
        phoneNumber: String,
        userId: Long?,
        reservationType: String,
        paymentMethod: String? = null,
        paymentScreenshot: ByteArray? = null,
        paymentReviewStatus: String = "NONE"
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_BOOKING_TIME, bookingTime)
            put(COLUMN_NAME, name)
            put(COLUMN_PHONE_NUMBER, phoneNumber)
            put(COLUMN_SHOWTIME_ID_FK, showtimeId)
            put(COLUMN_RESERVATION_TYPE, reservationType)
            put(COLUMN_PAYMENT_REVIEW_STATUS, paymentReviewStatus)

            if (userId != null && userId != -1L) {
                put(COLUMN_USER_ID_FK, userId)
            } else {
                putNull(COLUMN_USER_ID_FK)
            }

            if (paymentMethod != null) {
                put(COLUMN_PAYMENT_METHOD, paymentMethod)
            } else {
                putNull(COLUMN_PAYMENT_METHOD)
            }

            if (paymentScreenshot != null) {
                put(COLUMN_PAYMENT_SCREENSHOT, paymentScreenshot)
            } else {
                putNull(COLUMN_PAYMENT_SCREENSHOT)
            }
        }

        return db.insert(BOOKING_TABLE_NAME, null, values)
    }

    fun getAllBookings(): List<Booking> {
        val db = readableDatabase
        val bookingList = mutableListOf<Booking>()

        val cursor = db.query(
            BOOKING_TABLE_NAME,
            arrayOf(
                COLUMN_BOOKING_ID,
                COLUMN_BOOKING_TIME,
                COLUMN_NAME,
                COLUMN_PHONE_NUMBER,
                COLUMN_SHOWTIME_ID_FK,
                COLUMN_USER_ID_FK,
                COLUMN_BOOKING_STATUS,
                COLUMN_CANCELED_AT,
                COLUMN_RESERVATION_TYPE,
                COLUMN_PAYMENT_METHOD,
                COLUMN_PAYMENT_SCREENSHOT,
                COLUMN_PAYMENT_REVIEW_STATUS
            ),
            null, null, null, null, null
        )

        if (cursor.moveToFirst()) {
            do {
                val bookingId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_ID))
                val bookingTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_TIME))
                val customerName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val customerPhone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE_NUMBER))
                val showtimeId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SHOWTIME_ID_FK))

                val userIdColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_ID_FK)
                val userId = if (cursor.isNull(userIdColumnIndex)) null else cursor.getLong(userIdColumnIndex)

                val bookingStatus = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_STATUS))

                val canceledAtIndex = cursor.getColumnIndexOrThrow(COLUMN_CANCELED_AT)
                val canceledAt = if (cursor.isNull(canceledAtIndex)) null else cursor.getString(canceledAtIndex)

                val reservationType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESERVATION_TYPE))

                val paymentMethodIndex = cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_METHOD)
                val paymentMethod = if (cursor.isNull(paymentMethodIndex)) null else cursor.getString(paymentMethodIndex)

                val paymentScreenshotIndex = cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_SCREENSHOT)
                val paymentScreenshot = if (cursor.isNull(paymentScreenshotIndex)) null else cursor.getBlob(paymentScreenshotIndex)

                val paymentReviewStatus =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_REVIEW_STATUS))

                val booking = Booking(
                    bookingId,
                    bookingTime,
                    showtimeId,
                    customerName,
                    customerPhone,
                    userId,
                    bookingStatus,
                    canceledAt,
                    reservationType,
                    paymentMethod,
                    paymentScreenshot,
                    paymentReviewStatus
                )

                bookingList.add(booking)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return bookingList
    }

    // Showtime data Class
    data class Showtime(
        val id: Long,
        val showtimeDate: String,
        val showtimeTime: String, // New field for showtime time
        val movieId: Long,       // Movie ID
        val theatreId: Long      // Theatre ID
    )

    // Insert Showtime Method
    fun insertShowtime(showtimeDate: String, showtimeTime: String, theatreId: Long, movieId: Long): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SHOWTIME_DATE, showtimeDate)
            put(COLUMN_SHOWTIME_TIME, showtimeTime) // Insert the showtime time
            put(COLUMN_MOVIE_ID_FK, movieId) // Store movie ID
            put(COLUMN_THEATRE_ID_FK, theatreId)
        }
        return db.insert(SHOWTIME_TABLE_NAME, null, values)
    }

    // Fetch all showtimes by Movie ID
    fun getShowtimesByMovieId(movieId: Long): MutableList<Showtime> {
        val db = readableDatabase
        val showtimeList = mutableListOf<Showtime>()

        val cursor = db.query(
            SHOWTIME_TABLE_NAME,
            arrayOf(COLUMN_SHOWTIME_ID, COLUMN_SHOWTIME_DATE, COLUMN_SHOWTIME_TIME, COLUMN_MOVIE_ID_FK, COLUMN_THEATRE_ID_FK),
            "$COLUMN_MOVIE_ID_FK = ?",
            arrayOf(movieId.toString()), null, null, null
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val movieId = cursor.getLong(cursor.getColumnIndex(COLUMN_MOVIE_ID_FK))
                val theatreId = cursor.getLong(cursor.getColumnIndex(COLUMN_THEATRE_ID_FK))

                // Save the showtime with movieId, theatreId, and showtimeTime
                val showtime = Showtime(
                    cursor.getLong(cursor.getColumnIndex(COLUMN_SHOWTIME_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_SHOWTIME_DATE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_SHOWTIME_TIME)), // Fetch showtime time
                    movieId,   // Store movie ID
                    theatreId  // Store theatre ID
                )
                showtimeList.add(showtime)
            } while (cursor.moveToNext())
            cursor.close()
        }
        return showtimeList
    }

    // Get All Showtimes Method
    fun getAllShowtimes(): MutableList<Showtime> {
        val db = readableDatabase
        val showtimeList = mutableListOf<Showtime>()

        val cursor = db.query(
            SHOWTIME_TABLE_NAME,
            arrayOf(COLUMN_SHOWTIME_ID, COLUMN_SHOWTIME_DATE, COLUMN_SHOWTIME_TIME, COLUMN_MOVIE_ID_FK, COLUMN_THEATRE_ID_FK),
            null, null, null, null, null
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val movieId = cursor.getLong(cursor.getColumnIndex(COLUMN_MOVIE_ID_FK))
                val theatreId = cursor.getLong(cursor.getColumnIndex(COLUMN_THEATRE_ID_FK))

                val movie = getMovieById(movieId)
                val theatre = getTheatreById(theatreId)

                val showtime = Showtime(
                    cursor.getLong(cursor.getColumnIndex(COLUMN_SHOWTIME_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_SHOWTIME_DATE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_SHOWTIME_TIME)), // Fetch showtime time
                    movieId,  // Store movieId
                    theatreId  // Store theatreId
                )

                showtimeList.add(showtime)
            } while (cursor.moveToNext())
            cursor.close()
        }
        return showtimeList
    }

    // Get Showtime by ID Method
    fun getShowtimeById(showtimeId: Long): Showtime? {
        val db = readableDatabase
        val cursor = db.query(
            SHOWTIME_TABLE_NAME,
            arrayOf(COLUMN_SHOWTIME_ID, COLUMN_SHOWTIME_DATE, COLUMN_SHOWTIME_TIME, COLUMN_MOVIE_ID_FK, COLUMN_THEATRE_ID_FK),
            "$COLUMN_SHOWTIME_ID = ?",
            arrayOf(showtimeId.toString()), null, null, null
        )

        var showtime: Showtime? = null
        if (cursor != null && cursor.moveToFirst()) {
            val movie = getMovieById(cursor.getLong(cursor.getColumnIndex(COLUMN_MOVIE_ID_FK)))
            val theatre = getTheatreById(cursor.getLong(cursor.getColumnIndex(COLUMN_THEATRE_ID_FK)))

            showtime = Showtime(
                cursor.getLong(cursor.getColumnIndex(COLUMN_SHOWTIME_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_SHOWTIME_DATE)),
                cursor.getString(cursor.getColumnIndex(COLUMN_SHOWTIME_TIME)), // Fetch showtime time
                movie?.id ?: -1,  // Use movie.id (Long) instead of movie.name (String)
                theatre?.id ?: -1  // Use theatre.id (Long) instead of theatre.name (String)
            )
            cursor.close()
        }
        return showtime
    }

    fun updateShowtime(showtimeId: Long, date: String, time: String, theatreId: Long): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SHOWTIME_DATE, date)
            put(COLUMN_SHOWTIME_TIME, time)
            put(COLUMN_THEATRE_ID_FK, theatreId)
        }
        return db.update(SHOWTIME_TABLE_NAME, values, "$COLUMN_SHOWTIME_ID = ?", arrayOf(showtimeId.toString()))
    }

    // Delete Showtime Method
    fun deleteShowtime(showtimeId: Long): Int {
        val db = writableDatabase
        return db.delete(
            SHOWTIME_TABLE_NAME,
            "$COLUMN_SHOWTIME_ID = ?",
            arrayOf(showtimeId.toString())
        )
    }
// movie
    data class Movie(
        val id: Long,
        val name: String,
        val runtime: Int,
        val genre: String,
        val director: String?,
        val cast: String?,
        val description: String?,
        val trailerUrl: String?,
        val status: String?,
        val posterImage: ByteArray?,
        val coverPosterImage: ByteArray?
    )

    // Insert Movie Method (Now including the movie poster)
    fun insertMovie(
        movieName: String,
        runtime: Int,
        genre: String,
        director: String?,
        cast: String?,
        description: String?,
        trailerUrl: String?,
        status: String?,
        posterImage: ByteArray?,
        coverPosterImage: ByteArray?
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MOVIE_NAME, movieName)
            put(COLUMN_MOVIE_RUNTIME, runtime)
            put(COLUMN_MOVIE_GENRE, genre)
            put(COLUMN_MOVIE_DIRECTOR, director)
            put(COLUMN_MOVIE_CAST, cast)
            put(COLUMN_MOVIE_DESCRIPTION, description)
            put(COLUMN_MOVIE_TRAILER, trailerUrl)
            put(COLUMN_MOVIE_STATUS, status)
            put(COLUMN_MOVIE_POSTER, posterImage)
            put(COLUMN_MOVIE_COVER_POSTER, coverPosterImage)
        }
        return db.insert(MOVIE_TABLE_NAME, null, values)
    }

    fun getAllMovies(): MutableList<Movie> {
        val db = readableDatabase
        val movieList = mutableListOf<Movie>()

        val cursor = db.query(
            MOVIE_TABLE_NAME,
            arrayOf(
                COLUMN_MOVIE_ID,
                COLUMN_MOVIE_NAME,
                COLUMN_MOVIE_RUNTIME,
                COLUMN_MOVIE_GENRE,
                COLUMN_MOVIE_DIRECTOR,
                COLUMN_MOVIE_CAST,
                COLUMN_MOVIE_DESCRIPTION,
                COLUMN_MOVIE_TRAILER,
                COLUMN_MOVIE_STATUS,
                COLUMN_MOVIE_POSTER,
                COLUMN_MOVIE_COVER_POSTER
            ),
            null, null, null, null, null
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val movie = Movie(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_RUNTIME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_GENRE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_DIRECTOR)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_CAST)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_TRAILER)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_STATUS)),
                    cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_POSTER)),
                    cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_COVER_POSTER))
                )
                movieList.add(movie)
            } while (cursor.moveToNext())
            cursor.close()
        }

        return movieList
    }

//    // Get Movie by ID (Now including the movie poster)

    fun getMovieById(movieId: Long): Movie? {
        val db = readableDatabase
        val cursor = db.query(
            MOVIE_TABLE_NAME,
            arrayOf(
                COLUMN_MOVIE_ID,
                COLUMN_MOVIE_NAME,
                COLUMN_MOVIE_RUNTIME,
                COLUMN_MOVIE_GENRE,
                COLUMN_MOVIE_DIRECTOR,
                COLUMN_MOVIE_CAST,
                COLUMN_MOVIE_DESCRIPTION,
                COLUMN_MOVIE_TRAILER,
                COLUMN_MOVIE_STATUS,
                COLUMN_MOVIE_POSTER,
                COLUMN_MOVIE_COVER_POSTER
            ),
            "$COLUMN_MOVIE_ID = ?",
            arrayOf(movieId.toString()),
            null,
            null,
            null
        )

        var movie: Movie? = null
        if (cursor != null && cursor.moveToFirst()) {
            movie = Movie(
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_NAME)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_RUNTIME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_GENRE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_DIRECTOR)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_CAST)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_TRAILER)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_STATUS)),
                cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_POSTER)),
                cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_COVER_POSTER))
            )
            cursor.close()
        }

        return movie
    }

    // Delete Movie Method
    fun deleteMovie(movieId: Long): Int {
        val db = writableDatabase
        return db.delete(
            MOVIE_TABLE_NAME,
            "$COLUMN_MOVIE_ID = ?",
            arrayOf(movieId.toString())
        )
    }

    fun updateMovie(
        movieId: Long,
        newMovieName: String,
        newRuntime: Int,
        newGenre: String,
        newDirector: String?,
        newCast: String?,
        newDescription: String?,
        trailerUrl: String?,
        status: String?,
        posterImage: ByteArray?,
        coverPosterImage: ByteArray?
    ): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MOVIE_NAME, newMovieName)
            put(COLUMN_MOVIE_RUNTIME, newRuntime)
            put(COLUMN_MOVIE_GENRE, newGenre)
            put(COLUMN_MOVIE_DIRECTOR, newDirector)
            put(COLUMN_MOVIE_CAST, newCast)
            put(COLUMN_MOVIE_DESCRIPTION, newDescription)
            put(COLUMN_MOVIE_TRAILER, trailerUrl)
            put(COLUMN_MOVIE_STATUS, status)
            put(COLUMN_MOVIE_POSTER, posterImage)
            put(COLUMN_MOVIE_COVER_POSTER, coverPosterImage)
        }
        return db.update(
            MOVIE_TABLE_NAME,
            values,
            "$COLUMN_MOVIE_ID = ?",
            arrayOf(movieId.toString())
        )
    }

    fun getMoviesForTheatre(theatreId: Long): MutableList<Movie> {
        val movieList = mutableListOf<Movie>()
        val db = readableDatabase

        val query = """
        SELECT m.$COLUMN_MOVIE_ID, 
               m.$COLUMN_MOVIE_NAME, 
               m.$COLUMN_MOVIE_RUNTIME, 
               m.$COLUMN_MOVIE_GENRE,
               m.$COLUMN_MOVIE_DIRECTOR,
               m.$COLUMN_MOVIE_CAST,
               m.$COLUMN_MOVIE_DESCRIPTION,
               m.$COLUMN_MOVIE_TRAILER, 
               m.$COLUMN_MOVIE_STATUS, 
               m.$COLUMN_MOVIE_POSTER,
               m.$COLUMN_MOVIE_COVER_POSTER
        FROM $SHOWTIME_TABLE_NAME s
        JOIN $MOVIE_TABLE_NAME m ON s.$COLUMN_MOVIE_ID_FK = m.$COLUMN_MOVIE_ID
        WHERE s.$COLUMN_THEATRE_ID_FK = ?
    """

        val cursor = db.rawQuery(query, arrayOf(theatreId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val movieId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_ID))
                val movieName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_NAME))
                val movieRuntime = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_RUNTIME))
                val movieGenre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_GENRE))
                val movieDirector = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_DIRECTOR))
                val movieCast = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_CAST))
                val movieDescription = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_DESCRIPTION))
                val movieTrailerUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_TRAILER))
                val movieStatus = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_STATUS))
                val moviePoster = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_POSTER))
                val movieCoverPoster = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_COVER_POSTER))

                movieList.add(
                    Movie(
                        id = movieId,
                        name = movieName,
                        runtime = movieRuntime,
                        genre = movieGenre,
                        director = movieDirector,
                        cast = movieCast,
                        description = movieDescription,
                        trailerUrl = movieTrailerUrl,
                        status = movieStatus,
                        posterImage = moviePoster,
                        coverPosterImage = movieCoverPoster
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return movieList.distinctBy { it.id }.toMutableList()
    }

    fun getAllMoviesSearch(): List<Movie> {
        val movieList = mutableListOf<Movie>()
        val db = readableDatabase

        val query = """
        SELECT $COLUMN_MOVIE_ID, 
               $COLUMN_MOVIE_NAME, 
               $COLUMN_MOVIE_RUNTIME, 
               $COLUMN_MOVIE_GENRE,
               $COLUMN_MOVIE_DIRECTOR,
               $COLUMN_MOVIE_CAST,
               $COLUMN_MOVIE_DESCRIPTION,
               $COLUMN_MOVIE_TRAILER, 
               $COLUMN_MOVIE_STATUS, 
               $COLUMN_MOVIE_POSTER,
               $COLUMN_MOVIE_COVER_POSTER
        FROM $MOVIE_TABLE_NAME
    """

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val movieId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_ID))
                val movieName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_NAME))
                val movieRuntime = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_RUNTIME))
                val movieGenre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_GENRE))
                val movieDirector = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_DIRECTOR))
                val movieCast = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_CAST))
                val movieDescription = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_DESCRIPTION))
                val movieTrailerUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_TRAILER))
                val movieStatus = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_STATUS))
                val moviePoster = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_POSTER))
                val movieCoverPoster = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_COVER_POSTER))

                movieList.add(
                    Movie(
                        id = movieId,
                        name = movieName,
                        runtime = movieRuntime,
                        genre = movieGenre,
                        director = movieDirector,
                        cast = movieCast,
                        description = movieDescription,
                        trailerUrl = movieTrailerUrl,
                        status = movieStatus,
                        posterImage = moviePoster,
                        coverPosterImage = movieCoverPoster
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return movieList
    }

    // Cinema data class
    data class Cinema(
        val id: Long,
        val name: String,
        val township: String,
        val fullAddress: String,
        val googleMapUrl: String,
        val image: ByteArray? // Store the cinema image as byte array (BLOB)
    )

    // Theatre Data Class
    data class Theatre(
        val id: Long,
        val name: String,
        val type: String,
        val image: ByteArray?,
        val cinemaId: Long
    )

    // Insert Cinema Method
    fun insertCinema(
        cinemaName: String,
        township: String,
        fullAddress: String,
        googleMapUrl: String,
        cinemaImage: ByteArray?
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CINEMA_NAME, cinemaName)
            put(COLUMN_CINEMA_TOWNSHIP, township)
            put(COLUMN_CINEMA_FULL_ADDRESS, fullAddress)
            put(COLUMN_CINEMA_GOOGLE_MAP_URL, googleMapUrl)
            put(COLUMN_CINEMA_IMAGE, cinemaImage)  // Store cinema image (image as byte array)
        }
        return db.insert(CINEMA_TABLE_NAME, null, values)
    }

    // Insert Theatre Method
    fun insertTheatre(
        theatreName: String,
        theatreType: String,
        theatreImage: ByteArray?,
        cinemaId: Long
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_THEATRE_NAME, theatreName)
            put(COLUMN_THEATRE_TYPE, theatreType)
            put(COLUMN_THEATRE_IMAGE, theatreImage)
            put(COLUMN_CINEMA_ID_FK, cinemaId)  // Link to Cinema
        }
        return db.insert(THEATRE_TABLE_NAME, null, values)
    }


    // Get All Cinemas Method
    fun getAllCinemas(): MutableList<Cinema> {
        val db = readableDatabase
        val cinemaList = mutableListOf<Cinema>()

        val cursor = db.query(
            CINEMA_TABLE_NAME,
            arrayOf(
                COLUMN_CINEMA_ID,
                COLUMN_CINEMA_NAME,
                COLUMN_CINEMA_TOWNSHIP,
                COLUMN_CINEMA_FULL_ADDRESS,
                COLUMN_CINEMA_GOOGLE_MAP_URL,
                COLUMN_CINEMA_IMAGE // Include the cinema image column
            ),
            null, null, null, null, null
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val cinema = Cinema(
                    cursor.getLong(cursor.getColumnIndex(COLUMN_CINEMA_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CINEMA_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CINEMA_TOWNSHIP)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CINEMA_FULL_ADDRESS)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CINEMA_GOOGLE_MAP_URL)),
                    cursor.getBlob(cursor.getColumnIndex(COLUMN_CINEMA_IMAGE)) // Get the image as a byte array (BLOB)
                )
                cinemaList.add(cinema)
            } while (cursor.moveToNext())
            cursor.close()
        }
        return cinemaList
    }

    // Get All Theatres Method
    fun getAllTheatres(): MutableList<Theatre> {
        val db = readableDatabase
        val theatreList = mutableListOf<Theatre>()

        val cursor = db.query(
            THEATRE_TABLE_NAME,
            arrayOf(COLUMN_THEATRE_ID, COLUMN_THEATRE_NAME, COLUMN_THEATRE_TYPE, COLUMN_THEATRE_IMAGE, COLUMN_CINEMA_ID_FK),
            null, null, null, null, null
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val theatre = Theatre(
                    cursor.getLong(cursor.getColumnIndex(COLUMN_THEATRE_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_THEATRE_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_THEATRE_TYPE)),
                    cursor.getBlob(cursor.getColumnIndex(COLUMN_THEATRE_IMAGE)),
                    cursor.getLong(cursor.getColumnIndex(COLUMN_CINEMA_ID_FK))  // Foreign key reference to cinema
                )
                theatreList.add(theatre)
            } while (cursor.moveToNext())
            cursor.close()
        }
        return theatreList
    }

    // Get Cinema by ID
    fun getCinemaById(cinemaId: Long): Cinema? {
        val db = readableDatabase
        val cursor = db.query(
            CINEMA_TABLE_NAME,
            arrayOf(COLUMN_CINEMA_ID, COLUMN_CINEMA_NAME, COLUMN_CINEMA_TOWNSHIP, COLUMN_CINEMA_FULL_ADDRESS, COLUMN_CINEMA_GOOGLE_MAP_URL, COLUMN_CINEMA_IMAGE),
            "$COLUMN_CINEMA_ID = ?",
            arrayOf(cinemaId.toString()), null, null, null
        )

        var cinema: Cinema? = null
        if (cursor != null && cursor.moveToFirst()) {
            cinema = Cinema(
                cursor.getLong(cursor.getColumnIndex(COLUMN_CINEMA_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CINEMA_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CINEMA_TOWNSHIP)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CINEMA_FULL_ADDRESS)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CINEMA_GOOGLE_MAP_URL)),
                cursor.getBlob(cursor.getColumnIndex(COLUMN_CINEMA_IMAGE))  // Get the image as byte array (BLOB)
            )
            cursor.close()
        }
        return cinema
    }

    // Get Theatre by ID
    fun getTheatreById(theatreId: Long): Theatre? {
        val db = readableDatabase
        val cursor = db.query(
            THEATRE_TABLE_NAME,
            arrayOf(COLUMN_THEATRE_ID, COLUMN_THEATRE_NAME, COLUMN_THEATRE_TYPE, COLUMN_THEATRE_IMAGE, COLUMN_CINEMA_ID_FK),
            "$COLUMN_THEATRE_ID = ?",
            arrayOf(theatreId.toString()), null, null, null
        )

        var theatre: Theatre? = null
        if (cursor != null && cursor.moveToFirst()) {
            theatre = Theatre(
                cursor.getLong(cursor.getColumnIndex(COLUMN_THEATRE_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_THEATRE_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_THEATRE_TYPE)),
                cursor.getBlob(cursor.getColumnIndex(COLUMN_THEATRE_IMAGE)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_CINEMA_ID_FK))
            )
            cursor.close()
        }
        return theatre
    }


    // Delete Cinema Method
    fun deleteCinema(cinemaId: Long): Int {
        val db = writableDatabase
        return db.delete(
            CINEMA_TABLE_NAME,
            "$COLUMN_CINEMA_ID = ?",
            arrayOf(cinemaId.toString())
        )
    }

    // Delete Theatre Method
    fun deleteTheatre(theatreId: Long): Int {
        val db = writableDatabase
        return db.delete(
            THEATRE_TABLE_NAME,
            "$COLUMN_THEATRE_ID = ?",
            arrayOf(theatreId.toString())
        )
    }

    // Update Cinema Method
    fun updateCinema(
        cinemaId: Long,
        newCinemaName: String,
        newTownship: String,
        newFullAddress: String,
        newGoogleMapUrl: String,
        cinemaImage: ByteArray? // Add the cinema image as a parameter
    ): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CINEMA_NAME, newCinemaName)
            put(COLUMN_CINEMA_TOWNSHIP, newTownship)
            put(COLUMN_CINEMA_FULL_ADDRESS, newFullAddress)
            put(COLUMN_CINEMA_GOOGLE_MAP_URL, newGoogleMapUrl)
            put(COLUMN_CINEMA_IMAGE, cinemaImage)  // Update the cinema image (image as byte array)
        }
        return db.update(CINEMA_TABLE_NAME, values, "$COLUMN_CINEMA_ID = ?", arrayOf(cinemaId.toString()))
    }

    // Update Theatre Method (modified)
    fun updateTheatre(
        theatreId: Long,
        theatreName: String,
        theatreType: String,
        theatreImage: ByteArray?
    ): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_THEATRE_NAME, theatreName)
            put(COLUMN_THEATRE_TYPE, theatreType)
            put(COLUMN_THEATRE_IMAGE, theatreImage)
        }
        return db.update(THEATRE_TABLE_NAME, values, "$COLUMN_THEATRE_ID = ?", arrayOf(theatreId.toString()))
    }

    // DatabaseHelper: Get all theatres for a specific cinema
    fun getTheatresByCinemaId(cinemaId: Long): MutableList<Theatre> {
        val db = readableDatabase
        val theatreList = mutableListOf<Theatre>()

        val cursor = db.query(
            THEATRE_TABLE_NAME,
            arrayOf(
                COLUMN_THEATRE_ID,
                COLUMN_THEATRE_NAME,
                COLUMN_THEATRE_TYPE,
                COLUMN_THEATRE_IMAGE,
                COLUMN_CINEMA_ID_FK // Foreign Key reference to Cinema
            ),
            "$COLUMN_CINEMA_ID_FK = ?", // Query to match the cinema_id
            arrayOf(cinemaId.toString()), null, null, null
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val theatre = Theatre(
                    cursor.getLong(cursor.getColumnIndex(COLUMN_THEATRE_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_THEATRE_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_THEATRE_TYPE)),
                    cursor.getBlob(cursor.getColumnIndex(COLUMN_THEATRE_IMAGE)), // Theatre Image
                    cursor.getLong(cursor.getColumnIndex(COLUMN_CINEMA_ID_FK)) // Cinema ID
                )
                theatreList.add(theatre)
            } while (cursor.moveToNext())
            cursor.close()
        }
        return theatreList
    }

//    showtime movie

    fun getShowtimesByTheatreAndMovie(theatreId: Long, movieId: Long): List<Showtime> {
        val db = readableDatabase
        val showtimeList = mutableListOf<Showtime>()

        val cursor = db.query(
            SHOWTIME_TABLE_NAME,
            arrayOf(COLUMN_SHOWTIME_ID, COLUMN_SHOWTIME_DATE, COLUMN_SHOWTIME_TIME, COLUMN_MOVIE_ID_FK, COLUMN_THEATRE_ID_FK),
            "$COLUMN_THEATRE_ID_FK = ? AND $COLUMN_MOVIE_ID_FK = ?",
            arrayOf(theatreId.toString(), movieId.toString()), null, null, null
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val showtime = Showtime(
                    cursor.getLong(cursor.getColumnIndex(COLUMN_SHOWTIME_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_SHOWTIME_DATE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_SHOWTIME_TIME)),
                    cursor.getLong(cursor.getColumnIndex(COLUMN_MOVIE_ID_FK)),
                    cursor.getLong(cursor.getColumnIndex(COLUMN_THEATRE_ID_FK))
                )
                showtimeList.add(showtime)
            } while (cursor.moveToNext())
            cursor.close()
        }
        return showtimeList
    }

    fun getShowtimesByMovieIdAndDate(movieId: Long, theatreId: Long, showtimeDate: String): List<Showtime> {
        val db = readableDatabase
        val showtimeList = mutableListOf<Showtime>()

        val cursor = db.query(
            SHOWTIME_TABLE_NAME,
            arrayOf(COLUMN_SHOWTIME_ID, COLUMN_SHOWTIME_DATE, COLUMN_SHOWTIME_TIME, COLUMN_MOVIE_ID_FK, COLUMN_THEATRE_ID_FK),
            "$COLUMN_MOVIE_ID_FK = ? AND $COLUMN_THEATRE_ID_FK = ? AND $COLUMN_SHOWTIME_DATE = ?",
            arrayOf(movieId.toString(), theatreId.toString(), showtimeDate),
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val showtime = Showtime(
                    cursor.getLong(cursor.getColumnIndex(COLUMN_SHOWTIME_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_SHOWTIME_DATE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_SHOWTIME_TIME)),
                    cursor.getLong(cursor.getColumnIndex(COLUMN_MOVIE_ID_FK)),
                    cursor.getLong(cursor.getColumnIndex(COLUMN_THEATRE_ID_FK))
                )
                showtimeList.add(showtime)
            } while (cursor.moveToNext())
            cursor.close()
        }

        return showtimeList
    }

    // booking seats

    fun insertBookingSeat(bookingId: Long, seatId: Long): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_BOOKING_ID_FK, bookingId)
            put(COLUMN_SEAT_ID_FK, seatId)
        }
        return db.insert(BOOKING_SEAT_TABLE_NAME, null, values)
    }

    fun getSeatIdsByBookingId(bookingId: Long): List<Long> {
        val seatIds = mutableListOf<Long>()
        val db = readableDatabase

        val cursor = db.query(
            BOOKING_SEAT_TABLE_NAME,
            arrayOf(COLUMN_SEAT_ID_FK),
            "$COLUMN_BOOKING_ID_FK = ?",
            arrayOf(bookingId.toString()),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            do {
                seatIds.add(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SEAT_ID_FK))
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return seatIds
    }

    fun getSeatById(seatId: Long): Seat? {
        val db = readableDatabase

        val cursor = db.query(
            SEAT_TABLE_NAME,
            arrayOf(COLUMN_SEAT_ID, COLUMN_SEAT_LABEL, COLUMN_THEATRE_ID_FK, COLUMN_SEAT_TYPE_ID_FK),
            "$COLUMN_SEAT_ID = ?",
            arrayOf(seatId.toString()),
            null, null, null
        )

        var seat: Seat? = null

        if (cursor.moveToFirst()) {
            seat = Seat(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SEAT_ID)),
                seatLabel = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEAT_LABEL)),
                theatreId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_THEATRE_ID_FK)),
                seatTypeId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SEAT_TYPE_ID_FK))
            )
        }

        cursor.close()
        db.close()
        return seat
    }

//    showtime bookign admin

    fun getShowtimesByTheatreId(theatreId: Long): List<Showtime> {
        val showtimeList = mutableListOf<Showtime>()
        val db = readableDatabase

        val cursor = db.query(
            SHOWTIME_TABLE_NAME,
            arrayOf(
                COLUMN_SHOWTIME_ID,
                COLUMN_SHOWTIME_DATE,
                COLUMN_SHOWTIME_TIME,
                COLUMN_MOVIE_ID_FK,
                COLUMN_THEATRE_ID_FK
            ),
            "$COLUMN_THEATRE_ID_FK = ?",
            arrayOf(theatreId.toString()),
            null, null,
            "$COLUMN_SHOWTIME_DATE ASC, $COLUMN_SHOWTIME_TIME ASC"
        )

        if (cursor.moveToFirst()) {
            do {
                val showtime = Showtime(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SHOWTIME_ID)),
                    showtimeDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SHOWTIME_DATE)),
                    showtimeTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SHOWTIME_TIME)),
                    movieId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_ID_FK)),
                    theatreId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_THEATRE_ID_FK))
                )
                showtimeList.add(showtime)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return showtimeList
    }

    fun getBookingsByShowtimeId(showtimeId: Long): List<Booking> {
        val db = readableDatabase
        val bookingList = mutableListOf<Booking>()

        val cursor = db.query(
            BOOKING_TABLE_NAME,
            arrayOf(
                COLUMN_BOOKING_ID,
                COLUMN_BOOKING_TIME,
                COLUMN_NAME,
                COLUMN_PHONE_NUMBER,
                COLUMN_SHOWTIME_ID_FK,
                COLUMN_USER_ID_FK,
                COLUMN_BOOKING_STATUS,
                COLUMN_CANCELED_AT,
                COLUMN_RESERVATION_TYPE,
                COLUMN_PAYMENT_METHOD,
                COLUMN_PAYMENT_SCREENSHOT,
                COLUMN_PAYMENT_REVIEW_STATUS
            ),
            "$COLUMN_SHOWTIME_ID_FK = ?",
            arrayOf(showtimeId.toString()),
            null,
            null,
            "$COLUMN_BOOKING_ID DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                val bookingId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_ID))
                val bookingTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_TIME))
                val customerName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val customerPhone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE_NUMBER))
                val bookingShowtimeId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SHOWTIME_ID_FK))

                val userIdColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_ID_FK)
                val bookingUserId =
                    if (cursor.isNull(userIdColumnIndex)) null else cursor.getLong(userIdColumnIndex)

                val bookingStatus = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_STATUS))

                val canceledAtIndex = cursor.getColumnIndexOrThrow(COLUMN_CANCELED_AT)
                val canceledAt =
                    if (cursor.isNull(canceledAtIndex)) null else cursor.getString(canceledAtIndex)

                val reservationType =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESERVATION_TYPE))

                val paymentMethodIndex = cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_METHOD)
                val paymentMethod =
                    if (cursor.isNull(paymentMethodIndex)) null else cursor.getString(paymentMethodIndex)

                val paymentScreenshotIndex = cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_SCREENSHOT)
                val paymentScreenshot =
                    if (cursor.isNull(paymentScreenshotIndex)) null else cursor.getBlob(paymentScreenshotIndex)

                val paymentReviewStatus =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_REVIEW_STATUS))

                val booking = Booking(
                    bookingId,
                    bookingTime,
                    bookingShowtimeId,
                    customerName,
                    customerPhone,
                    bookingUserId,
                    bookingStatus,
                    canceledAt,
                    reservationType,
                    paymentMethod,
                    paymentScreenshot,
                    paymentReviewStatus
                )

                bookingList.add(booking)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return bookingList
    }

    fun getPendingBuyBookings(): List<Booking> {
        val db = readableDatabase
        val bookingList = mutableListOf<Booking>()

        val cursor = db.query(
            BOOKING_TABLE_NAME,
            arrayOf(
                COLUMN_BOOKING_ID,
                COLUMN_BOOKING_TIME,
                COLUMN_NAME,
                COLUMN_PHONE_NUMBER,
                COLUMN_SHOWTIME_ID_FK,
                COLUMN_USER_ID_FK,
                COLUMN_BOOKING_STATUS,
                COLUMN_CANCELED_AT,
                COLUMN_RESERVATION_TYPE,
                COLUMN_PAYMENT_METHOD,
                COLUMN_PAYMENT_SCREENSHOT,
                COLUMN_PAYMENT_REVIEW_STATUS
            ),
            "$COLUMN_RESERVATION_TYPE = ? AND $COLUMN_PAYMENT_REVIEW_STATUS = ? AND $COLUMN_BOOKING_STATUS = ?",
            arrayOf("BUY", "PENDING", "ACTIVE"),
            null,
            null,
            "$COLUMN_BOOKING_ID DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                val bookingId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_ID))
                val bookingTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_TIME))
                val customerName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val customerPhone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE_NUMBER))
                val showtimeId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SHOWTIME_ID_FK))

                val userIdColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_ID_FK)
                val bookingUserId =
                    if (cursor.isNull(userIdColumnIndex)) null else cursor.getLong(userIdColumnIndex)

                val bookingStatus = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_STATUS))

                val canceledAtIndex = cursor.getColumnIndexOrThrow(COLUMN_CANCELED_AT)
                val canceledAt =
                    if (cursor.isNull(canceledAtIndex)) null else cursor.getString(canceledAtIndex)

                val reservationType =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESERVATION_TYPE))

                val paymentMethodIndex = cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_METHOD)
                val paymentMethod =
                    if (cursor.isNull(paymentMethodIndex)) null else cursor.getString(paymentMethodIndex)

                val paymentScreenshotIndex = cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_SCREENSHOT)
                val paymentScreenshot =
                    if (cursor.isNull(paymentScreenshotIndex)) null else cursor.getBlob(paymentScreenshotIndex)

                val paymentReviewStatus =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_REVIEW_STATUS))

                val booking = Booking(
                    bookingId,
                    bookingTime,
                    showtimeId,
                    customerName,
                    customerPhone,
                    bookingUserId,
                    bookingStatus,
                    canceledAt,
                    reservationType,
                    paymentMethod,
                    paymentScreenshot,
                    paymentReviewStatus
                )

                bookingList.add(booking)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return bookingList
    }

    fun approveBuyPayment(bookingId: Long): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PAYMENT_REVIEW_STATUS, "APPROVED")
        }

        val result = db.update(
            BOOKING_TABLE_NAME,
            values,
            "$COLUMN_BOOKING_ID = ?",
            arrayOf(bookingId.toString())
        )

        db.close()
        return result > 0
    }

    fun rejectBuyPayment(bookingId: Long): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PAYMENT_REVIEW_STATUS, "REJECTED")
            put(COLUMN_BOOKING_STATUS, "CANCELED")
            put(COLUMN_CANCELED_AT, java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                java.util.Locale.getDefault()
            ).format(java.util.Date()))
        }

        val result = db.update(
            BOOKING_TABLE_NAME,
            values,
            "$COLUMN_BOOKING_ID = ?",
            arrayOf(bookingId.toString())
        )

        db.close()
        return result > 0
    }

//    notification

    fun getBookingsByUserId(userId: Long): List<Booking> {
        val db = readableDatabase
        val bookingList = mutableListOf<Booking>()

        val cursor = db.query(
            BOOKING_TABLE_NAME,
            arrayOf(
                COLUMN_BOOKING_ID,
                COLUMN_BOOKING_TIME,
                COLUMN_NAME,
                COLUMN_PHONE_NUMBER,
                COLUMN_SHOWTIME_ID_FK,
                COLUMN_USER_ID_FK,
                COLUMN_BOOKING_STATUS,
                COLUMN_CANCELED_AT,
                COLUMN_RESERVATION_TYPE,
                COLUMN_PAYMENT_METHOD,
                COLUMN_PAYMENT_SCREENSHOT,
                COLUMN_PAYMENT_REVIEW_STATUS
            ),
            "$COLUMN_USER_ID_FK = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "$COLUMN_BOOKING_ID DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                val bookingId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_ID))
                val bookingTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_TIME))
                val customerName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val customerPhone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE_NUMBER))
                val showtimeId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SHOWTIME_ID_FK))

                val userIdColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_ID_FK)
                val bookingUserId =
                    if (cursor.isNull(userIdColumnIndex)) null else cursor.getLong(userIdColumnIndex)

                val bookingStatus = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_STATUS))

                val canceledAtIndex = cursor.getColumnIndexOrThrow(COLUMN_CANCELED_AT)
                val canceledAt =
                    if (cursor.isNull(canceledAtIndex)) null else cursor.getString(canceledAtIndex)

                val reservationType =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESERVATION_TYPE))

                val paymentMethodIndex = cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_METHOD)
                val paymentMethod =
                    if (cursor.isNull(paymentMethodIndex)) null else cursor.getString(paymentMethodIndex)

                val paymentScreenshotIndex = cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_SCREENSHOT)
                val paymentScreenshot =
                    if (cursor.isNull(paymentScreenshotIndex)) null else cursor.getBlob(paymentScreenshotIndex)

                val paymentReviewStatus =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_REVIEW_STATUS))

                val booking = Booking(
                    bookingId,
                    bookingTime,
                    showtimeId,
                    customerName,
                    customerPhone,
                    bookingUserId,
                    bookingStatus,
                    canceledAt,
                    reservationType,
                    paymentMethod,
                    paymentScreenshot,
                    paymentReviewStatus
                )

                bookingList.add(booking)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return bookingList
    }

    fun cancelBooking(bookingId: Long, canceledAt: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_BOOKING_STATUS, "CANCELED")
            put(COLUMN_CANCELED_AT, canceledAt)
        }

        val result = db.update(
            BOOKING_TABLE_NAME,
            values,
            "$COLUMN_BOOKING_ID = ?",
            arrayOf(bookingId.toString())
        )

        db.close()
        return result > 0
    }

    fun getBookedSeatIdsByShowtimeId(showtimeId: Long): Set<Long> {
        val bookedSeatIds = mutableSetOf<Long>()
        val db = readableDatabase

        val query = """
        SELECT bs.$COLUMN_SEAT_ID_FK
        FROM $BOOKING_SEAT_TABLE_NAME bs
        INNER JOIN $BOOKING_TABLE_NAME b
        ON bs.$COLUMN_BOOKING_ID_FK = b.$COLUMN_BOOKING_ID
        WHERE b.$COLUMN_SHOWTIME_ID_FK = ?
        AND b.$COLUMN_BOOKING_STATUS = 'ACTIVE'
        AND b.$COLUMN_RESERVATION_TYPE = 'BOOK'
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(showtimeId.toString()))

        if (cursor.moveToFirst()) {
            do {
                bookedSeatIds.add(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SEAT_ID_FK))
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return bookedSeatIds
    }

    fun getBoughtSeatIdsByShowtimeId(showtimeId: Long): Set<Long> {
        val boughtSeatIds = mutableSetOf<Long>()
        val db = readableDatabase

        val query = """
        SELECT bs.$COLUMN_SEAT_ID_FK
        FROM $BOOKING_SEAT_TABLE_NAME bs
        INNER JOIN $BOOKING_TABLE_NAME b
        ON bs.$COLUMN_BOOKING_ID_FK = b.$COLUMN_BOOKING_ID
        WHERE b.$COLUMN_SHOWTIME_ID_FK = ?
        AND b.$COLUMN_BOOKING_STATUS = 'ACTIVE'
        AND b.$COLUMN_RESERVATION_TYPE = 'BUY'
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(showtimeId.toString()))

        if (cursor.moveToFirst()) {
            do {
                boughtSeatIds.add(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SEAT_ID_FK))
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return boughtSeatIds
    }

//    admin donut charts
fun getMovieBookingCounts(): Map<String, Int> {
    val bookingCounts = mutableMapOf<String, Int>()
    val bookings = getAllBookings()

    for (booking in bookings) {
        if (booking.bookingStatus != "ACTIVE") continue

        val showtime = getShowtimeById(booking.showtimeId)
        val movie = getMovieById(showtime?.movieId ?: -1)

        if (movie != null) {
            val currentCount = bookingCounts[movie.name] ?: 0
            bookingCounts[movie.name] = currentCount + 1
        }
    }

    return bookingCounts
}

    fun getTheatreBookingCounts(): Map<String, Int> {
        val bookingCounts = mutableMapOf<String, Int>()
        val bookings = getAllBookings()

        for (booking in bookings) {
            if (booking.bookingStatus != "ACTIVE") continue

            val showtime = getShowtimeById(booking.showtimeId)
            val theatre = getTheatreById(showtime?.theatreId ?: -1)

            if (theatre != null) {
                val currentCount = bookingCounts[theatre.name] ?: 0
                bookingCounts[theatre.name] = currentCount + 1
            }
        }

        return bookingCounts
    }

//    admin data chart

    data class TopMovieStat(
        val movieName: String,
        val bookingCount: Int
    )

    data class MonthlyBookingStat(
        val monthLabel: String,
        val bookingCount: Int
    )

    data class TheatreBookingStat(
        val theatreName: String,
        val bookingCount: Int
    )

    fun getTotalMoviesCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $MOVIE_TABLE_NAME", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }

    fun getTotalCinemasCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $CINEMA_TABLE_NAME", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }

    fun getTotalTheatresCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $THEATRE_TABLE_NAME", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }

    fun getTotalUsersCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $USER_TABLE_NAME", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }

    fun getTotalBookingsCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $BOOKING_TABLE_NAME", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }

    fun getTop3SellingMovies(): List<TopMovieStat> {
        val db = readableDatabase
        val result = mutableListOf<TopMovieStat>()

        val query = """
        SELECT m.$COLUMN_MOVIE_NAME AS movie_name, COUNT(b.$COLUMN_BOOKING_ID) AS booking_count
        FROM $BOOKING_TABLE_NAME b
        INNER JOIN $SHOWTIME_TABLE_NAME s
            ON b.$COLUMN_SHOWTIME_ID_FK = s.$COLUMN_SHOWTIME_ID
        INNER JOIN $MOVIE_TABLE_NAME m
            ON s.$COLUMN_MOVIE_ID_FK = m.$COLUMN_MOVIE_ID
        GROUP BY m.$COLUMN_MOVIE_ID, m.$COLUMN_MOVIE_NAME
        ORDER BY booking_count DESC, m.$COLUMN_MOVIE_NAME ASC
        LIMIT 3
    """.trimIndent()

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                result.add(
                    TopMovieStat(
                        movieName = cursor.getString(cursor.getColumnIndexOrThrow("movie_name")),
                        bookingCount = cursor.getInt(cursor.getColumnIndexOrThrow("booking_count"))
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return result
    }

    fun getMonthlyBookingStats(): List<MonthlyBookingStat> {
        val db = readableDatabase
        val result = mutableListOf<MonthlyBookingStat>()

        val query = """
        SELECT strftime('%Y-%m', $COLUMN_BOOKING_TIME) AS month_label,
               COUNT(*) AS booking_count
        FROM $BOOKING_TABLE_NAME
        WHERE $COLUMN_BOOKING_TIME IS NOT NULL
        GROUP BY month_label
        ORDER BY month_label ASC
    """.trimIndent()

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val monthLabel = cursor.getString(cursor.getColumnIndexOrThrow("month_label")) ?: continue
                result.add(
                    MonthlyBookingStat(
                        monthLabel = monthLabel,
                        bookingCount = cursor.getInt(cursor.getColumnIndexOrThrow("booking_count"))
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return result
    }

    fun getTheatreBookingStats(): List<TheatreBookingStat> {
        val db = readableDatabase
        val result = mutableListOf<TheatreBookingStat>()

        val query = """
        SELECT t.$COLUMN_THEATRE_NAME AS theatre_name,
               COUNT(b.$COLUMN_BOOKING_ID) AS booking_count
        FROM $BOOKING_TABLE_NAME b
        INNER JOIN $SHOWTIME_TABLE_NAME s
            ON b.$COLUMN_SHOWTIME_ID_FK = s.$COLUMN_SHOWTIME_ID
        INNER JOIN $THEATRE_TABLE_NAME t
            ON s.$COLUMN_THEATRE_ID_FK = t.$COLUMN_THEATRE_ID
        GROUP BY t.$COLUMN_THEATRE_ID, t.$COLUMN_THEATRE_NAME
        ORDER BY booking_count DESC, t.$COLUMN_THEATRE_NAME ASC
    """.trimIndent()

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                result.add(
                    TheatreBookingStat(
                        theatreName = cursor.getString(cursor.getColumnIndexOrThrow("theatre_name")),
                        bookingCount = cursor.getInt(cursor.getColumnIndexOrThrow("booking_count"))
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return result
    }

}