/*
 * @(#) TestReadingInputStream.kt
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
import kotlin.test.fail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

import io.kstuff.test.shouldBe

import io.kstuff.pipeline.StringCoAcceptor

class TestReadingInputStream {

    @Test fun `should read and convert external resource`() = runBlocking {
        val pipe = withContext(Dispatchers.IO) {
            this::class.java.getResourceAsStream("/test1.txt")?.use {
                CoUTF8_CodePoint(CoCodePoint_UTF16(StringCoAcceptor(120))).apply {
                    while (!closed)
                        accept(it.read())
                }
            } ?: fail("Couldn't locate /test1.txt")
        }
        pipe.result shouldBe expected
    }

    companion object {
        @Suppress("ConstPropertyName")
        const val expected = "The quick brown fox jumps over the lazy dog.\n\n" +
                "And now for something completely different: \u2014 \u201C \u00C0 \u00C9 \u0130 \u00D4 \u00DC \u201D\n"
    }

}
