package com.kamran.composeads.ads

import android.app.Activity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError

import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdManager(private val activity: Activity) {
    private var interstitialAd : InterstitialAd? = null

    fun loadAd(adUnitId: String) {
        InterstitialAd.load(
            activity,
            adUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad

                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            interstitialAd = null
                            loadAd(adUnitId)
                        }
                    }
                }
            }
        )
    }
    fun showAd() {
        interstitialAd?.show(activity) ?:run {

        }
    }
}