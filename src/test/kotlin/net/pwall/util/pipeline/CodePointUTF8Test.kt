package net.pwall.util.pipeline

import kotlinx.coroutines.runBlocking

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.expect

class CodePointUTF8Test {

    @Test fun `should pass through ASCII`() {
        runBlocking {
            val pipe = CoCodePoint_UTF8(TestCoIntAcceptor())
            pipe.accept('A'.toInt())
            assertTrue(pipe.complete)
            val result = pipe.result
            expect(1) { result.size }
            expect('A'.toInt()) { result[0] }
        }
    }

    @Test fun `should pass through multiple ASCII`() {
        runBlocking {
            val pipe = CoCodePoint_UTF8(TestCoIntAcceptor())
            pipe.accept("ABC")
            assertTrue(pipe.complete)
            val result = pipe.result
            expect(3) { result.size }
            expect('A'.toInt()) { result[0] }
            expect('B'.toInt()) { result[1] }
            expect('C'.toInt()) { result[2] }
        }
    }

    @Test fun `should pass through two byte chars`() {
        runBlocking {
            val pipe = CoCodePoint_UTF8(TestCoIntAcceptor())
            pipe.accept(0xA9)
            pipe.accept(0xF7)
            assertTrue(pipe.complete)
            val result = pipe.result
            expect(4) { result.size }
            expect(0xC2) { result[0] }
            expect(0xA9) { result[1] }
            expect(0xC3) { result[2] }
            expect(0xB7) { result[3] }
        }
    }

    @Test fun `should pass through three byte chars`() {
        runBlocking {
            val pipe = CoCodePoint_UTF8(TestCoIntAcceptor())
            pipe.accept(0x2014)
            assertTrue(pipe.complete)
            val result = pipe.result
            expect(3) { result.size }
            expect(0xE2) { result[0] }
            expect(0x80) { result[1] }
            expect(0x94) { result[2] }
        }
    }

}
