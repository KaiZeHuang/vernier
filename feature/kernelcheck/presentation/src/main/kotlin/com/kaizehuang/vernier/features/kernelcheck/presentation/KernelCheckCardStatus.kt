package com.kaizehuang.vernier.features.kernelcheck.presentation

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import com.kaizehuang.vernier.features.kernelcheck.domain.KernelCheckCvePatchState
import com.kaizehuang.vernier.features.kernelcheck.domain.KernelCheckMethod
import com.kaizehuang.vernier.features.kernelcheck.domain.KernelCheckMethodOutcome
import com.kaizehuang.vernier.features.kernelcheck.domain.KernelCheckMethodResult
internal fun cvePatchStatus(
    state: KernelCheckCvePatchState,
): DetectorStatus {
    return when (state) {
        KernelCheckCvePatchState.UNPATCHED,
        KernelCheckCvePatchState.PARTIALLY_PATCHED -> DetectorStatus.info(InfoKind.SUPPORT)

        KernelCheckCvePatchState.PATCHED -> DetectorStatus.allClear()
        KernelCheckCvePatchState.INCONCLUSIVE -> DetectorStatus.info(InfoKind.SUPPORT)
    }
}

internal fun methodStatus(result: KernelCheckMethodResult): DetectorStatus {
    return when (result.outcome) {
        KernelCheckMethodOutcome.CLEAN -> DetectorStatus.allClear()
        KernelCheckMethodOutcome.DETECTED -> DetectorStatus.danger()
        KernelCheckMethodOutcome.INFO -> {
            if (result.method == KernelCheckMethod.CVE_PATCH_CHECK) {
                DetectorStatus.info(InfoKind.SUPPORT)
            } else {
                DetectorStatus.warning()
            }
        }

        KernelCheckMethodOutcome.SUPPORT -> DetectorStatus.info(InfoKind.SUPPORT)
    }
}
