/*
 * @(#) Base64CoEncoderTest.kt
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

package net.pwall.pipeline.base64

import kotlin.test.Test
import kotlinx.coroutines.runBlocking

import io.kstuff.test.shouldBe

import net.pwall.pipeline.StringCoAcceptor
import net.pwall.pipeline.accept

class Base64CoEncoderTest {

    @Test fun `should encode simple string`() = runBlocking {
        val pipeline = Base64CoEncoder(StringCoAcceptor())
        pipeline.accept("ABCD")
        pipeline.close()
        pipeline.result shouldBe "QUJDRA=="
    }

    @Test fun `should encode high characters`() = runBlocking {
        val pipeline = Base64CoEncoder(StringCoAcceptor())
        pipeline.accept(0xFB)
        pipeline.accept(0xFF)
        pipeline.accept(0xBF)
        pipeline.close()
        pipeline.result shouldBe "+/+/"
    }

    @Test fun `should encode simple string using URL encoding`() = runBlocking {
        val pipeline = Base64CoEncoder(StringCoAcceptor(), urlSafe = true)
        pipeline.accept("ABCD")
        pipeline.close()
        pipeline.result shouldBe "QUJDRA"
    }

    @Test fun `should encode high characters using URL encoding`() = runBlocking {
        val pipeline = Base64CoEncoder(StringCoAcceptor(), urlSafe = true)
        pipeline.accept(0xFB)
        pipeline.accept(0xFF)
        pipeline.accept(0xBF)
        pipeline.close()
        pipeline.result shouldBe "-_-_"
    }

}
