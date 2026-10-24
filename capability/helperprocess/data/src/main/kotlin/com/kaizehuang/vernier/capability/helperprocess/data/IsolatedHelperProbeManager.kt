package com.kaizehuang.vernier.capability.helperprocess.data

import android.content.Context

public open class IsolatedHelperProbeManager(
    context: Context? = null,
) : HelperProbeManager(
    context = context,
    serviceClass = IsolatedHelperProbeService::class.java,
    expectedProfile = HelperProcessProfile.ISOLATED,
)
