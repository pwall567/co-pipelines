/*
 * @(#) SchemaURICoEncoderTest.kt
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

package io.kstuff.pipeline.uri

import kotlin.test.Test
import kotlinx.coroutines.runBlocking

import io.kstuff.test.shouldBe

import io.kstuff.pipeline.StringCoAcceptor
import io.kstuff.pipeline.accept

class SchemaURICoEncoderTest {

    @Test fun `should encode schema fragment with dollar sign unmodified`() = runBlocking {
        SchemaURICoEncoder(StringCoAcceptor()).let {
            it.accept("\$ref")
            it.result shouldBe "\$ref"
        }
    }

    @Test fun `should encode reserved characters`() = runBlocking {
        SchemaURICoEncoder(StringCoAcceptor()).let {
            it.accept("Hello, World!")
            it.result shouldBe "Hello%2C%20World%21"
        }
        SchemaURICoEncoder(StringCoAcceptor()).let {
            it.accept("a more-complicated string: a/b+c%e.(???)")
            it.result shouldBe "a%20more-complicated%20string%3A%20a%2Fb%2Bc%25e.%28%3F%3F%3F%29"
        }
    }

}
