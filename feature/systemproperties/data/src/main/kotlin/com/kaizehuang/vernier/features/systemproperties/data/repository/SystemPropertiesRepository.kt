package com.kaizehuang.vernier.features.systemproperties.data.repository

import com.kaizehuang.vernier.capability.systemproperties.data.SystemPropertyConsistencyUtils
import com.kaizehuang.vernier.capability.systemproperties.data.SystemPropertyReadUtils
import com.kaizehuang.vernier.capability.systemproperties.domain.MultiSourcePropertyRead
import com.kaizehuang.vernier.capability.systemproperties.domain.SystemPropertyCategory
import com.kaizehuang.vernier.capability.systemproperties.domain.SystemPropertySignal
import com.kaizehuang.vernier.capability.systemproperties.domain.SystemPropertySource
import com.kaizehuang.vernier.core.detector.DetectorScanner
import com.kaizehuang.vernier.features.systemproperties.data.rules.SystemPropertiesCatalog
import com.kaizehuang.vernier.features.systemproperties.domain.SystemPropertiesReport
import com.kaizehuang.vernier.features.systemproperties.domain.SystemPropertiesStage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SystemPropertiesRepository(
    internal val readUtils: SystemPropertyReadUtils = SystemPropertyReadUtils(),
    private val consistencyUtils: SystemPropertyConsistencyUtils = SystemPropertyConsistencyUtils(),
) : DetectorScanner<SystemPropertiesReport> {
    private val propertyAreaMtimeProbe = PropertyAreaMtimeProbe()

    override suspend fun scan(): SystemPropertiesReport = withContext(Dispatchers.IO) {
        runCatching { scanInternal() }
            .getOrElse { throwable ->
                SystemPropertiesReport.failed(throwable.message ?: "System Properties scan failed.")
            }
    }

    private fun scanInternal(): SystemPropertiesReport {
        val trackedProperties =
            (SystemPropertiesCatalog.rules.map { it.property } + SystemPropertiesCatalog.infoProperties)
                .distinct()
        val nativeSnapshot = readUtils.collectNativeSnapshot(trackedProperties)
        val propertyCache = linkedMapOf<String, MultiSourcePropertyRead>()
        val ruleSignals = mutableListOf<SystemPropertySignal>()
        val infoSignals = mutableListOf<SystemPropertySignal>()

        SystemPropertiesCatalog.rules.forEach { rule ->
            if (rule.property == SERVICE_ADB_ROOT) {
                return@forEach
            }
            val read = readUtils.readProperty(
                property = rule.property,
                category = rule.category,
                cache = propertyCache,
                nativeSnapshot = nativeSnapshot,
            )
            if (read.preferredValue.isBlank()) {
                return@forEach
            }
            ruleSignals += buildRuleSignal(rule, read)
        }

        buildAdbRootSignal(propertyCache, nativeSnapshot)?.let(ruleSignals::add)

        SystemPropertiesCatalog.infoProperties.forEach { property ->
            val read = readUtils.readProperty(
                property = property,
                category = infoCategory(property),
                cache = propertyCache,
                nativeSnapshot = nativeSnapshot,
            )
            if (read.preferredValue.isBlank()) {
                return@forEach
            }
            if (ruleSignals.none { it.property == property }) {
                infoSignals += buildInfoSignal(property, read)
            }
        }

        val buildSignals = buildBuildConstantSignals()
        val sourceSignals = consistencyUtils.buildSourceMismatchSignals(propertyCache.values)
        val consistencySignals = consistencyUtils.buildConsistencySignals(
            readsByProperty = propertyCache,
            nativeSnapshot = nativeSnapshot,
        )
        val propAreaSignals = buildPropAreaSignals(nativeSnapshot)

        val propertySignals =
            ruleSignals +
                    buildSignals +
                    sourceSignals +
                    consistencySignals
        if (
            propertySignals.isEmpty() &&
            propAreaSignals.isEmpty() &&
            infoSignals.isEmpty() &&
            !nativeSnapshot.propAreaAvailable
        ) {
            return SystemPropertiesReport.failed(
                "No readable system properties, raw boot parameters, or build constants were collected.",
            )
        }

        val observedRuleCount =
            ruleSignals.count { it.category != SystemPropertyCategory.SOURCE_CONSISTENCY }
        val reflectionHitCount = propertyCache.values.count {
            it.sourceValues[SystemPropertySource.REFLECTION].isNullOrBlank().not()
        }
        val getpropHitCount = propertyCache.values.count {
            it.sourceValues[SystemPropertySource.GETPROP].isNullOrBlank().not()
        }
        val jvmHitCount = propertyCache.values.count {
            it.sourceValues[SystemPropertySource.JVM].isNullOrBlank().not()
        }

        return SystemPropertiesReport(
            stage = SystemPropertiesStage.READY,
            propertySignals = propertySignals,
            propAreaSignals = propAreaSignals,
            infoSignals = infoSignals,
            checkedRuleCount = SystemPropertiesCatalog.rules.size,
            observedRuleCount = observedRuleCount,
            infoPropertyCount = infoSignals.size,
            reflectionHitCount = reflectionHitCount,
            getpropHitCount = getpropHitCount,
            jvmHitCount = jvmHitCount,
            nativeHitCount = nativeSnapshot.nativePropertyHitCount,
            bootParamHitCount = nativeSnapshot.bootParamHitCount,
            buildSignalCount = buildSignals.size,
            propAreaAvailable = nativeSnapshot.propAreaAvailable,
            propAreaContextCount = nativeSnapshot.propAreaContextCount,
            propAreaHoleCount = nativeSnapshot.propAreaHoleCount,
            methods = buildMethods(
                ruleSignals = ruleSignals,
                infoSignals = infoSignals,
                reflectionHitCount = reflectionHitCount,
                getpropHitCount = getpropHitCount,
                jvmHitCount = jvmHitCount,
                nativeHitCount = nativeSnapshot.nativePropertyHitCount,
                bootParamHitCount = nativeSnapshot.bootParamHitCount,
                observedRuleCount = observedRuleCount,
                buildSignals = buildSignals,
                sourceSignals = sourceSignals,
                consistencySignals = consistencySignals,
                propAreaSignals = propAreaSignals,
                propAreaAvailable = nativeSnapshot.propAreaAvailable,
                propAreaContextCount = nativeSnapshot.propAreaContextCount,
                propAreaHoleCount = nativeSnapshot.propAreaHoleCount,
            ),
            propertyAreaMtimes = propertyAreaMtimeProbe.read(),
        )
    }

    private fun infoCategory(
        property: String,
    ): SystemPropertyCategory {
        return if (property.contains("fingerprint", ignoreCase = true)) {
            SystemPropertyCategory.BUILD_FINGERPRINT
        } else {
            SystemPropertyCategory.DEVICE_INFO
        }
    }

}
