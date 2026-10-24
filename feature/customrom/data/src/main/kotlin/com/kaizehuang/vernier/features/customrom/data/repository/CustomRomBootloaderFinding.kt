package com.kaizehuang.vernier.features.customrom.data.repository

import com.kaizehuang.vernier.features.customrom.domain.CustomRomModificationFinding

private val UNLOCKED_VALUES = setOf("0", "unlocked", "false")

/**
 * An unlocked bootloader as ro.boot.flash.locked states it, or null.
 *
 * init copies the bootloader's androidboot.flash.locked into ro.boot.flash.locked, and bootloaders
 * are not required to pass it, so an empty or unreadable value says nothing about the lock state.
 */
internal fun bootloaderUnlockFinding(lockState: String?): CustomRomModificationFinding? {
    val value = lockState?.trim().orEmpty()
    if (value.lowercase() !in UNLOCKED_VALUES) {
        return null
    }
    return CustomRomModificationFinding(
        category = "Bootloader",
        signal = "ro.boot.flash.locked",
        summary = "Unlocked bootloader",
        detail = "ro.boot.flash.locked=$value",
    )
}
