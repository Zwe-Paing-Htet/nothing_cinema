package com.example.nothingcinema

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.security.MessageDigest

class RegisterAdminActivity : AppCompatActivity() {

    private lateinit var adminUsernameEditText: EditText
    private lateinit var adminEmailEditText: EditText
    private lateinit var adminPasswordEditText: EditText
    private lateinit var registerAdminButton: Button

    private lateinit var backButton: ImageButton
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_admin)

        adminUsernameEditText = findViewById(R.id.adminUsernameEditText)
        adminEmailEditText = findViewById(R.id.adminEmailEditText)
        adminPasswordEditText = findViewById(R.id.adminPasswordEditText)
        registerAdminButton = findViewById(R.id.registerAdminButton)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        dbHelper = DatabaseHelper(this)

        registerAdminButton.setOnClickListener {
            val username = adminUsernameEditText.text.toString().trim()
            val email = adminEmailEditText.text.toString().trim()
            val password = adminPasswordEditText.text.toString().trim()

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (dbHelper.isUsernameTaken(username)) {
                Toast.makeText(this, "Username is already taken", Toast.LENGTH_SHORT).show()
            } else if (dbHelper.isEmailTaken(email)) {
                Toast.makeText(this, "Email is already taken", Toast.LENGTH_SHORT).show()
            } else {
                val hashedPassword = hashPassword(password)
                val result = dbHelper.registerAdmin(username, email, hashedPassword)

                if (result != -1L) {
                    Toast.makeText(this, "Admin registration successful!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to register admin", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun hashPassword(password: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hashedBytes = messageDigest.digest(password.toByteArray())
        return hashedBytes.joinToString("") { "%02x".format(it) }
    }
}