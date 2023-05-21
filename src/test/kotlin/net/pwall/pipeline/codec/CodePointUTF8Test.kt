/*
 * @(#) CodePointUTF8Test.kt
 *
 * co-pipelines   Pipeline library for Kotlin coroutines
 * Copyright (c) 2020 Peter Wall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.pwall.pipeline.codec

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.expect
import kotlinx.coroutines.runBlocking

import net.pwall.pipeline.TestIntCoAcceptor
import net.pwall.pipeline.accept

class CodePointUTF8Test {

    @Test fun `should pass through ASCII`() = runBlocking {
        val pipe = CoCodePoint_UTF8(TestIntCoAcceptor())
        pipe.accept('A'.code)
        assertTrue(pipe.complete)
        val result = pipe.result
        expect(1) { result.size }
        expect('A'.code) { result[0] }
    }

    @Test fun `should pass through multiple ASCII`() = runBlocking {
        val pipe = CoCodePoint_UTF8(TestIntCoAcceptor())
        pipe.accept("ABC")
        assertTrue(pipe.complete)
        val result = pipe.result
        expect(3) { result.size }
        expect('A'.code) { result[0] }
        expect('B'.code) { result[1] }
        expect('C'.code) { result[2] }
    }

    @Test fun `should pass through two byte chars`() = runBlocking {
        val pipe = CoCodePoint_UTF8(TestIntCoAcceptor())
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

    @Test fun `should pass through three byte chars`() = runBlocking {
        val pipe = CoCodePoint_UTF8(TestIntCoAcceptor())
        pipe.accept(0x2014)
        assertTrue(pipe.complete)
        val result = pipe.result
        expect(3) { result.size }
        expect(0xE2) { result[0] }
        expect(0x80) { result[1] }
        expect(0x94) { result[2] }
    }

    @Test fun `should throw exception on invalid code point`() = runBlocking {
        val pipe = CoCodePoint_UTF8(TestIntCoAcceptor())
        assertFailsWith<EncoderException> { pipe.accept(0x110000) }.let {
            expect("Illegal value 0x110000") { it.message }
            expect(0x110000) { it.errorValue }
        }
    }

    @Test fun `should ignore invalid code point when selected`() = runBlocking {
        val pipe = CoCodePoint_UTF8(TestIntCoAcceptor(), ErrorStrategy.IGNORE)
        pipe.accept('A'.code)
        pipe.accept(0x110000)
        pipe.accept('B'.code)
        assertTrue(pipe.complete)
        val result = pipe.result
        expect(2) { result.size }
        expect('A'.code) { result[0] }
        expect('B'.code) { result[1] }
    }

    @Test fun `should substitute for invalid code point when selected`() = runBlocking {
        val pipe = CoCodePoint_UTF8(TestIntCoAcceptor(), ErrorStrategy.Substitute())
        pipe.accept('A'.code)
        pipe.accept(0x110000)
        pipe.accept('B'.code)
        assertTrue(pipe.complete)
        val result = pipe.result
        expect(3) { result.size }
        expect('A'.code) { result[0] }
        expect(0xBF) { result[1] }
        expect('B'.code) { result[2] }
    }

}
