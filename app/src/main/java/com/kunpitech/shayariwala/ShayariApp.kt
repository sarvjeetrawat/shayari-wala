package com.kunpitech.shayariwala

import android.app.Application
import com.kunpitech.shayariwala.ads.AdManager
import com.kunpitech.shayariwala.ads.AppOpenManager

class ShayariApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize ads at app start — not in Activity
        AdManager.initialize(this)
        AppOpenManager(this)
    }
}