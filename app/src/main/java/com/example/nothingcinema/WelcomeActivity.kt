package com.example.nothingcinema

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var guestButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)

        val sharedPreferences: SharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", -1L)
        val username = sharedPreferences.getString("username", null)

        if (userId != -1L && !username.isNullOrEmpty()) {
            val dbHelper = DatabaseHelper(this)
            val user = dbHelper.getUserById(userId)

            if (user != null) {
                if (user.roleId == 1L) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    return
                } else if (user.roleId == 2L) {
                    startActivity(Intent(this, AdminDashboardActivity::class.java))
                    finish()
                    return
                }
            }
        }

        setContentView(R.layout.activity_welcome)

        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
        guestButton = findViewById(R.id.guestButton)

        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        guestButton.setOnClickListener {
            showGuestConfirmationDialog()
        }
    }

    private fun showGuestConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Continue as Guest?")
            .setMessage("Are you sure you want to continue as guest? Some features may be limited for guest users.")
            .setPositiveButton("OK") { _, _ ->
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}