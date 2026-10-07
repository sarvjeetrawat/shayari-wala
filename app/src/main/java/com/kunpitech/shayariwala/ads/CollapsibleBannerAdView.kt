package com.kunpitech.shayariwala.ads

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

private const val TAG = "CollapsibleBanner"

@Composable
fun CollapsibleBannerAdView(
    modifier: Modifier = Modifier,
    adUnitId: String = AdConstants.COLLAPSIBLE_BANNER_AD_UNIT_ID,
    collapsePosition: String = "bottom", // "bottom" or "top"
    applyNavigationBarsPadding: Boolean = false,
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp

    // Calculate anchored adaptive banner size
    val adSize = remember(screenWidthDp) {
        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, screenWidthDp)
    }

    var adViewRef by remember { mutableStateOf<AdView?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            adViewRef?.destroy()
            adViewRef = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color(0xFF0E0E16))
            .then(
                if (applyNavigationBarsPadding) Modifier.navigationBarsPadding() else Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { ctx ->
                AdView(ctx).apply {
                    setAdSize(adSize)
                    setAdUnitId(adUnitId)

                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            Log.d(TAG, "Collapsible banner loaded successfully")
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            Log.e(TAG, "Collapsible banner failed to load: code=${error.code}, message=${error.message}")
                        }

                        override fun onAdOpened() {
                            Log.d(TAG, "Collapsible banner opened")
                        }

                        override fun onAdClosed() {
                            Log.d(TAG, "Collapsible banner closed / collapsed")
                        }
                    }

                    // Set the collapsible parameter in extras bundle
                    val extras = Bundle().apply {
                        putString("collapsible", collapsePosition)
                    }
                    val adRequest = AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                        .build()

                    loadAd(adRequest)
                    adViewRef = this
                }
            },
        )
    }
}
