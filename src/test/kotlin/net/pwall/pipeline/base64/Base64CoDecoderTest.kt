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

package net.pwall.pipeline.base64

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.expect
import kotlinx.coroutines.runBlocking

import net.pwall.pipeline.ByteArrayCoAcceptor
import net.pwall.pipeline.StringCoAcceptor
import net.pwall.pipeline.accept
import net.pwall.pipeline.codec.EncoderException

class Base64CoDecoderTest {

    @Test fun `should decode simple string`() = runBlocking {
        val pipeline = Base64CoDecoder(StringCoAcceptor())
        pipeline.accept("QUJDRA==")
        expect("ABCD") { pipeline.result }
    }

    @Test fun `should decode special characters`() = runBlocking {
        val pipeline = Base64CoDecoder(ByteArrayCoAcceptor())
        pipeline.accept("+/+/")
        val result = pipeline.result
        expect(3) { result.size }
        expect(0xFB.toByte()) { result[0] }
        expect(0xFF.toByte()) { result[1] }
        expect(0xBF.toByte()) { result[2] }
    }

    @Test fun `should decode URL special characters`() = runBlocking {
        val pipeline = Base64CoDecoder(ByteArrayCoAcceptor())
        pipeline.accept("-_-_")
        val result = pipeline.result
        expect(3) { result.size }
        expect(0xFB.toByte()) { result[0] }
        expect(0xFF.toByte()) { result[1] }
        expect(0xBF.toByte()) { result[2] }
    }

    @Test fun `should throw exception on invalid character`() = runBlocking {
        val pipeline = Base64CoDecoder(ByteArrayCoAcceptor())
        assertFailsWith<EncoderException> { pipeline.accept('*'.code) }.let {
            expect("Illegal value 0x2A") { it.message }
            expect(0x2A) { it.errorValue }
        }
    }

}
