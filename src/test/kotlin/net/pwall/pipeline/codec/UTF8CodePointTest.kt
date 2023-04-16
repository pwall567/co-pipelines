/*
 * @(#) UTF8CodePointTest.kt
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.expect
import kotlinx.coroutines.runBlocking

import net.pwall.pipeline.TestIntCoAcceptor

class UTF8CodePointTest {

    @Test fun `should pass through single char`() = runBlocking {
        val pipe = CoUTF8_CodePoint(TestIntCoAcceptor())
        pipe.accept('A'.code)
        assertTrue(pipe.complete)
        val result = pipe.result
        expect(1) { result.size }
        expect('A'.code) { result[0] }
    }

    @Test fun `should pass through single char plus terminator`() = runBlocking {
        val pipe = CoUTF8_CodePoint(TestIntCoAcceptor())
        pipe.accept('A'.code)
        pipe.accept(-1)
        assertTrue(pipe.complete)
        assertTrue(pipe.closed)
        val result = pipe.result
        expect(1) { result.size }
        expect('A'.code) { result[0] }
    }

    @Test fun `should pass through two byte chars`() = runBlocking {
        val pipe = CoUTF8_CodePoint(TestIntCoAcceptor())
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

    @Test fun `should pass through three byte chars`() = runBlocking {
        val pipe = CoUTF8_CodePoint(TestIntCoAcceptor())
        pipe.accept(0xE2)
        pipe.accept(0x80)
        pipe.accept(0x94)
        assertTrue(pipe.complete)
        val result = pipe.result
        expect(1) { result.size }
        expect(0x2014) { result[0] }
    }

}
