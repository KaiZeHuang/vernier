package com.kaizehuang.vernier.testhelpers.clean

class NeutralExceptionHandler : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(
        thread: Thread,
        throwable: Throwable,
    ) = Unit
}
