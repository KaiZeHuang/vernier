package com.kaizehuang.vernier.core.native

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pins the wire contract that `cpp/common/payload_codec.cpp` implements on the native side. The
 * expected encodings below are written out literally so that changing either side without the
 * other fails here rather than silently corrupting a report.
 */
class NativePayloadCodecTest {

    @Test
    fun `reads both spellings every encoder uses for a true flag`() {
        assertTrue(NativePayloadCodec.decodeFlag("1"))
        assertTrue(NativePayloadCodec.decodeFlag("true"))
        assertTrue(NativePayloadCodec.decodeFlag("TRUE"))
        assertTrue(NativePayloadCodec.decodeFlag("True"))
    }

    @Test
    fun `reads a false flag`() {
        assertFalse(NativePayloadCodec.decodeFlag("0"))
        assertFalse(NativePayloadCodec.decodeFlag("false"))
    }

    @Test
    fun `refuses to turn an unreadable flag into a positive detection`() {
        assertFalse(NativePayloadCodec.decodeFlag(null))
        assertFalse(NativePayloadCodec.decodeFlag(""))
        assertFalse(NativePayloadCodec.decodeFlag(" "))
        assertFalse(NativePayloadCodec.decodeFlag("yes"))
        assertFalse(NativePayloadCodec.decodeFlag("2"))
    }

    @Test
    fun `encodes the four structural characters`() {
        assertEquals("""a\\b""", NativePayloadCodec.encodeValue("""a\b"""))
        assertEquals("""a\nb""", NativePayloadCodec.encodeValue("a\nb"))
        assertEquals("""a\rb""", NativePayloadCodec.encodeValue("a\rb"))
        assertEquals("""a\tb""", NativePayloadCodec.encodeValue("a\tb"))
    }

    @Test
    fun `leaves values without structural characters untouched`() {
        val value = "/system/lib64/libvernier.so (deleted)"
        assertEquals(value, NativePayloadCodec.encodeValue(value))
        assertEquals(value, NativePayloadCodec.decodeValue(value))
    }

    @Test
    fun `encoded payload never contains a raw record separator`() {
        val encoded = NativePayloadCodec.encodeValue("group\tlabel\nnext=value")

        assertEquals(false, encoded.contains('\t'))
        assertEquals(false, encoded.contains('\n'))
    }

    @Test
    fun `round trips values that mix escapes and literals`() {
        val values = listOf(
            """C:\Windows\nope""",
            "line1\nline2\ttabbed",
            """trailing backslash\""",
            """\\\\""",
            """\n""",
            "",
            "\t\r\n\\",
        )

        values.forEach { value ->
            assertEquals(value, NativePayloadCodec.decodeValue(NativePayloadCodec.encodeValue(value)))
        }
    }

    @Test
    fun `decodes an escaped backslash without re-reading the next character`() {
        // The literal three characters backslash, backslash, n. A chained replace would turn this
        // into a newline; a single left-to-right pass must yield a backslash followed by 'n'.
        assertEquals("""\n""", NativePayloadCodec.decodeValue("""\\n"""))
    }

    @Test
    fun `passes unknown escapes through verbatim`() {
        // Forward compatibility: a newer native layer adding an escape must not make this parser
        // drop the character.
        assertEquals("""\q""", NativePayloadCodec.decodeValue("""\q"""))
        assertEquals("""\0""", NativePayloadCodec.decodeValue("""\0"""))
    }

    @Test
    fun `keeps a trailing lone backslash`() {
        assertEquals("""a\""", NativePayloadCodec.decodeValue("""a\"""))
    }
}
