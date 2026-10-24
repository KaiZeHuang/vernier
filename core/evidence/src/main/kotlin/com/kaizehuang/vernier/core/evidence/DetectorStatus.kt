package com.kaizehuang.vernier.core.evidence

@ContractValue
public class DetectorStatus(
    public val severity: DetectionSeverity,
    public val infoKind: InfoKind? = null,
) {
    init {
        require(severity == DetectionSeverity.INFO || infoKind == null) {
            "Only INFO status can carry an InfoKind"
        }
    }

    public companion object {
        public fun info(kind: InfoKind): DetectorStatus = DetectorStatus(DetectionSeverity.INFO, kind)
        public fun allClear(): DetectorStatus = DetectorStatus(DetectionSeverity.ALL_CLEAR)
        public fun warning(): DetectorStatus = DetectorStatus(DetectionSeverity.WARNING)
        public fun danger(): DetectorStatus = DetectorStatus(DetectionSeverity.DANGER)
    }
}
