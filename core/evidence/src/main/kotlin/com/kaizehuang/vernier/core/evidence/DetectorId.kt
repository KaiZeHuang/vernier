package com.kaizehuang.vernier.core.evidence

/**
 * Stable identity of a detector.
 *
 * Titles are presentation text and may change with wording or localisation; anything that needs
 * to recognise a detector (ordering, exported sections) keys on this instead.
 */
@JvmInline
public value class DetectorId(public val value: String) {
    init {
        require(FORMAT.matches(value)) { "Detector ids are lowercase snake_case, got '$value'" }
    }

    override fun toString(): String = value

    private companion object {
        val FORMAT = Regex("[a-z][a-z0-9]*(_[a-z0-9]+)*")
    }
}
