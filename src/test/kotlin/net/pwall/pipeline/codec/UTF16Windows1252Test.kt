/*
 * @(#) UTF16Windows1252Test.kt
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
import kotlinx.coroutines.runBlocking

import io.kstuff.test.shouldBe

import net.pwall.pipeline.TestIntCoAcceptor

class UTF16Windows1252Test {

    @Test fun `should pass through single char`() = runBlocking {
        val pipe = CoUTF16_Windows1252(TestIntCoAcceptor())
        pipe.accept('A'.code)
        pipe.complete shouldBe true
        val result = pipe.result
        result.size shouldBe 1
        result[0] shouldBe 'A'.code
    }

    @Test fun `should pass through single char plus terminator`() = runBlocking {
        val pipe = CoUTF16_Windows1252(TestIntCoAcceptor())
        pipe.accept('A'.code)
        pipe.accept(-1)
        pipe.complete shouldBe true
        pipe.closed shouldBe true
        val result = pipe.result
        result.size shouldBe 1
        result[0] shouldBe 'A'.code
    }

    @Test fun `should pass through special chars`() = runBlocking {
        val pipe = CoUTF16_Windows1252(TestIntCoAcceptor())
        pipe.accept(0x20AC)
        pipe.accept(0x201A)
        pipe.accept(0x0192)
        pipe.accept(0x201E)
        pipe.accept(0x2026)
        pipe.accept(0x2020)
        pipe.accept(0x2021)
        pipe.accept(0x02C6)
        pipe.accept(0x2030)
        pipe.accept(0x0160)
        pipe.accept(0x2039)
        pipe.accept(0x0152)
        pipe.accept(0x017D)
        pipe.accept(0x2018)
        pipe.complete shouldBe true
        val result = pipe.result
        result.size shouldBe 14
        result[0] shouldBe 0x80
        result[1] shouldBe 0x82
        result[2] shouldBe 0x83
        result[3] shouldBe 0x84
        result[4] shouldBe 0x85
        result[5] shouldBe 0x86
        result[7] shouldBe 0x88
        result[8] shouldBe 0x89
        result[9] shouldBe 0x8A
        result[11] shouldBe 0x8C
        result[12] shouldBe 0x8E
        result[13] shouldBe 0x91
    }

}
