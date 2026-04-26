package com.example.nothingcinema

import android.app.Application

class NothingCinemaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ThemeManager.applySavedTheme(this)
    }
}