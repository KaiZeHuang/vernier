package com.kaizehuang.vernier.features.rootmanagers.domain

/**
 * How strongly the matched anchors identify a root manager; one surviving strong anchor already
 * outranks any number of weak ones, so a hit never reads stronger than what actually matched.
 */
enum class RootManagerConfidence(
    val displayName: String,
) {
    HIGH("high"),
    MEDIUM("medium"),
    LOW("low"),
}
