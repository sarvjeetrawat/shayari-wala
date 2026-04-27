package com.kunpitech.shayariwala

import android.app.Application
import com.kunpitech.shayariwala.ads.AdManager

class ShayariApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize ads at app start — not in Activity
        AdManager.initialize(this)
    }
}