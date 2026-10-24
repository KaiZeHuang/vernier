package com.kaizehuang.vernier

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ComposeView
import com.kaizehuang.vernier.core.designsystem.theme.VernierAppTheme
import com.kaizehuang.vernier.core.ui.AppBuildInfo
import com.kaizehuang.vernier.core.ui.LocalAppBuildInfo
import com.kaizehuang.vernier.sdk.Vernier
import com.kaizehuang.vernier.ui.VernierApp
import com.kaizehuang.vernier.ui.StartupBootstrapLoadingScreen
import com.kaizehuang.vernier.ui.appearance.UiAppearanceStore
import io.github.xiaotong6666.uihelper.mode.UiMode
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var procMountSampler: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Vernier.captureLaunchEvidence(intent)
        enableEdgeToEdge()
        // Attached before Compose starts, as the SDK asks.
        procMountSampler = Vernier.createProcMountSampler(this)
        val root = FrameLayout(this)
        procMountSampler?.let { sampler ->
            root.addView(sampler, FrameLayout.LayoutParams(1, 1))
        }
        val composeView = ComposeView(this)
        root.addView(
            composeView,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            ),
        )
        setContentView(root)
        val appearanceStore = UiAppearanceStore.getInstance(applicationContext)
        composeView.setContent {
            // Resolve the saved mode before rendering the app, otherwise a saved Material choice
            // briefly flashes MIUIX on every cold start.
            val uiMode by produceState<UiMode?>(initialValue = null, key1 = appearanceStore) {
                appearanceStore.mode.collect { value = it }
            }
            val scope = rememberCoroutineScope()
            CompositionLocalProvider(LocalAppBuildInfo provides appBuildInfo) {
                val resolvedMode = uiMode
                if (resolvedMode == null) {
                    StartupBootstrapLoadingScreen()
                } else {
                    VernierAppTheme(uiMode = resolvedMode) {
                        VernierApp(onUiModeChange = { selected ->
                            scope.launch { appearanceStore.setMode(selected) }
                        })
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        Vernier.captureLaunchEvidence(intent)
    }

    override fun onDestroy() {
        val sampler = procMountSampler
        (sampler?.parent as? ViewGroup)?.removeView(sampler)
        sampler?.destroy()
        procMountSampler = null
        super.onDestroy()
    }

    private val appBuildInfo = AppBuildInfo(
        versionName = BuildConfig.VERSION_NAME,
        versionCode = BuildConfig.VERSION_CODE,
        buildHash = BuildConfig.BUILD_HASH,
        buildTimeUtc = BuildConfig.BUILD_TIME_UTC,
        isAlphaVersion = BuildConfig.isAlphaVersion,
    )
}
