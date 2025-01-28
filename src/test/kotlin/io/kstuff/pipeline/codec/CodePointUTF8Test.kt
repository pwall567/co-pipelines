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

package io.kstuff.pipeline.codec

import kotlin.test.Test
import kotlinx.coroutines.runBlocking

import io.kstuff.test.shouldBe
import io.kstuff.test.shouldThrow

import io.kstuff.pipeline.TestIntCoAcceptor
import io.kstuff.pipeline.accept
import io.jstuff.pipeline.codec.EncoderException
import io.jstuff.pipeline.codec.ErrorStrategy

class CodePointUTF8Test {

    @Test fun `should pass through ASCII`() = runBlocking {
        val pipe = CoCodePoint_UTF8(TestIntCoAcceptor())
        pipe.accept('A'.code)
        pipe.complete shouldBe true
        val result = pipe.result
        result.size shouldBe 1
        result[0] shouldBe 'A'.code
    }

    @Test fun `should pass through multiple ASCII`() = runBlocking {
        val pipe = CoCodePoint_UTF8(TestIntCoAcceptor())
        pipe.accept("ABC")
        pipe.complete shouldBe true
        val result = pipe.result
        result.size shouldBe 3
        result[0] shouldBe 'A'.code
        result[1] shouldBe 'B'.code
        result[2] shouldBe 'C'.code
    }

    @Test fun `should pass through two byte chars`() = runBlocking {
        val pipe = CoCodePoint_UTF8(TestIntCoAcceptor())
        pipe.accept(0xA9)
        pipe.accept(0xF7)
        pipe.complete shouldBe true
        val result = pipe.result
        result.size shouldBe 4
        result[0] shouldBe 0xC2
        result[1] shouldBe 0xA9
        result[2] shouldBe 0xC3
        result[3] shouldBe 0xB7
    }

    @Test fun `should pass through three byte chars`() = runBlocking {
        val pipe = CoCodePoint_UTF8(TestIntCoAcceptor())
        pipe.accept(0x2014)
        pipe.complete shouldBe true
        val result = pipe.result
        result.size shouldBe 3
        result[0] shouldBe 0xE2
        result[1] shouldBe 0x80
        result[2] shouldBe 0x94
    }

    @Test fun `should throw exception on invalid code point`() = runBlocking {
        val pipe = CoCodePoint_UTF8(TestIntCoAcceptor())
        shouldThrow<EncoderException>("Illegal value 0x110000") {
            pipe.accept(0x110000)
        }.let {
            it.errorValue shouldBe 0x110000
        }
    }

    @Test fun `should ignore invalid code point when selected`() = runBlocking {
        val pipe = CoCodePoint_UTF8(TestIntCoAcceptor(), ErrorStrategy.IGNORE)
        pipe.accept('A'.code)
        pipe.accept(0x110000)
        pipe.accept('B'.code)
        pipe.complete shouldBe true
        val result = pipe.result
        result.size shouldBe 2
        result[0] shouldBe 'A'.code
        result[1] shouldBe 'B'.code
    }

    @Test fun `should substitute for invalid code point when selected`() = runBlocking {
        val pipe = CoCodePoint_UTF8(TestIntCoAcceptor(), ErrorStrategy.Substitute())
        pipe.accept('A'.code)
        pipe.accept(0x110000)
        pipe.accept('B'.code)
        pipe.complete shouldBe true
        val result = pipe.result
        result.size shouldBe 3
        result[0] shouldBe 'A'.code
        result[1] shouldBe 0xBF
        result[2] shouldBe 'B'.code
    }

}
