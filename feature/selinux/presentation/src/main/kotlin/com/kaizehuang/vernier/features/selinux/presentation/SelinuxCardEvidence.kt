package com.kaizehuang.vernier.features.selinux.presentation

import com.kaizehuang.vernier.features.selinux.domain.SelinuxCheckResult
import com.kaizehuang.vernier.features.selinux.domain.SelinuxPolicyRuleSet
private fun policyRuleDisplayName(result: SelinuxCheckResult): String {
    val rule = result.policyRule ?: return result.method
    return when (rule.set) {
        SelinuxPolicyRuleSet.DIRTY_SEPOLICY -> rule.edge
        SelinuxPolicyRuleSet.DROIDSPACES -> "Droidspaces: ${rule.edge}"
        SelinuxPolicyRuleSet.MSD -> "MSD: ${rule.edge}"
        SelinuxPolicyRuleSet.POLICY_OBSERVATION -> result.method
    }
}

internal fun trustedPolicyRuleVerdict(): String {
    return "Enforcing with dirty sepolicy rule"
}

internal fun trustedPolicyRuleSummary(result: SelinuxCheckResult): String {
    return "A trusted DirtySepolicy-style access query reported ${policyRuleDisplayName(result)} as allowed."
}

internal fun trustedPolicyRuleImpact(result: SelinuxCheckResult): String {
    return "A trusted DirtySepolicy-style access rule was allowed: ${policyRuleDisplayName(result)}."
}
