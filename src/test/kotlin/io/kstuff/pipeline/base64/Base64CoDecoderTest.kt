/*
 * @(#) Base64CoDecoderTest.kt
 *
 * co-pipelines   Pipeline library for Kotlin coroutines
 * Copyright (c) 2023 Peter Wall
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

package io.kstuff.pipeline.base64

import kotlin.test.Test
import kotlinx.coroutines.runBlocking

import io.kstuff.test.shouldBe
import io.kstuff.test.shouldThrow

import io.jstuff.pipeline.codec.EncoderException

import io.kstuff.pipeline.ByteArrayCoAcceptor
import io.kstuff.pipeline.StringCoAcceptor
import io.kstuff.pipeline.accept

class Base64CoDecoderTest {

    @Test fun `should decode simple string`() = runBlocking {
        val pipeline = Base64CoDecoder(StringCoAcceptor())
        pipeline.accept("QUJDRA==")
        pipeline.result shouldBe "ABCD"
    }

    @Test fun `should decode special characters`() = runBlocking {
        val pipeline = Base64CoDecoder(ByteArrayCoAcceptor())
        pipeline.accept("+/+/")
        val result = pipeline.result
        result.size shouldBe 3
        result[0] shouldBe 0xFB.toByte()
        result[1] shouldBe 0xFF.toByte()
        result[2] shouldBe 0xBF.toByte()
    }

    @Test fun `should decode URL special characters`() = runBlocking {
        val pipeline = Base64CoDecoder(ByteArrayCoAcceptor())
        pipeline.accept("-_-_")
        val result = pipeline.result
        result.size shouldBe 3
        result[0] shouldBe 0xFB.toByte()
        result[1] shouldBe 0xFF.toByte()
        result[2] shouldBe 0xBF.toByte()
    }

    @Test fun `should throw exception on invalid character`() = runBlocking {
        val pipeline = Base64CoDecoder(ByteArrayCoAcceptor())
        shouldThrow<EncoderException>("Illegal value 0x2A") {
            pipeline.accept('*'.code)
        }.let {
            it.errorValue shouldBe 0x2A
        }
    }

}
