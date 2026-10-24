package com.kaizehuang.vernier.features.deviceinfo.domain

/** Port through which the detector collects a fresh [DeviceInfoReport]; implemented by the data layer. */
fun interface DeviceInfoScanner {
    suspend fun scan(): DeviceInfoReport
}
