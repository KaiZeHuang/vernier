package com.kaizehuang.vernier.features.rootmanagers.domain

/**
 * What one family looks like through the fields of an installed app.
 *
 * Each set matches one field and is independent evidence (see [RootManagerEntryRules]); a set is
 * empty when the family has no authoritative value for that field. Names compare on whole dotted
 * segments, so `me.weishu.kernelsu` never matches `me.weishu.kernelsufoo`.
 */
data class RootManagerSignature(
    val family: RootManagerFamily,
    /** Exact package names; a dotted build variant such as `com.sukisu.ultra.pr` also matches. */
    val packageNames: Set<String> = emptySet(),
    /**
     * The family's code namespace. A build that only changes `applicationId` keeps it inside every
     * manifest class name, so an application class or zygote preload under it still matches.
     */
    val codeNamespaces: Set<String> = emptySet(),
    val applicationClassSuffixes: Set<String> = emptySet(),
    /**
     * `Application` simple names too generic to stand alone outside the family namespace, such as
     * APatch's `.APApplication`; they corroborate as a weak anchor instead of a strong one.
     */
    val genericApplicationClassSuffixes: Set<String> = emptySet(),
    val zygotePreloadNameSuffixes: Set<String> = emptySet(),
    val labelPrefixes: Set<String> = emptySet(),
    val launcherClassSuffixes: Set<String> = emptySet(),
    /** Manager keys the family's kernel trusts, taken from that kernel's own build defaults. */
    val signingCertificates: Set<CertificateFingerprint> = emptySet(),
    /**
     * Native libraries the manager ships and executes. A name another lineage also ships, such as
     * `libbusybox.so` or `libmagiskpolicy.so` (APatch runs both), is left out, so a payload never
     * points at the wrong lineage; forks of one lineage may share a name like `libksud.so`.
     */
    val nativePayloads: Set<String> = emptySet(),
)
