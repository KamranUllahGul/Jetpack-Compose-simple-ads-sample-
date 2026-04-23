package com.kamran.composeads.ads

import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView as SDKNativeAdView

@Composable
fun NativeAdView(
    adUnitId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    var loadError by remember { mutableStateOf<String?>(null) }

    // ──────────────────────────────────────────────────────────────
    // PREVIEW MODE → Beautiful mock (exactly like before)
    // ──────────────────────────────────────────────────────────────
    if (LocalInspectionMode.current) {
        NativeAdContent(
            headline = "Sample Ad • Try Now!",
            body = "Discover amazing features in this app. Download today and get 30% off your first purchase.",
            cta = "Install Now",
            iconPainter = painterResource(id = android.R.drawable.ic_menu_gallery),
            modifier = modifier
        )
        return
    }

    // ──────────────────────────────────────────────────────────────
    // REAL MODE → Load ad + use official NativeAdView
    // ──────────────────────────────────────────────────────────────
    LaunchedEffect(adUnitId) {
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { ad ->
                nativeAd = ad
                loadError = null
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    loadError = "Failed: ${error.message}"
                }
            })
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    DisposableEffect(Unit) {
        onDispose { nativeAd?.destroy() }
    }

    // Real Native Ad using official SDK view
    key(nativeAd) {
        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            factory = { ctx ->
                val sdkNativeAdView = SDKNativeAdView(ctx)

                // Container that matches your preview design
                val container = LinearLayout(ctx).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(24, 24, 24, 24)
                    setBackgroundColor(android.graphics.Color.WHITE)
                }

                // Icon
                val iconView = ImageView(ctx).apply {
                    layoutParams = LinearLayout.LayoutParams(48.toPx(ctx), 48.toPx(ctx))
                }

                // Headline
                val headlineView = TextView(ctx).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    setTextSize(16f)
                    setTextColor(android.graphics.Color.BLACK)
                }

                // Body
                val bodyView = TextView(ctx).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    setTextSize(14f)
                    setTextColor(android.graphics.Color.DKGRAY)
                }

                // CTA Button
                val ctaView = Button(ctx).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(0, 16, 0, 0) }
                }

                // Add views to container
                val row = LinearLayout(ctx).apply {
                    orientation = LinearLayout.HORIZONTAL
                    addView(iconView)
                    addView(headlineView, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(12, 0, 0, 0) })
                }
                container.addView(row)
                container.addView(bodyView)
                container.addView(ctaView)

                // Register views with NativeAdView (THIS makes CTA work!)
                sdkNativeAdView.headlineView = headlineView
                sdkNativeAdView.iconView = iconView
                sdkNativeAdView.bodyView = bodyView
                sdkNativeAdView.callToActionView = ctaView

                sdkNativeAdView.addView(container)
                sdkNativeAdView
            },
            update = { sdkNativeAdView ->
                nativeAd?.let { ad ->
                    (sdkNativeAdView.headlineView as? TextView)?.text = ad.headline
                    (sdkNativeAdView.bodyView as? TextView)?.text = ad.body
                    (sdkNativeAdView.callToActionView as? Button)?.text = ad.callToAction

                    // Icon
                    ad.icon?.let { icon ->
                        (sdkNativeAdView.iconView as? ImageView)?.setImageDrawable(icon.drawable)
                    }

                    // Tell AdMob this is the real ad (enables clicks + tracking)
                    sdkNativeAdView.setNativeAd(ad)
                }
            }
        )

    }
}

// Helper to convert dp to pixels inside AndroidView
private fun Int.toPx(ctx: android.content.Context): Int =
    (this * ctx.resources.displayMetrics.density).toInt()

/** Your original preview UI (kept unchanged) */
@Composable
private fun NativeAdContent(
    headline: String?,
    body: String?,
    cta: String?,
    iconPainter: Any?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = when (iconPainter) {
                        is androidx.compose.ui.graphics.painter.Painter -> iconPainter
                        else -> rememberAsyncImagePainter(model = iconPainter)
                    },
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = headline ?: "Ad Headline",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = body ?: "This is a beautiful sample ad body text that describes the offer.",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { /* Preview only */ },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = cta ?: "Learn More")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewNativeAd() {
    NativeAdView(adUnitId = "ca-app-pub-3940256099942544/2247696110")
}