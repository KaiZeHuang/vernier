package com.kaizehuang.vernier.testhelpers.suspicious

class LsposedInjectedHandler : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(
        thread: Thread,
        throwable: Throwable,
    ) = Unit
}
