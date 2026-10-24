package com.kaizehuang.vernier.features.selinux.domain

/** The app zygote oracles whose results the report reads beyond their text; [label] is the method each is listed under. */
enum class SelinuxOracle(val label: String) {
    CONTEXT_VALIDITY("Context validity oracle"),
    PROC_ATTR_CURRENT_WRITE("app_zygote attr/current write"),
    POLICYLOAD_SEQNO("App-zygote seqno oracle"),
    SIDTAB_CONSISTENCY("SID-table query consistency"),
    ATTR_CURRENT_TIMING("SELinux Hide attr/current timing (experimental)"),
    APP_ZYGOTE_AVC_LOOKUPS("App Zygote AVC lookup profile (experimental)"),
}

/** The context validity oracle's reading of the two KSU-specific contexts; [label] is the status it is shown with. */
enum class SelinuxContextValidityVerdict(val label: String) {
    CLEAN("00"),
    KSU_PRESENT("11"),
    AMBIGUOUS("01/10"),
    SELF_TEST_FAILED("Self-test failed"),
    UNSUPPORTED("Unsupported"),
}

/** The context validity oracle's verdict and the state of the app zygote carrier it ran in. */
data class SelinuxContextValidityReading(
    val verdict: SelinuxContextValidityVerdict,
    val carrier: AppZygoteCarrierSupportState,
    /**
     * Whether the oracle's repeated writes may have disagreed. The oracle reports a failed self-test
     * and unstable repeats as one untrusted state, so every self-test failure sets this.
     */
    val repeatabilityFailed: Boolean = false,
)

/**
 * The controlled app_zygote attr/current probe's reading; [label] is the status it is shown with.
 * Only [CONTEXT_RECOGNIZED] is evidence: the others, including [NOT_RECOGNIZED], cannot exclude a
 * hidden policy.
 */
enum class SelinuxProcAttrCurrentVerdict(val label: String) {
    CONTEXT_RECOGNIZED("Context recognized"),
    NOT_RECOGNIZED("Tested contexts not recognized"),
    PERMISSION_LIMITED("Permission limited"),
    UNSUPPORTED("Unsupported"),
    UNAVAILABLE("Unavailable"),
    INCONCLUSIVE("Inconclusive"),
}

object SelinuxPolicyloadSeqnoLabels {
    const val STATUS_CLEAN = "Clean"
    const val STATUS_SUSPICIOUS = "Seqno split"
    const val STATUS_PAGE_FAULTED = "Status page faulted"
    const val STATUS_INCONCLUSIVE = "Info"
    const val STATUS_UNAVAILABLE = "Unavailable"
}

/** The checkers whose SELinux policy rule queries the report lists; [prefix] starts each query's method. */
enum class SelinuxPolicyRuleSet(val prefix: String) {
    DIRTY_SEPOLICY("Dirty sepolicy rule: "),
    POLICY_OBSERVATION("SELinux policy observation: "),
    DROIDSPACES("Droidspaces checker: "),
    MSD("MSD checker: "),
}

/** Live policy's answer to a rule query; [label] is the status it is shown with. */
enum class SelinuxRuleVerdict(val label: String) {
    ALLOWED("Allowed"),
    DENIED("Denied"),
    UNAVAILABLE("Unavailable"),
}

/** One policy rule query: the checker that asked, the queried edge and live policy's answer. */
data class SelinuxPolicyRule(
    val set: SelinuxPolicyRuleSet,
    val edge: String,
    val verdict: SelinuxRuleVerdict,
) {
    val method: String get() = set.prefix + edge
}
