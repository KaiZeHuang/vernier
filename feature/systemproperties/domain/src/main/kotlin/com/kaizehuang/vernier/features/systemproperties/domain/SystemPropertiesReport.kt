package com.kaizehuang.vernier.features.systemproperties.domain

import com.kaizehuang.vernier.capability.systemproperties.domain.SystemPropertyCategory
import com.kaizehuang.vernier.capability.systemproperties.domain.SystemPropertySeverity
import com.kaizehuang.vernier.capability.systemproperties.domain.SystemPropertySignal

enum class SystemPropertiesStage {
    LOADING,
    READY,
    FAILED,
}

enum class SystemPropertiesMethodOutcome {
    CLEAN,
    WARNING,
    DANGER,
    SUPPORT,
}

data class SystemPropertiesMethodResult(
    val label: String,
    val summary: String,
    val outcome: SystemPropertiesMethodOutcome,
    val detail: String? = null,
)

data class SystemPropertiesReport(
    val stage: SystemPropertiesStage,
    /** Signals about individual properties, their sources and how those sources agree. */
    val propertySignals: List<SystemPropertySignal>,
    /** One signal per property context whose raw property area has holes. */
    val propAreaSignals: List<SystemPropertySignal>,
    val infoSignals: List<SystemPropertySignal>,
    val checkedRuleCount: Int,
    val observedRuleCount: Int,
    val infoPropertyCount: Int,
    val reflectionHitCount: Int,
    val getpropHitCount: Int,
    val jvmHitCount: Int,
    val nativeHitCount: Int,
    val bootParamHitCount: Int,
    val buildSignalCount: Int,
    val propAreaAvailable: Boolean,
    val propAreaContextCount: Int,
    val propAreaHoleCount: Int,
    val methods: List<SystemPropertiesMethodResult>,
    val errorMessage: String? = null,
    /** Diagnostic only: the status, verdict and review counts never read it. */
    val propertyAreaMtimes: List<PropertyAreaMtime> = emptyList(),
) {
    val signals: List<SystemPropertySignal>
        get() = propertySignals + propAreaSignals

    val dangerSignals: List<SystemPropertySignal>
        get() = signals.filter { it.severity == SystemPropertySeverity.DANGER }

    val warningSignals: List<SystemPropertySignal>
        get() = signals.filter { it.severity == SystemPropertySeverity.WARNING }

    val hasDangerSignals: Boolean
        get() = dangerSignals.isNotEmpty()

    val hasWarningSignals: Boolean
        get() = warningSignals.isNotEmpty()

    val bootSignalCount: Int
        get() = signals.count {
            (it.category == SystemPropertyCategory.VERIFIED_BOOT ||
                    it.category == SystemPropertyCategory.PARTITION_VERITY) &&
                    it.severity != SystemPropertySeverity.SAFE &&
                    it.severity != SystemPropertySeverity.NEUTRAL
        }

    val buildProfileSignalCount: Int
        get() = signals.count {
            it.category == SystemPropertyCategory.BUILD_PROFILE &&
                    it.severity != SystemPropertySeverity.SAFE &&
                    it.severity != SystemPropertySeverity.NEUTRAL
        }

    val sourceMismatchCount: Int
        get() = signals.count { it.category == SystemPropertyCategory.SOURCE_CONSISTENCY }

    val consistencySignalCount: Int
        get() = signals.count { it.category == SystemPropertyCategory.PROPERTY_CONSISTENCY }

    val runtimeSignalCount: Int
        get() = signals.count {
            (it.category == SystemPropertyCategory.SECURITY_CORE ||
                    it.category == SystemPropertyCategory.ROOT_RUNTIME ||
                    it.category == SystemPropertyCategory.CUSTOM_ROM) &&
                    it.severity != SystemPropertySeverity.SAFE &&
                    it.severity != SystemPropertySeverity.NEUTRAL
        }

    companion object {
        fun loading(): SystemPropertiesReport {
            return SystemPropertiesReport(
                stage = SystemPropertiesStage.LOADING,
                propertySignals = emptyList(),
                propAreaSignals = emptyList(),
                infoSignals = emptyList(),
                checkedRuleCount = 0,
                observedRuleCount = 0,
                infoPropertyCount = 0,
                reflectionHitCount = 0,
                getpropHitCount = 0,
                jvmHitCount = 0,
                nativeHitCount = 0,
                bootParamHitCount = 0,
                buildSignalCount = 0,
                propAreaAvailable = false,
                propAreaContextCount = 0,
                propAreaHoleCount = 0,
                methods = emptyList(),
            )
        }

        fun failed(message: String): SystemPropertiesReport {
            return SystemPropertiesReport(
                stage = SystemPropertiesStage.FAILED,
                propertySignals = emptyList(),
                propAreaSignals = emptyList(),
                infoSignals = emptyList(),
                checkedRuleCount = 0,
                observedRuleCount = 0,
                infoPropertyCount = 0,
                reflectionHitCount = 0,
                getpropHitCount = 0,
                jvmHitCount = 0,
                nativeHitCount = 0,
                bootParamHitCount = 0,
                buildSignalCount = 0,
                propAreaAvailable = false,
                propAreaContextCount = 0,
                propAreaHoleCount = 0,
                methods = emptyList(),
                errorMessage = message,
            )
        }
    }
}
