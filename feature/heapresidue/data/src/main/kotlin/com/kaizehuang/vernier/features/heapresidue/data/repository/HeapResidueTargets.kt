package com.kaizehuang.vernier.features.heapresidue.data.repository

/** A small exact-name policy. Names can be spoofed or repackaged; they do not establish app identity. */
internal object HeapResidueTargets {
    val packages: Set<String> = setOf(
        "com.topjohnwu.magisk", "io.github.vvb2060.magisk",
        "me.weishu.kernelsu", "me.weishu.kernelsu.pr", "io.github.a13e300.mksu", "com.rifsxd.ksunext",
        "com.sukisu.ultra", "com.resukisu.resukisu", "me.bmax.apatch",
        "org.lsposed.manager", "org.lsposed.lspatch", "de.robv.android.xposed.installer",
        "com.tsng.hidemyapplist", "com.tsng.pzyhrx.hma",
    )
}
