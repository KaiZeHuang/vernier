package com.kaizehuang.vernier.features.tee.domain

enum class TeeSignalLevel {
    PASS,
    INFO,
    WARN,
    FAIL,
}

data class TeeSignal(
    val label: String,
    val value: String,
    val level: TeeSignalLevel,
)
