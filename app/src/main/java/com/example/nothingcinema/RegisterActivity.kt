package com.example.nothingcinema

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.security.MessageDigest

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerUsernameEditText: EditText
    private lateinit var registerEmailEditText: EditText
    private lateinit var registerPasswordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var backButton: ImageButton
    private lateinit var signInLink: TextView
    private lateinit var passwordToggle: ImageView

    private var isPasswordVisible = false
    private var isUpdatingUsername = false
    private var isUpdatingEmail = false

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        registerUsernameEditText = findViewById(R.id.registerUsernameEditText)
        registerEmailEditText = findViewById(R.id.registerEmailEditText)
        registerPasswordEditText = findViewById(R.id.registerPasswordEditText)
        registerButton = findViewById(R.id.registerButton)
        backButton = findViewById(R.id.backButton)
        signInLink = findViewById(R.id.signInLink)
        passwordToggle = findViewById(R.id.passwordToggle)

        dbHelper = DatabaseHelper(this)

        // Prevent spaces from being typed into username, email, and password
        val noSpaceFilter = InputFilter { source, _, _, _, _, _ ->
            if (source != null && source.contains(" ")) {
                source.toString().replace(" ", "")
            } else {
                null
            }
        }

        registerUsernameEditText.filters = arrayOf(noSpaceFilter)
        registerEmailEditText.filters = arrayOf(noSpaceFilter)
        registerPasswordEditText.filters = arrayOf(noSpaceFilter)

        // Force username lowercase + no spaces, even on paste
        registerUsernameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isUpdatingUsername) return

                val original = s.toString()
                val cleaned = original.lowercase().replace(" ", "")

                if (original != cleaned) {
                    isUpdatingUsername = true
                    registerUsernameEditText.setText(cleaned)
                    registerUsernameEditText.setSelection(cleaned.length)
                    isUpdatingUsername = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Force email lowercase + no spaces, even on paste
        registerEmailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isUpdatingEmail) return

                val original = s.toString()
                val cleaned = original.lowercase().replace(" ", "")

                if (original != cleaned) {
                    isUpdatingEmail = true
                    registerEmailEditText.setText(cleaned)
                    registerEmailEditText.setSelection(cleaned.length)
                    isUpdatingEmail = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        backButton.setOnClickListener {
            finish()
        }

        signInLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        passwordToggle.setOnClickListener {
            if (isPasswordVisible) {
                registerPasswordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordToggle.setImageResource(R.drawable.ic_eye)
            } else {
                registerPasswordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                passwordToggle.setImageResource(R.drawable.ic_eye_off)
            }
            registerPasswordEditText.setSelection(registerPasswordEditText.text.length)
            isPasswordVisible = !isPasswordVisible
        }

        registerButton.setOnClickListener {
            val username = registerUsernameEditText.text.toString().trim()
            val email = registerEmailEditText.text.toString().trim()
            val password = registerPasswordEditText.text.toString()

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()

            } else if (username.contains(" ")) {
                Toast.makeText(this, "Username cannot contain spaces", Toast.LENGTH_SHORT).show()

            } else if (username != username.lowercase()) {
                Toast.makeText(this, "Username must be lowercase only", Toast.LENGTH_SHORT).show()

            } else if (email.contains(" ")) {
                Toast.makeText(this, "Email cannot contain spaces", Toast.LENGTH_SHORT).show()

            } else if (!email.endsWith("@gmail.com")) {
                Toast.makeText(this, "Email must end with @gmail.com", Toast.LENGTH_SHORT).show()

            } else if (password.contains(" ")) {
                Toast.makeText(this, "Password cannot contain spaces", Toast.LENGTH_SHORT).show()

            } else if (password.length < 8) {
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()

            } else if (dbHelper.isUsernameTaken(username)) {
                Toast.makeText(this, "Username is already taken", Toast.LENGTH_SHORT).show()

            } else if (dbHelper.isEmailTaken(email)) {
                Toast.makeText(this, "Email is already taken", Toast.LENGTH_SHORT).show()

            } else {
                val hashedPassword = hashPassword(password)
                dbHelper.registerUser(username, email, hashedPassword)
                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun hashPassword(password: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hashedBytes = messageDigest.digest(password.toByteArray())
        return hashedBytes.joinToString("") { "%02x".format(it) }
    }
}