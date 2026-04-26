package com.example.nothingcinema

import android.content.Intent
import android.text.TextUtils
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class CustomerProfileActivity : AppCompatActivity() {

    private lateinit var tvGuestMessage: TextView
    private lateinit var layoutProfileContent: LinearLayout
    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnEditProfile: Button
    private lateinit var btnThemeSettings: Button
    private lateinit var btnLogout: Button
    private lateinit var btnDeleteAccount: Button

    private lateinit var backButton: ImageButton

    private lateinit var dbHelper: DatabaseHelper
    private var currentUserId: Long = -1L
    private var currentUser: DatabaseHelper.User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_profile)

        tvGuestMessage = findViewById(R.id.tvGuestMessage)
        layoutProfileContent = findViewById(R.id.layoutProfileContent)
        tvUsername = findViewById(R.id.tvUsername)
        tvEmail = findViewById(R.id.tvEmail)
        btnEditProfile = findViewById(R.id.btnEditProfile)
        btnThemeSettings = findViewById(R.id.btnThemeSettings)
        btnLogout = findViewById(R.id.btnLogout)
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        dbHelper = DatabaseHelper(this)

        checkLoginStatus()

        btnEditProfile.setOnClickListener {
            showEditDialog()
        }

        btnThemeSettings.setOnClickListener {
            startActivity(Intent(this, ThemeSettingsActivity::class.java))
        }

        btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }

        btnDeleteAccount.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    override fun onResume() {
        super.onResume()
        if (currentUserId != -1L) {
            loadUserProfile()
        }
    }

    private fun checkLoginStatus() {
        val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        currentUserId = sharedPreferences.getLong("user_id", -1L)

        if (currentUserId == -1L) {
            tvGuestMessage.visibility = TextView.VISIBLE
            layoutProfileContent.visibility = LinearLayout.GONE
        } else {
            tvGuestMessage.visibility = TextView.GONE
            layoutProfileContent.visibility = LinearLayout.VISIBLE
            loadUserProfile()
        }
    }

    private fun loadUserProfile() {
        currentUser = dbHelper.getUserById(currentUserId)

        if (currentUser != null) {
            tvUsername.text = "Username: ${currentUser!!.username}"
            tvEmail.text = "Email: ${currentUser!!.email}"
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showEditDialog() {
        val dialogView = layoutInflater.inflate(R.layout.activity_edit_customer_profile, null)
        val etEditUsername = dialogView.findViewById<EditText>(R.id.etEditUsername)
        val etEditEmail = dialogView.findViewById<EditText>(R.id.etEditEmail)

        currentUser?.let {
            etEditUsername.setText(it.username)
            etEditEmail.setText(it.email)
        }

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newUsername = etEditUsername.text.toString().trim()
                val newEmail = etEditEmail.text.toString().trim()

                if (TextUtils.isEmpty(newUsername) || TextUtils.isEmpty(newEmail)) {
                    Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                } else {
                    val updated = dbHelper.updateUserProfile(currentUserId, newUsername, newEmail)
                    if (updated) {
                        val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
                        sharedPreferences.edit().putString("username", newUsername).apply()

                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        loadUserProfile()
                    } else {
                        Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { _, _ ->
                val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()

                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account?")
            .setPositiveButton("Yes") { _, _ ->
                val deleted = dbHelper.deleteUserAccount(currentUserId)

                if (deleted) {
                    val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
                    sharedPreferences.edit().clear().apply()

                    Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, WelcomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to delete account", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }
}