package android.os

// Stand-in for the hidden ServiceManager, which android.jar does not contain.
class ServiceManager {
    companion object {
        var services: Map<String, IBinder> = emptyMap()
        var listed: Array<Any?> = emptyArray()

        @JvmStatic
        fun getService(name: String): IBinder? = services[name]

        @JvmStatic
        fun listServices(): Array<Any?> = listed
    }
}
