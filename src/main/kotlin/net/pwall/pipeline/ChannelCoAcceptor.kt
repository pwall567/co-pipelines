/*
 * @(#) ChannelCoAcceptor.kt
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

import kotlinx.coroutines.channels.SendChannel

/**
 * An implementation of [CoAcceptor] that sends the value to a [SendChannel].
 *
 * @author  Peter Wall
 */
class ChannelCoAcceptor<A>(private val channel: SendChannel<A>) : AbstractCoAcceptor<A, Unit>() {

    /**
     * Accept a value, after `closed` check and test for end of data.  Send the value to the [SendChannel].
     *
     * @param   value       the input value
     */
    override suspend fun acceptObject(value: A) {
        channel.send(value)
    }

    /**
     * Close the acceptor.
     */
    override suspend fun close() {
        super.close()
        channel.close()
    }

    override val result: Unit
        get() = throw UnsupportedOperationException()

}
