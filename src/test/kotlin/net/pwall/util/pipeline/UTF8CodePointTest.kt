package net.pwall.util.pipeline

import kotlinx.coroutines.runBlocking

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.expect

class UTF8CodePointTest {

    @Test fun `should pass through single char`() {
        runBlocking {
            val pipe = CoUTF8_CodePoint(TestCoIntAcceptor())
            pipe.accept('A'.toInt())
            assertTrue(pipe.complete)
            val result = pipe.result
            expect(1) { result.size }
            expect('A'.toInt()) { result[0] }
        }
    }

    @Test fun `should pass through single char plus terminator`() {
        runBlocking {
            val pipe = CoUTF8_CodePoint(TestCoIntAcceptor())
            pipe.accept('A'.toInt())
            pipe.accept(-1)
            assertTrue(pipe.complete)
            assertTrue(pipe.closed)
            val result = pipe.result
            expect(1) { result.size }
            expect('A'.toInt()) { result[0] }
        }
    }

    @Test fun `should pass through two byte chars`() {
        runBlocking {
            val pipe = CoUTF8_CodePoint(TestCoIntAcceptor())
            pipe.accept(0xC2)
            assertFalse(pipe.complete)
            pipe.accept(0xA9)
            pipe.accept(0xC3)
            pipe.accept(0xB7)
            assertTrue(pipe.complete)
            val result = pipe.result
            expect(2) { result.size }
            expect(0xA9) { result[0] }
            expect(0xF7) { result[1] }
        }
    }

    @Test fun `should pass through three byte chars`() {
        runBlocking {
            val pipe = CoUTF8_CodePoint(TestCoIntAcceptor())
            pipe.accept(0xE2)
            pipe.accept(0x80)
            pipe.accept(0x94)
            assertTrue(pipe.complete)
            val result = pipe.result
            expect(1) { result.size }
            expect(0x2014) { result[0] }
        }
    }

}
