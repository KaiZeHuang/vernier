package com.kaizehuang.vernier.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.kaizehuang.vernier.core.detector.DetectorSpecificApi
import com.kaizehuang.vernier.core.detector.run
import com.kaizehuang.vernier.core.report.DetectorResult
import com.kaizehuang.vernier.features.su.detector.SuDetector
import com.kaizehuang.vernier.sdk.Vernier
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Runs one detector, then every detector, through the SDK alone and lists each verdict. The SDK's
 * launcher starts it with the early launch evidence. Running SU through `SuDetector` opts in to
 * that detector's own API, which may change in any release.
 */
@OptIn(DetectorSpecificApi::class)
class ScanActivity : Activity() {

    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Vernier.captureLaunchEvidence(intent)
        val output = TextView(this)
        setContentView(output)
        scope.launch {
            val su: DetectorResult = SuDetector.run(this@ScanActivity)
            val all: List<DetectorResult> = Vernier.scan(this@ScanActivity)
            output.text = (listOf(su) + all).joinToString(separator = "\n") { result ->
                "${result.id}: ${result.status.severity} (${result.report.verdict})"
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        Vernier.captureLaunchEvidence(intent)
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
