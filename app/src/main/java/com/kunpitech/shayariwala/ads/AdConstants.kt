package com.kunpitech.shayariwala.ads

object AdConstants {

    // ── Test IDs — use these during development ───────────
    // Replace with REAL IDs before publishing to Play Store

    // App ID (also put in AndroidManifest)
    const val TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"

    // Banner Ad Unit ID (Standard)
   /* const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

    // Collapsible Banner Ad Unit ID (Official Google Test ID)
    const val COLLAPSIBLE_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/2014213617"

    // Interstitial Ad Unit ID
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    // App Open Ad Unit ID
    const val APP_OPEN_AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921"*/

    // ── Real IDs — replace before publishing ──────────────
     const val BANNER_AD_UNIT_ID             = "ca-app-pub-1843908357541717/4846314131"
     const val COLLAPSIBLE_BANNER_AD_UNIT_ID = "ca-app-pub-1843908357541717/6883398657"
     const val INTERSTITIAL_AD_UNIT_ID       = "ca-app-pub-1843908357541717/5893293182"
     const val APP_OPEN_AD_UNIT_ID           = "ca-app-pub-1843908357541717/9139292256"

    // Show interstitial every N shayari opens
    const val INTERSTITIAL_SHOW_EVERY = 5
}