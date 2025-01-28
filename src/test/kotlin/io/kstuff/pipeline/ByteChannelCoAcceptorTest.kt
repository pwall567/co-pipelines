/*
 * @(#) ByteChannelCoAcceptorTest.kt
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

package io.kstuff.pipeline

import kotlin.test.Test
import kotlinx.coroutines.io.ByteChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import io.kstuff.test.shouldBe

class ByteChannelCoAcceptorTest {

    @Test fun `should send data to a ByteChannel`() = runBlocking {
        val channel = ByteChannel(true)
        val job = launch {
            channel.readByte() shouldBe 'H'.code.toByte()
            channel.readByte() shouldBe 'e'.code.toByte()
            channel.readByte() shouldBe 'l'.code.toByte()
            channel.readByte() shouldBe 'l'.code.toByte()
            channel.readByte() shouldBe 'o'.code.toByte()
        }
        val byteChannelCoAcceptor = ByteChannelCoAcceptor(channel)
        byteChannelCoAcceptor.accept("Hello")
        job.join()
    }

}
