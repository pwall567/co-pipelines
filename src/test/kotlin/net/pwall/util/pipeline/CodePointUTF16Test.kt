/*
 * @(#) CodePointUTF16Test.kt
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

package net.pwall.util.pipeline

import kotlinx.coroutines.runBlocking

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.expect

class CodePointUTF16Test {

    @Test fun `should pass through BMP code point`() {
        runBlocking {
            val pipe = CoCodePoint_UTF16(TestIntCoAcceptor())
            pipe.accept('A'.toInt())
            assertTrue(pipe.complete)
            val result = pipe.result
            expect(1) { result.size }
            expect('A'.toInt()) { result[0] }
        }
    }

    @Test fun `should convert surrogate pair`() {
        runBlocking {
            val pipe = CoCodePoint_UTF16(TestIntCoAcceptor())
            pipe.accept(0x1F602)
            assertTrue(pipe.complete)
            val result = pipe.result
            expect(2) { result.size }
            expect(0xD83D) { result[0] }
            expect(0xDE02) { result[1] }
        }
    }

}
