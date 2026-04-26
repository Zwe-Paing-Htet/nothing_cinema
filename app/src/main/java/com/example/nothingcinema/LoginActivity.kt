package com.example.nothingcinema

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {

    private lateinit var loginUsernameEditText: EditText
    private lateinit var loginPasswordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpLink: TextView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var backButton: ImageButton

    private lateinit var passwordToggle: ImageView
    private lateinit var termsCheckBox: CheckBox
    private lateinit var termsText: TextView

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Views
        loginUsernameEditText = findViewById(R.id.loginUsernameEditText)
        loginPasswordEditText = findViewById(R.id.loginPasswordEditText)
        loginButton = findViewById(R.id.loginButton)
        signUpLink = findViewById(R.id.signUpLink)
        backButton = findViewById(R.id.backButton)

        passwordToggle = findViewById(R.id.passwordToggle)
        termsCheckBox = findViewById(R.id.termsCheckBox)
        termsText = findViewById(R.id.termsText)

        dbHelper = DatabaseHelper(this)

        // Login button
        loginButton.setOnClickListener {
            val loginInput = loginUsernameEditText.text.toString().trim()
            val password = loginPasswordEditText.text.toString().trim()

            if (TextUtils.isEmpty(loginInput) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()

            } else if (!termsCheckBox.isChecked) {
                Toast.makeText(this, "Please accept Terms & Conditions", Toast.LENGTH_SHORT).show()

            } else {
                loginUser(loginInput, password)
            }
        }

        // Register link
        signUpLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Back button
        backButton.setOnClickListener {
            finish()
        }

        // Password toggle 👁
        passwordToggle.setOnClickListener {
            if (isPasswordVisible) {
                loginPasswordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordToggle.setImageResource(R.drawable.ic_eye)
            } else {
                loginPasswordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                passwordToggle.setImageResource(R.drawable.ic_eye_off)
            }
            loginPasswordEditText.setSelection(loginPasswordEditText.text.length)
            isPasswordVisible = !isPasswordVisible
        }

        // Terms click → open page
        termsText.setOnClickListener {
            startActivity(Intent(this, TermsAndConditionsActivity::class.java))
        }
    }

    private fun loginUser(loginInput: String, password: String) {
        val user = dbHelper.getUserByUsernameOrEmail(loginInput)

        if (user != null) {
            if (user.passwordHash == hashPassword(password)) {

                val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("username", user.username)
                editor.putLong("user_id", user.id)
                editor.apply()

                navigateToHome(user)

            } else {
                Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Username or email not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hashPassword(password: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hashedBytes = messageDigest.digest(password.toByteArray())
        return hashedBytes.joinToString("") { "%02x".format(it) }
    }

    private fun navigateToHome(user: DatabaseHelper.User) {
        if (user.roleId == 1L) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else if (user.roleId == 2L) {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
            finish()
        }
    }
}