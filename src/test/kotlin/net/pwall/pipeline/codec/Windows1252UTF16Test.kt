/*
 * @(#) Windows1252UTF16Test.kt
 *
 * co-pipelines   Pipeline library for Kotlin coroutines
 * Copyright (c) 2021 Peter Wall
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
import kotlin.test.assertTrue
import kotlin.test.expect
import kotlinx.coroutines.runBlocking
import net.pwall.pipeline.TestIntCoAcceptor

class Windows1252UTF16Test {

    @Test fun `should pass through single char`() = runBlocking {
        val pipe = CoWindows1252_UTF16(TestIntCoAcceptor())
        pipe.accept('A'.code)
        assertTrue(pipe.complete)
        val result = pipe.result
        expect(1) { result.size }
        expect('A'.code) { result[0] }
    }

    @Test fun `should pass through single char plus terminator`() = runBlocking {
        val pipe = CoWindows1252_UTF16(TestIntCoAcceptor())
        pipe.accept('A'.code)
        pipe.accept(-1)
        assertTrue(pipe.complete)
        assertTrue(pipe.closed)
        val result = pipe.result
        expect(1) { result.size }
        expect('A'.code) { result[0] }
    }

    @Test fun `should pass through special chars`() = runBlocking {
        val pipe = CoWindows1252_UTF16(TestIntCoAcceptor())
        pipe.accept(0x80)
        pipe.accept(0x82)
        pipe.accept(0x83)
        pipe.accept(0x84)
        pipe.accept(0x85)
        pipe.accept(0x86)
        pipe.accept(0x87)
        pipe.accept(0x88)
        pipe.accept(0x89)
        pipe.accept(0x8A)
        pipe.accept(0x8B)
        pipe.accept(0x8C)
        pipe.accept(0x8E)
        pipe.accept(0x91)
        assertTrue(pipe.complete)
        val result = pipe.result
        expect(14) { result.size }
        expect(0x20AC) { result[0] }
        expect(0x201A) { result[1] }
        expect(0x0192) { result[2] }
        expect(0x201E) { result[3] }
        expect(0x2026) { result[4] }
        expect(0x2020) { result[5] }
        expect(0x2021) { result[6] }
        expect(0x02C6) { result[7] }
        expect(0x2030) { result[8] }
        expect(0x0160) { result[9] }
        expect(0x2039) { result[10] }
        expect(0x0152) { result[11] }
        expect(0x017D) { result[12] }
        expect(0x2018) { result[13] }
    }

}
