package com.kaizehuang.vernier.capability.selinuxpolicy.data

public enum class DedicatedCarrierState(
    public val label: String,
) {
    OK("ok"),
    FAILED("failed"),
    UNTRUSTED("untrusted"),
}
