package com.kaizehuang.vernier.features.tee.data.verification.keystore

import com.kaizehuang.vernier.core.platform.HiddenSystemProperties
import com.kaizehuang.vernier.core.platform.PlatformFailureName

fun interface VendorApiLevelReader {
    fun read(): VendorApiLevelResult
}

data class VendorApiLevelResult(
    val level: Int?,
    val detail: String,
) {
    companion object {
        fun unused(): VendorApiLevelResult = VendorApiLevelResult(
            level = null,
            detail = "Vendor API level was not required.",
        )
    }
}

internal fun resolveVendorApiLevel(readProperty: (String) -> String?): VendorApiLevelResult {
    // This intentionally mirrors KeyMint VTS get_vendor_api_level(), rather than using
    // Build.VERSION.SDK_INT or a different init/libvendorsupport interpretation:
    //   ro.vendor.api_level
    //   else ro.board.api_level
    //   else ro.board.first_api_level
    //   productApi = ro.product.first_api_level ?: ro.build.version.sdk
    //   if boardApi is absent, return productApi; otherwise return min(productApi, boardApi).
    // 这里刻意复刻 KeyMint VTS 的 get_vendor_api_level()，不能用 Build.VERSION.SDK_INT 或另一套
    // init/libvendorsupport 语义替代：vendor 属性优先；board 两个属性缺失时直接返回 product；
    // board 存在时返回 min(productApi, boardApi)。
    //
    // AOSP reference:
    // hardware/interfaces/security/keymint/aidl/vts/functional/KeyMintAidlTestBase.cpp
    // https://android.googlesource.com/platform/hardware/interfaces/+/refs/heads/main/security/keymint/aidl/vts/functional/KeyMintAidlTestBase.cpp
    fun property(name: String): Int? = readProperty(name)
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?.toIntOrNull()
        ?.takeIf { it >= 0 }

    property("ro.vendor.api_level")?.let { level ->
        return VendorApiLevelResult(level, "source=ro.vendor.api_level")
    }

    val boardApiLevel = property("ro.board.api_level")
        ?: property("ro.board.first_api_level")
    val productApiLevel = property("ro.product.first_api_level")
        ?: property("ro.build.version.sdk")
        ?: return VendorApiLevelResult(
            level = null,
            detail = "Missing ro.product.first_api_level and ro.build.version.sdk.",
        )

    return if (boardApiLevel == null) {
        VendorApiLevelResult(productApiLevel, "source=productApi, boardApi=absent")
    } else {
        VendorApiLevelResult(
            level = minOf(productApiLevel, boardApiLevel),
            detail = "source=min(productApi=$productApiLevel, boardApi=$boardApiLevel)",
        )
    }
}

internal class ReflectionVendorApiLevelReader : VendorApiLevelReader {
    override fun read(): VendorApiLevelResult {
        return runCatching {
            resolveVendorApiLevel { name -> HiddenSystemProperties.read(name).getOrThrow() }
        }.getOrElse { throwable ->
            VendorApiLevelResult(
                level = null,
                detail = "SystemProperties unavailable: ${PlatformFailureName.of(throwable)}: " +
                    (throwable.message ?: "no message"),
            )
        }
    }
}
