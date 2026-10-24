package android.os

// Stand-ins for hidden platform failures, which android.jar does not contain even as stubs.

class ServiceSpecificException(
    @JvmField val errorCode: Int,
    message: String? = null,
) : RuntimeException(message)

class ParcelableException(cause: Throwable) : RuntimeException(cause)

class DeadSystemRuntimeException : RuntimeException()
