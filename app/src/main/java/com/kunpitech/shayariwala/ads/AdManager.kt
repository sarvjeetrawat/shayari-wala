package com.kunpitech.shayariwala.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object AdManager {

    private const val TAG = "AdManager"

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var interstitialAd    : InterstitialAd? = null
    private var shayariOpenCount  : Int             = 0
    private var isInitialized     : Boolean         = false
    private var appContext        : Context?        = null

    private val _isInterstitialReady = MutableStateFlow(false)
    val isInterstitialReady: StateFlow<Boolean> = _isInterstitialReady.asStateFlow()

    fun initialize(context: Context) {
        if (isInitialized) return
        appContext = context.applicationContext

        // ── Step 1: configure test devices BEFORE init ────
        val testDeviceIds = listOf(
            AdRequest.DEVICE_ID_EMULATOR,   // emulator always works
            // add your physical device ID here (see logcat for "Use RequestConfiguration")
        )
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(testDeviceIds)
            .build()
        MobileAds.setRequestConfiguration(configuration)

        // ── Step 2: initialize SDK ────────────────────────
        scope.launch(Dispatchers.IO) {
            MobileAds.initialize(context.applicationContext) { initStatus ->
                isInitialized = true
                Log.d(TAG, "MobileAds initialized: ${initStatus.adapterStatusMap}")
                scope.launch(Dispatchers.Main) {
                    loadInterstitial(context.applicationContext)
                }
            }
        }
    }

    fun loadInterstitial(context: Context) {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context.applicationContext,
            AdConstants.INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd             = ad
                    _isInterstitialReady.value = true
                    Log.d(TAG, "Interstitial loaded successfully")

                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Log.d(TAG, "Interstitial dismissed")
                            interstitialAd             = null
                            _isInterstitialReady.value = false
                            appContext?.let { loadInterstitial(it) }
                        }
                        override fun onAdFailedToShowFullScreenContent(e: AdError) {
                            Log.e(TAG, "Interstitial failed to show: ${e.message}")
                            interstitialAd             = null
                            _isInterstitialReady.value = false
                            appContext?.let { loadInterstitial(it) }
                        }
                        override fun onAdShowedFullScreenContent() {
                            Log.d(TAG, "Interstitial showing")
                        }
                    }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e(TAG, "Interstitial load failed: code=${error.code} msg=${error.message}")
                    interstitialAd             = null
                    _isInterstitialReady.value = false
                }
            }
        )
    }

    fun onShayariOpened(activity: Activity) {
        shayariOpenCount++
        Log.d(TAG, "Shayari open count: $shayariOpenCount")
        if (shayariOpenCount % AdConstants.INTERSTITIAL_SHOW_EVERY == 0) {
            showInterstitial(activity)
        }
    }

    fun showInterstitial(activity: Activity) {
        if (interstitialAd != null) {
            interstitialAd!!.show(activity)
        } else {
            Log.d(TAG, "Interstitial not ready, loading now...")
            appContext?.let { loadInterstitial(it) }
        }
    }
}