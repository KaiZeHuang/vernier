package com.kaizehuang.vernier.features.nativeroot.data.native

import com.kaizehuang.vernier.core.native.VernierNativeLibrary
import com.kaizehuang.vernier.core.native.NativePayloadCodec
import com.kaizehuang.vernier.features.nativeroot.domain.SrcuCloseSample
import com.kaizehuang.vernier.features.nativeroot.domain.SrcuCloseStage
import com.kaizehuang.vernier.features.nativeroot.domain.SrcuTimingWindow

internal class SrcuTimingNativeBridge {
    fun measure(directory: String, trigger: Runnable): SrcuTimingWindow {
        check(VernierNativeLibrary.isLoaded) { "Native library unavailable" }
        return parse(nativeMeasureWindow(directory, trigger))
    }
    internal fun parse(raw: String): SrcuTimingWindow {
        require(raw.length <= 32_768) { "Oversized close window" }
        val lines = raw.lineSequence().filter { it.isNotEmpty() }.toList()
        val fields = mutableMapOf<String, String>()
        val samples = mutableListOf<SrcuCloseSample>()
        for (line in lines) {
            require('=' in line) { "Malformed close field" }
            val key = line.substringBefore('=')
            val value = line.substringAfter('=')
            if (key == "SAMPLE") {
                val parts = value.split('\t').map(NativePayloadCodec::decodeValue)
                require(parts.size == 4 && samples.size < 128)
                val stage = parts[2].toInt()
                require(stage in SrcuCloseStage.entries.indices)
                samples += SrcuCloseSample(parts[0].toLong(), parts[1].toLong(),
                    SrcuCloseStage.entries[stage], parts[3].toInt().also { require(it >= 0) })
            } else {
                require(fields.put(key, NativePayloadCodec.decodeValue(value)) == null) { "Duplicate close field" }
            }
        }
        require(fields["VERSION"] == "1" && fields["COUNT"]?.toInt() == samples.size)
        require(fields["SATURATED"] in setOf("0", "1"))
        return SrcuTimingWindow(requireNotNull(fields["BEGIN"]).toLong(),
            requireNotNull(fields["END"]).toLong(), fields["SATURATED"] == "1", samples)
    }
    private external fun nativeMeasureWindow(directory: String, trigger: Runnable): String
}
