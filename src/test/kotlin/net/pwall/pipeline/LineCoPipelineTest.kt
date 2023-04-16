/*
 * @(#) LineCoPipelineTest.kt
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

package net.pwall.pipeline

import kotlin.test.Test
import kotlin.test.expect
import kotlinx.coroutines.runBlocking

class LineCoPipelineTest {

    @Test fun `should split text on newlines`() = runBlocking {
        val input = "abc\ndef\nghi\n"
        val pipeline = LineCoPipeline(ListCoAcceptor()).apply { accept(input) }
        val expected = listOf("abc", "def", "ghi")
        expect(expected) { pipeline.result }
    }

    @Test fun `should split text on CRLF`() = runBlocking {
        val input = "abc\r\ndef\r\nghi\r\n"
        val pipeline = LineCoPipeline(ListCoAcceptor()).apply { accept(input) }
        val expected = listOf("abc", "def", "ghi")
        expect(expected) { pipeline.result }
    }

    @Test fun `should allow missing line terminator at end`() = runBlocking {
        val input = "abc\nde"
        val pipeline = LineCoPipeline(ListCoAcceptor())
        val result = pipeline.use { it.accept(input) }
        val expected = listOf("abc", "de")
        expect(expected) { result }
    }

}
