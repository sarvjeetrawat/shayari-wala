package com.kunpitech.shayariwala.ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.Date

class AppOpenManager(private val application: Application) :
    Application.ActivityLifecycleCallbacks,
    DefaultLifecycleObserver {

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    private var isShowingAd = false
    private var currentActivity: Activity? = null
    private var loadTime: Long = 0
    private var isAdMobInitialized = false

    init {
        instance = this
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun onAdMobInitialized() {
        isAdMobInitialized = true
        Log.d(TAG, "AdMob initialized, pre-fetching first App Open ad.")
        fetchAd()
    }

    /** Request an ad */
    fun fetchAd() {
        // Have unused ad, no need to load
        if (isAdAvailable()) {
            return
        }

        if (isLoadingAd) {
            return
        }

        isLoadingAd = true
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            application,
            AdConstants.APP_OPEN_AD_UNIT_ID,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                    Log.d(TAG, "App Open Ad Loaded.")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                    Log.e(TAG, "App Open Ad Failed to Load: ${loadAdError.message}")
                }
            }
        )
    }

    /** Utility method to check if ad has been loaded and is not expired */
    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    /** Check if ad was loaded less than N hours ago */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < (numMilliSecondsPerHour * numHours)
    }

    /** Show the ad if available */
    fun showAdIfAvailable() {
        if (!isAdMobInitialized) {
            Log.d(TAG, "AdMob not initialized yet, skipping show.")
            return
        }

        // Only show ad if there is a currently visible activity and ad is available
        val activity = currentActivity ?: return
        if (isShowingAd) {
            Log.d(TAG, "Ad already showing.")
            return
        }

        if (!isAdAvailable()) {
            Log.d(TAG, "Ad not available, loading one for next session...")
            fetchAd()
            return
        }

        Log.d(TAG, "Will show App Open ad.")

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                // Set the ad to null so we don't show the same ad again
                appOpenAd = null
                isShowingAd = false
                Log.d(TAG, "App Open ad dismissed.")
                fetchAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                isShowingAd = false
                Log.e(TAG, "App Open ad failed to show: ${adError.message}")
                fetchAd()
            }

            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
                Log.d(TAG, "App Open ad showing.")
            }
        }
        appOpenAd?.show(activity)
    }

    // Lifecycle Observer methods
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        showAdIfAvailable()
    }

    // Activity Lifecycle Callback methods
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
        }
    }

    companion object {
        private const val TAG = "AppOpenManager"
        @Volatile
        var instance: AppOpenManager? = null
            private set
    }
}
