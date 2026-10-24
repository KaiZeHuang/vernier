package com.kaizehuang.vernier.core.report

import com.kaizehuang.vernier.core.evidence.ContractValue
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.evidence.DetectorStatus

/**
 * The outcome of one detector run without any UI: which detector ran, its verdict, and its report.
 *
 * [status] is the detector's own verdict, so a caller can act on it without reading the report's
 * wording, and [report] is the same structured report the export renders.
 */
@ContractValue
public class DetectorResult(
    public val id: DetectorId,
    public val status: DetectorStatus,
    public val report: DetectorReport,
)
