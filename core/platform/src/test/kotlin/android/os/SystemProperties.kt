package android.os

// Stand-in for the hidden SystemProperties, which android.jar does not contain. It records which
// overload was called so the tests can tell them apart.
class SystemProperties {
    companion object {
        var values: Map<String, String> = emptyMap()
        var lastOverload: String? = null

        @JvmStatic
        fun get(key: String): String {
            lastOverload = "get(key)"
            return values[key].orEmpty()
        }

        @JvmStatic
        fun get(key: String, def: String): String {
            lastOverload = "get(key, def)"
            return values[key] ?: def
        }
    }
}
