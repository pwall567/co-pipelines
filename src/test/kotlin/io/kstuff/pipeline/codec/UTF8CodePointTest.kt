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

package io.kstuff.pipeline.codec

import kotlin.test.Test
import kotlinx.coroutines.runBlocking

import io.kstuff.test.shouldBe

import io.kstuff.pipeline.TestIntCoAcceptor

class UTF8CodePointTest {

    @Test fun `should pass through single char`() = runBlocking {
        val pipe = CoUTF8_CodePoint(TestIntCoAcceptor())
        pipe.accept('A'.code)
        pipe.complete shouldBe true
        val result = pipe.result
        result.size shouldBe 1
        result[0] shouldBe 'A'.code
    }

    @Test fun `should pass through single char plus terminator`() = runBlocking {
        val pipe = CoUTF8_CodePoint(TestIntCoAcceptor())
        pipe.accept('A'.code)
        pipe.accept(-1)
        pipe.complete shouldBe true
        pipe.closed shouldBe true
        val result = pipe.result
        result.size shouldBe 1
        result[0] shouldBe 'A'.code
    }

    @Test fun `should pass through two byte chars`() = runBlocking {
        val pipe = CoUTF8_CodePoint(TestIntCoAcceptor())
        pipe.accept(0xC2)
        pipe.complete shouldBe false
        pipe.accept(0xA9)
        pipe.accept(0xC3)
        pipe.accept(0xB7)
        pipe.complete shouldBe true
        val result = pipe.result
        result.size shouldBe 2
        result[0] shouldBe 0xA9
        result[1] shouldBe 0xF7
    }

    @Test fun `should pass through three byte chars`() = runBlocking {
        val pipe = CoUTF8_CodePoint(TestIntCoAcceptor())
        pipe.accept(0xE2)
        pipe.accept(0x80)
        pipe.accept(0x94)
        pipe.complete shouldBe true
        val result = pipe.result
        result.size shouldBe 1
        result[0] shouldBe 0x2014
    }

}
