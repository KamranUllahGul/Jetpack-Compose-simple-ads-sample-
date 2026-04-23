package com.kamran.composeads.ui.screen

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kamran.composeads.ads.AdsIds.BannerAdId
import com.kamran.composeads.ads.AdsIds.NativeAdId
import com.kamran.composeads.ads.AdsIds.interstitialAdId
import com.kamran.composeads.ads.BannerAdView
import com.kamran.composeads.ads.InterstitialAdManager
import com.kamran.composeads.ads.NativeAdView
import com.kamran.composeads.ui.theme.ComposeAdsTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // MobileAds initialization (already done)
        setContent {
            ComposeAdsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { edgeToEdgePadding ->
                    MainScreen(modifier = Modifier.padding(edgeToEdgePadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current

    // ──────────────────────────────────────────────────────────────
    // Interstitial logic — ONLY runs in real app (prevents preview crash)
    // ──────────────────────────────────────────────────────────────
    val interstitialManager = if (!isPreview) {
        remember { InterstitialAdManager(context as Activity) }
    } else {
        null
    }

    // Load the interstitial only when NOT in preview
    if (!isPreview) {
        LaunchedEffect(Unit) {
            interstitialManager?.loadAd(interstitialAdId)
        }
    }

    // ──────────────────────────────────────────────────────────────
    // Common UI (no duplication anymore)
    // ──────────────────────────────────────────────────────────────
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.LightGray),
    ) {
        TopAppBar(
            title = {
                Text(
                    "admob_demo",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Green
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .background(color = Color.Black)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(16.dp)
                    .clickable {
                        // Safe click: only real app can show ad
                        if (!isPreview) {
                            interstitialManager?.showAd()
                        }
                    }
                    .border(2.dp, Color.Green, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    contentColor = Color.Green,
                    containerColor = Color.Black
                ),
            ) {
                Text(
                    text = "Click for Interstitial Ad",
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .wrapContentSize(Alignment.Center),
                    color = Color.Green,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            NativeAdView(NativeAdId, modifier = Modifier.fillMaxWidth().padding(16.dp))
        }

        // Banner pinned at the bottom (already preview-friendly)
        BannerAdView(
            adUnitId = BannerAdId,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewMainScreen() {
    MainScreen()
}