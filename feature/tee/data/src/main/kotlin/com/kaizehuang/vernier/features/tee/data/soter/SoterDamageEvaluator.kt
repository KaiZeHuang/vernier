package com.kaizehuang.vernier.features.tee.data.soter

import com.kaizehuang.vernier.features.tee.domain.TeeSoterAnomaly
import com.kaizehuang.vernier.features.tee.domain.TeeSoterState

class SoterDamageEvaluator {

    fun evaluate(
        serviceReachable: Boolean,
        keyPrepared: Boolean,
        signSessionAvailable: Boolean,
        errorMessage: String?,
        abnormalEnvironment: Boolean = false,
        anomalies: List<TeeSoterAnomaly> = emptyList(),
    ): TeeSoterState {
        val available = serviceReachable && keyPrepared && signSessionAvailable
        val damaged = serviceReachable && !available
        val outcome = when {
            available -> "Soter checks succeeded: Treble service was reachable and ASK/AuthKey/initSigh all succeeded."
            abnormalEnvironment ->
                "Abnormal Soter environment: Simplified Chinese locale on a likely Soter-supporting device, but PackageManager could not resolve com.tencent.soter.soterserver and no biometric authentication was available."
            !serviceReachable -> "Soter check skipped because the Treble service was not reachable."
            errorMessage != null -> withSoterHint(errorMessage)
            !keyPrepared -> "Soter key preparation failed after the Treble service became reachable."
            else -> "Soter signing session initialization failed after the Treble service became reachable."
        }
        val replyReview = anomalies.joinToString(separator = "; ") { it.detail }
        val summary = when {
            anomalies.isEmpty() -> outcome
            available -> "Soter ASK/AuthKey/initSigh calls succeeded, but the replies need review: $replyReview."
            else -> "${outcome.trimEnd('.')}. Replies need review: $replyReview."
        }
        return TeeSoterState(
            serviceReachable = serviceReachable,
            keyPrepared = keyPrepared,
            signSessionAvailable = signSessionAvailable,
            available = available,
            damaged = damaged,
            abnormalEnvironment = abnormalEnvironment,
            anomalies = anomalies,
            summary = summary,
        )
    }

    private fun withSoterHint(message: String): String {
        return if (message.contains("soter", ignoreCase = true)) {
            message
        } else {
            "Soter check: $message"
        }
    }
}
