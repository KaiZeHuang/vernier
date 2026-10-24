package com.kaizehuang.vernier.capability.systemproperties.domain

public data class MultiSourcePropertyRead(
    val property: String,
    val category: SystemPropertyCategory,
    val preferredValue: String,
    val preferredSource: SystemPropertySource,
    val sourceValues: Map<SystemPropertySource, String>,
)
