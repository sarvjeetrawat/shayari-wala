package com.kunpitech.shayariwala.ads

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun BannerAdView(
    modifier : Modifier = Modifier,
    adUnitId : String   = AdConstants.BANNER_AD_UNIT_ID,
) {
    Box(
        modifier         = modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp)
            .background(Color(0xFF0E0E16)),
        contentAlignment = Alignment.Center,
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory  = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    setAdUnitId(adUnitId)

                    // Add listener for debugging
                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            Log.d("BannerAd", "Banner loaded successfully")
                        }
                        override fun onAdFailedToLoad(error: LoadAdError) {
                            Log.e("BannerAd", "Banner failed: code=${error.code} msg=${error.message}")
                        }
                        override fun onAdOpened() {
                            Log.d("BannerAd", "Banner opened")
                        }
                    }

                    loadAd(AdRequest.Builder().build())
                }
            },
        )
    }
}