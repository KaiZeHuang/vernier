package com.kaizehuang.vernier.features.selinux.domain

enum class SelinuxAuditIntegrityState {
    CLEAR,
    RESIDUE,
    EXPOSED,
    TAMPERED,
    INCONCLUSIVE,
}

data class SelinuxAuditEvidence(
    val label: String,
    val value: String,
    val detail: String? = null,
    val strongSignal: Boolean = false,
)

/** What an audit integrity note says. */
enum class SelinuxAuditNoteKind {
    PROBES_EXPOSED_TAMPERING,
    SIDE_CHANNEL_LEAK,
    DIRECT_PROBE_NOT_LEAKED,
    EVENT_BUFFER_CLEAN,
    EVENTS_NOT_GUARANTEED,
    EVENT_LOGS_UNAVAILABLE,
    SU_ACTOR_REFERENCED,
    RESIDUE_FOUND,
    NO_RESIDUE,
    RESIDUE_UNCHECKED,
    ABSENCE_NOT_PROOF,
}

data class SelinuxAuditNote(
    val kind: SelinuxAuditNoteKind,
    val text: String,
)

data class SelinuxAuditIntegrityAnalysis(
    val state: SelinuxAuditIntegrityState,
    val residueHits: List<SelinuxAuditEvidence>,
    /** False when a residue location could not be stat-ed, so finding none does not show absence. */
    val residueObservable: Boolean = true,
    val runtimeHits: List<SelinuxAuditEvidence>,
    val sideChannelHits: List<SelinuxAuditEvidence>,
    val suspiciousActorHits: List<SelinuxAuditEvidence> = emptyList(),
    val logcatChecked: Boolean,
    val directProbeUsed: Boolean = false,
    val notes: List<SelinuxAuditNote>,
)
