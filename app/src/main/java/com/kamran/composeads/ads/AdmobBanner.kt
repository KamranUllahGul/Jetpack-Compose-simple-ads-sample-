package com.kamran.composeads.ads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.kamran.composeads.ads.AdsIds.BannerAdId

@Composable
fun BannerAdView(
    adUnitId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    if (LocalInspectionMode.current) {
        // Shown in Preview
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.Black),
            contentAlignment = Center
        ) {
            Text(
                text = "Ad Banner Placeholder",
                color = Color.Green
            )
        }
    } else {
        var isAdLoaded by remember { mutableStateOf(false) }

        val displayMetrics = LocalContext.current.resources.displayMetrics
        val adWidthDp = (displayMetrics.widthPixels / displayMetrics.density).toInt()
        val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidthDp)


        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            factory = { ctx ->
                AdView(ctx).apply {
                    setAdSize(adSize)
                    setAdUnitId(adUnitId)

                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            isAdLoaded = true
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            isAdLoaded = false
                            print(error)
                        }
                    }

                    loadAd(AdRequest.Builder().build())
                }
            }
        )
        // Loading placeholder (shown until ad loads)
        if (!isAdLoaded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ad is loading...",
                    color = Color.Green
                )
            }
        }
    }
}


@Preview
@Composable
private fun PreviewAdmobBanner() {
    BannerAdView(
        adUnitId = BannerAdId
    )
}