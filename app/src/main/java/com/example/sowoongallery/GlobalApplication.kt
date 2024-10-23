package com.example.sowoongallery

import android.app.Application
import com.google.firebase.FirebaseApp

class GlobalApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}