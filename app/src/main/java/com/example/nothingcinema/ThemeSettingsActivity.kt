package com.example.nothingcinema

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity

class ThemeSettingsActivity : AppCompatActivity() {

    private lateinit var systemModeRadioButton: RadioButton
    private lateinit var lightModeRadioButton: RadioButton
    private lateinit var darkModeRadioButton: RadioButton
    private lateinit var applyThemeButton: Button

    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme_settings)

        systemModeRadioButton = findViewById(R.id.systemModeRadioButton)
        lightModeRadioButton = findViewById(R.id.lightModeRadioButton)
        darkModeRadioButton = findViewById(R.id.darkModeRadioButton)
        applyThemeButton = findViewById(R.id.applyThemeButton)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        when (ThemeManager.getSavedTheme(this)) {
            ThemeManager.MODE_SYSTEM -> systemModeRadioButton.isChecked = true
            ThemeManager.MODE_LIGHT -> lightModeRadioButton.isChecked = true
            ThemeManager.MODE_DARK -> darkModeRadioButton.isChecked = true
        }

        applyThemeButton.setOnClickListener {
            val selectedMode = when {
                systemModeRadioButton.isChecked -> ThemeManager.MODE_SYSTEM
                lightModeRadioButton.isChecked -> ThemeManager.MODE_LIGHT
                else -> ThemeManager.MODE_DARK
            }

            ThemeManager.saveTheme(this, selectedMode)
            finish()
            startActivity(intent)
        }
    }
}