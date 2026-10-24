package com.kaizehuang.vernier.features.rootmanagers.domain

/**
 * A root manager family, kept separate from the others by its own catalogue entry.
 *
 * KernelSU and its forks name their `Application` class `…KernelSUApplication` and APatch names its
 * own `…APApplication`, so a rename can leave that class naming a family the package name no longer
 * does. Each KernelSU fork's kernel trusts its own manager key, which is what separates the forks
 * once their shared class names stop doing so.
 */
enum class RootManagerFamily(
    val displayName: String,
) {
    KERNEL_SU("KernelSU"),
    KERNEL_SU_NEXT("KernelSU-Next"),
    SUKI_SU("SukiSU Ultra"),

    /** ReSukiSU continues upstream as BakaSU, which still ships the `com.resukisu.resukisu` package. */
    RE_SUKI_SU("ReSukiSU (BakaSU)"),
    APATCH("APatch"),
    SK_ROOT("SKRoot"),
    MAGISK("Magisk"),
}
