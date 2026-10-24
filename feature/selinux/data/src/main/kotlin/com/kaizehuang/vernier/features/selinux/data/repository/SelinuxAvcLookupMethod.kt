package com.kaizehuang.vernier.features.selinux.data.repository

import com.kaizehuang.vernier.capability.selinuxpolicy.data.SelinuxAvcLookupPayload
import com.kaizehuang.vernier.capability.selinuxpolicy.data.SelinuxAvcLookupSnapshot
import com.kaizehuang.vernier.capability.selinuxpolicy.data.SelinuxAvcLookupState
import com.kaizehuang.vernier.features.selinux.domain.SelinuxAvcLookupCollection
import com.kaizehuang.vernier.features.selinux.domain.SelinuxAvcLookupCollectionDetails
import com.kaizehuang.vernier.features.selinux.domain.SelinuxAvcLookupReading
import com.kaizehuang.vernier.features.selinux.domain.SelinuxCheckResult
import com.kaizehuang.vernier.features.selinux.domain.SelinuxOracle

internal fun buildAvcLookupMethod(snapshot: SelinuxAvcLookupSnapshot): SelinuxCheckResult {
    val reading = SelinuxAvcLookupReading(
        collection = when (snapshot.state) {
            SelinuxAvcLookupState.NOT_COLLECTED -> SelinuxAvcLookupCollection.NOT_COLLECTED
            SelinuxAvcLookupState.COLLECTED -> SelinuxAvcLookupCollection.COLLECTED
            SelinuxAvcLookupState.TIMING_ONLY -> SelinuxAvcLookupCollection.TIMING_ONLY
            SelinuxAvcLookupState.PERMISSION_LIMITED -> SelinuxAvcLookupCollection.PERMISSION_LIMITED
            SelinuxAvcLookupState.UNSUPPORTED -> SelinuxAvcLookupCollection.UNSUPPORTED
            SelinuxAvcLookupState.UNAVAILABLE -> SelinuxAvcLookupCollection.UNAVAILABLE
            SelinuxAvcLookupState.INCONCLUSIVE -> SelinuxAvcLookupCollection.INCONCLUSIVE
        },
        writesPerBatch = snapshot.writesPerBatch,
        batchesA = snapshot.batchesA,
        batchesB = snapshot.batchesB,
        completedRounds = snapshot.rounds,
        pairs = snapshot.pairs,
        medianANs = snapshot.medianANs,
        medianBNs = snapshot.medianBNs,
        medianDeltaNs = snapshot.medianDeltaNs,
        details = SelinuxAvcLookupCollectionDetails(
            step = snapshot.step?.name,
            errno = snapshot.errno,
            unexpectedWrite = snapshot.unexpectedPayload.takeIf { it != SelinuxAvcLookupPayload.NONE }
                ?.let { "${it.name} returned ${snapshot.unexpectedReturned}" },
            identityChanged = snapshot.identityChanged,
            childEnd = snapshot.childEnd?.name,
            childExitStatus = snapshot.childExitStatus,
            childSignal = snapshot.childSignal,
            childErrno = snapshot.childErrno,
            cpu = snapshot.cpu,
            cpuRow = snapshot.cpuRow,
            cpuRows = snapshot.cpuRows,
            possibleCpus = snapshot.possibleCpus,
            failureReason = snapshot.failureReason,
        ),
    )
    return SelinuxCheckResult(
        method = SelinuxOracle.APP_ZYGOTE_AVC_LOOKUPS.label,
        status = reading.label,
        isSecure = null,
        permissionDenied = reading.collection == SelinuxAvcLookupCollection.PERMISSION_LIMITED,
        oracle = SelinuxOracle.APP_ZYGOTE_AVC_LOOKUPS,
        avcLookup = reading,
    )
}
