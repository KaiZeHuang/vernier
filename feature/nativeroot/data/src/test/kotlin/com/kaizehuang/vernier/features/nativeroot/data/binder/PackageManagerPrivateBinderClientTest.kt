package com.kaizehuang.vernier.features.nativeroot.data.binder

import org.junit.Assert.assertEquals
import org.junit.Test

class PackageManagerPrivateBinderClientTest {

    @Test
    fun `success path resolves get and set through the hidden service`() {
        val client = PackageManagerPrivateBinderClient(
            transport = FakeTransport(ThroneHuntFakeService()),
            sdkProvider = { android.os.Build.VERSION_CODES.R },
            methodGateway = ReflectionMethodGateway(),
        )

        val get = client.getMimeGroup("com.example", "group")
        val set = client.setMimeGroup("com.example", "group", listOf("a"))

        assertEquals(get.detail, PackageManagerPrivateCallStatus.SUCCESS, get.status)
        assertEquals(listOf("a", "b"), get.value)
        assertEquals(set.detail, PackageManagerPrivateCallStatus.SUCCESS, set.status)
    }

    @Test
    fun `binder unavailable is explicit`() {
        val client = PackageManagerPrivateBinderClient(
            transport = FakeTransport(null),
            sdkProvider = { android.os.Build.VERSION_CODES.R },
            methodGateway = ReflectionMethodGateway(),
        )

        val result = client.getMimeGroup("com.example", "group")

        assertEquals(PackageManagerPrivateCallStatus.BINDER_UNAVAILABLE, result.status)
    }

    @Test
    fun `hidden api unavailable is explicit`() {
        val client = PackageManagerPrivateBinderClient(
            transport = FakeTransport(
                service = ThroneHuntFakeService(),
                hiddenApiUnavailable = true,
            ),
            sdkProvider = { android.os.Build.VERSION_CODES.R },
            methodGateway = ReflectionMethodGateway(),
        )

        val result = client.setMimeGroup("com.example", "group", listOf("a"))

        assertEquals(PackageManagerPrivateCallStatus.HIDDEN_API_UNAVAILABLE, result.status)
    }

    @Test
    fun `method missing is explicit`() {
        val client = PackageManagerPrivateBinderClient(
            transport = FakeTransport(object {}),
            sdkProvider = { android.os.Build.VERSION_CODES.R },
            methodGateway = ReflectionMethodGateway(),
        )

        val result = client.getMimeGroup("com.example", "group")

        assertEquals(result.detail, PackageManagerPrivateCallStatus.METHOD_UNAVAILABLE, result.status)
    }

    @Test
    fun `invocation failure is explicit`() {
        val client = PackageManagerPrivateBinderClient(
            transport = FakeTransport(ThroneHuntFakeService(throwOnCall = true)),
            sdkProvider = { android.os.Build.VERSION_CODES.R },
            methodGateway = ReflectionMethodGateway(),
        )

        val result = client.setMimeGroup("com.example", "group", listOf("a"))

        assertEquals(PackageManagerPrivateCallStatus.INVOCATION_FAILED, result.status)
        assertEquals("setMimeGroup failed: IllegalStateException: boom", result.detail)
    }

    private class FakeTransport(
        private val service: Any?,
        private val hiddenApiUnavailable: Boolean = false,
    ) : PackageManagerPrivateBinderClient.Transport {

        override fun resolveService(): PackageManagerPrivateBinderClient.Transport.ServiceResolution {
            return when {
                hiddenApiUnavailable -> PackageManagerPrivateBinderClient.Transport.ServiceResolution.HiddenApiUnavailable
                service == null -> PackageManagerPrivateBinderClient.Transport.ServiceResolution.BinderUnavailable
                else -> PackageManagerPrivateBinderClient.Transport.ServiceResolution.Service(service)
            }
        }
    }

    private class ThroneHuntFakeService(
        private val throwOnCall: Boolean = false,
    ) {
        fun getMimeGroup(packageName: String, group: String): List<String> {
            if (throwOnCall) error("boom")
            return listOf("a", "b")
        }

        fun setMimeGroup(packageName: String, group: String, mimeTypes: List<String>?) {
            if (throwOnCall) error("boom")
        }
    }

    private class ReflectionMethodGateway :
        PackageManagerPrivateBinderClient.MethodGateway {

        override fun invoke(
            targetClass: Class<*>,
            target: Any?,
            methodName: String,
            vararg arguments: Any?,
        ): Any? {
            val method = targetClass.methods.firstOrNull {
                it.name == methodName && it.parameterTypes.size == arguments.size
            } ?: throw NoSuchMethodException(methodName)
            return method.invoke(target, *arguments)
        }

        override fun declaredMethods(targetClass: Class<*>): List<java.lang.reflect.Executable> {
            return targetClass.methods.toList()
        }
    }
}
