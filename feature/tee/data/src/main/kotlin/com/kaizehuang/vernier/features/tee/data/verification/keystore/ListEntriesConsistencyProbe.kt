package com.kaizehuang.vernier.features.tee.data.verification.keystore

import android.os.BadParcelableException
import android.os.Build
import com.kaizehuang.vernier.capability.attestation.data.AndroidKeyStoreTools
import com.kaizehuang.vernier.core.platform.PlatformFailureName
import java.security.KeyStore

class ListEntriesConsistencyProbe {

    fun inspect(): ListEntriesConsistencyResult {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return ListEntriesConsistencyResult(
                executed = false,
                detail = "listEntries consistency probe requires Android 12 or newer.",
            )
        }
        val keyStore = AndroidKeyStoreTools.loadKeyStore()
        val alias = "vernier_list_entries_${System.nanoTime()}"
        return try {
            AndroidKeyStoreTools.generateSigningEcKey(
                keyStore = keyStore,
                alias = alias,
                subject = "CN=Vernier ListEntries, O=Vernier",
                useStrongBox = false,
            )
            val containsAlias = keyStore.containsAlias(alias)
            val aliases = buildList {
                val enumeration = keyStore.aliases()
                while (enumeration.hasMoreElements()) {
                    add(enumeration.nextElement())
                }
            }
            val listedInAliases = aliases.contains(alias)
            ListEntriesConsistencyResult(
                executed = true,
                containsAlias = containsAlias,
                listedInAliases = listedInAliases,
                inconsistent = containsAlias != listedInAliases,
                detail = "containsAlias=$containsAlias, listedInAliases=$listedInAliases, aliasCount=${aliases.size}",
            )
        } catch (throwable: Throwable) {
            val badParcelableLikeCrash = isBadParcelableLikeCrash(throwable)
            ListEntriesConsistencyResult(
                executed = badParcelableLikeCrash,
                badParcelableLikeCrash = badParcelableLikeCrash,
                inconsistent = badParcelableLikeCrash,
                detail = if (badParcelableLikeCrash) {
                    "aliases() crashed with a BadParcelable-style failure: ${throwable.message ?: PlatformFailureName.of(throwable)}"
                } else {
                    throwable.message ?: "listEntries consistency probe could not complete."
                },
            )
        }.also {
            AndroidKeyStoreTools.safeDelete(keyStore, alias)
        }
    }

    private fun isBadParcelableLikeCrash(throwable: Throwable): Boolean {
        var current: Throwable? = throwable
        while (current != null) {
            val message = current.message.orEmpty()
            if (current is BadParcelableException ||
                message.contains("BadParcelableException") ||
                message.contains("Parcelable too small") ||
                message.contains("KeyDescriptor.readFromParcel")
            ) {
                return true
            }
            current = current.cause
        }
        return false
    }
}

data class ListEntriesConsistencyResult(
    val executed: Boolean,
    val containsAlias: Boolean? = null,
    val listedInAliases: Boolean? = null,
    val badParcelableLikeCrash: Boolean = false,
    val inconsistent: Boolean = false,
    val detail: String,
)
