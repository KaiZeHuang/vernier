package com.kaizehuang.vernier.capability.selinuxpolicy.data

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.Parcel
import android.system.Os

public class SelinuxContextValidityCarrierService : Service() {

    private val binder = object : Binder() {
        override fun onTransact(
            code: Int,
            data: Parcel,
            reply: Parcel?,
            flags: Int,
        ): Boolean {
            return when (code) {
                INTERFACE_TRANSACTION -> {
                    reply?.writeString(SelinuxContextValidityCarrierProtocol.DESCRIPTOR)
                    true
                }

                SelinuxContextValidityCarrierProtocol.TRANSACTION_COLLECT_SNAPSHOT -> {
                    data.enforceInterface(SelinuxContextValidityCarrierProtocol.DESCRIPTOR)
                    reply?.writeNoException()
                    reply?.writeString(buildSnapshotPayload())
                    true
                }

                else -> super.onTransact(code, data, reply, flags)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    private fun buildSnapshotPayload(): String {
        return runCatching {
            resolveCarrierPayload(
                consumePreloadedRawData = SelinuxContextValidityBridge::consumePreloadedRawDataForCarrier,
            )
        }.getOrElse { throwable ->
            carrierFailurePayload(
                throwable.message ?: "SELinux carrier probe failed.",
                "SELinux carrier probe crashed before returning the preloaded payload.",
            )
        }
    }

    public companion object {
        @Volatile
        private var cachedPreloadedPayload: String? = null

        internal fun resolveCarrierPayload(
            consumePreloadedRawData: () -> String?,
        ): String {
            cachedPreloadedPayload?.let { return it }
            val raw = consumePreloadedRawData()
            if (!raw.isNullOrBlank()) {
                cachedPreloadedPayload = raw
                return raw
            }
            return carrierFailurePayload(
                reason = "Dedicated app_zygote preload payload unavailable.",
                note = "SELinux carrier service did not receive a preloaded app_zygote payload.",
            )
        }

        internal fun carrierFailurePayload(
            reason: String,
            note: String,
        ): String {
            return SelinuxContextValidityPayloadCodec.encode(
                SelinuxContextValiditySnapshot(
                    dirtyPolicyFailureReason = reason,
                    javaDirtyPolicyFailureReason = reason,
                    policyloadSeqnoState = SelinuxPolicyloadSeqnoState.UNAVAILABLE.name,
                    policyloadSeqnoFailureReason = reason,
                    procAttrCurrentFailureReason = reason,
                    failureReason = reason,
                    dirtyPolicyNotes = listOf(note),
                    javaDirtyPolicyNotes = listOf(note),
                    policyloadSeqnoNotes = listOf(note),
                    notes = listOf(note),
                ),
            )
        }

        internal fun clearCachedPreloadedPayloadForTests() {
            cachedPreloadedPayload = null
        }

        internal fun procAttrCurrentGateFailureReason(
            snapshot: SelinuxContextValiditySnapshot,
            appUid: Int?,
            uid: Int,
        ): String? {
            if (appUid == null) {
                return "Application UID unavailable for app_zygote attr/current probe."
            }
            if (uid != appUid) {
                return "UID mismatch: $uid != app uid $appUid"
            }
            if (!snapshot.available) {
                return snapshot.failureReason ?: "Carrier SELinux context was unreadable."
            }
            if (snapshot.selinuxEnabled == false) {
                return "SELinux is disabled in the carrier context."
            }
            if (snapshot.selinuxEnforced == false) {
                return "SELinux is permissive in the carrier context."
            }
            if (!snapshot.carrierMatchesExpected) {
                return "Carrier self-check did not land in app_zygote context."
            }
            if (snapshot.carrierContext?.startsWith("u:r:app_zygote:s0") == false) {
                return "Carrier self-check did not match the expected app_zygote context prefix."
            }
            if (snapshot.pidContextMatchesCurrent == false) {
                return "Carrier pid context did not match the current process context."
            }
            if (snapshot.procSelfContextMatchesCurrent == false) {
                return "Carrier /proc/self context did not match the current process context."
            }
            if (snapshot.dyntransitionCheckPassed == false) {
                return "Carrier dyntransition self-check failed."
            }
            return null
        }
    }
}
