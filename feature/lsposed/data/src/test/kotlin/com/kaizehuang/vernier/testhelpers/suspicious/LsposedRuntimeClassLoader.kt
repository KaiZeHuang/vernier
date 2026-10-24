package com.kaizehuang.vernier.testhelpers.suspicious

import com.kaizehuang.vernier.testhelpers.clean.NeutralLoader

class LsposedRuntimeClassLoader(
    parent: ClassLoader?,
) : NeutralLoader(parent)
