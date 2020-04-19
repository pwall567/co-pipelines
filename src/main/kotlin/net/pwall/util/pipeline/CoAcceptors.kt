/*
 * @(#) CoAcceptors.kt
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

package net.pwall.util.pipeline

import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.io.ByteWriteChannel

/**
 * The base interface for pipeline and acceptor classes.
 *
 * @param   R       the result type
 */
interface BaseCoAcceptor<out R> : AutoCloseable {

    /** `true` if all sequences in the input are complete */
    val complete: Boolean

    /** `true` if the acceptor is closed */
    val closed: Boolean

    /** the result of the acceptor */
    val result: R

}

/**
 * An acceptor that takes a value of the specified type.
 *
 * @param   A       the accepted (input) type
 * @param   R       the result type
 */
interface CoAcceptor<in A, out R> : BaseCoAcceptor<R> {

    /**
     * Accept a value.
     *
     * @param   value   the value to be processed
     */
    suspend fun accept(value: A?)

}

/**
 * An acceptor that takes an integer value.  Includes default functions to cater for the common cases of strings or byte
 * arrays being used for integer values.
 *
 * @param   R       the result type
 */
interface IntCoAcceptor<out R> : BaseCoAcceptor<R> {

    /**
     * Accept a value.
     *
     * @param   value   the value to be processed
     */
    suspend fun accept(value: Int)

    /**
     * Accept a [CharSequence] (e.g. [String]) as a sequence of integer values.
     *
     * @param   charSequence    the [CharSequence]
     */
    suspend fun accept(charSequence: CharSequence) {
        for (character in charSequence)
            accept(character.toInt())
        close()
    }

    /**
     * Accept a [ByteArray] as a sequence of integer values.
     *
     * @param   bytes   the [ByteArray]
     * @param   offset  the starting offset into the array
     * @param   length  the length to accept
     */
    suspend fun accept(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size - offset) {
        for (i in offset until offset + length)
            accept(bytes[i].toInt())
        close()
    }

}

/**
 * Common functionality for [AbstractCoAcceptor] and [AbstractIntCoAcceptor].
 *
 * @param   R       the result type
 */
abstract class BaseAbstractCoAcceptor<out R> : BaseCoAcceptor<R> {

    /** Default `true` (all sequences in the input are complete) */
    override val complete: Boolean = true

    /** `true` if the acceptor is closed */
    private var _closed = false
    override val closed: Boolean
        get() = _closed

    /**
     * Close the acceptor.  Throws an exception if the acceptor is not in the "complete" state.
     */
    override fun close() {
        check(complete) { "Sequence not complete" }
        _closed = true
    }

    /**
     * Get the result object (default implementation throws exception).
     */
    override val result: R
        get() = throw IllegalStateException("Acceptor does not have a result")

}

/**
 * Abstract implementation of [CoAcceptor].
 *
 * @param   A       the accepted (input) type
 * @param   R       the result type
 */
abstract class AbstractCoAcceptor<in A, out R> : BaseAbstractCoAcceptor<R>(), CoAcceptor<A, R> {

    /**
     * Accept an object.  Check for pipeline already closed, and handle end of data.  This assumes that `null` is used
     * to indicate end of data; if that is not the case this method must be overridden or an alternative implementation
     * of [CoAcceptor] used.
     *
     * @param   value       the input value
     */
    override suspend fun accept(value: A?) {
        check(!closed) { "Acceptor is closed" }
        if (value == null)
            close()
        else
            acceptObject(value)
    }

    /**
     * Accept a value, after `closed` check and test for end of data.  Implementing classes must supply an
     * implementation of this method.
     *
     * @param   value       the input value
     */
    abstract suspend fun acceptObject(value: A)

}

/**
 * Abstract implementation of [IntCoAcceptor].
 *
 * @param   R       the result type
 */
abstract class AbstractIntCoAcceptor<out R> : BaseAbstractCoAcceptor<R>(), IntCoAcceptor<R> {

    /**
     * Accept an `int`.  Check for acceptor already closed, and handle end of data.
     *
     * @param   value       the input value
     */
    override suspend fun accept(value: Int) {
        check(!closed) { "Acceptor is closed" }
        if (value == -1)
            close()
        else
            acceptInt(value)
    }

    /**
     * Accept an `int`, after `closed` check and test for end of data.  Implementing classes must supply an
     * implementation of this method.
     *
     * @param   value       the input value
     */
    abstract suspend fun acceptInt(value: Int)

}

/**
 * A simple implementation of [CoAcceptor] that takes a lambda as the accepting function.
 */
class SimpleCoAcceptor<A>(val block: suspend (A) -> Unit) : AbstractCoAcceptor<A, Unit>() {

    /**
     * Accept a value, after `closed` check and test for end of data.  Invoke the lambda with the value.
     *
     * @param   value       the input value
     */
    override suspend fun acceptObject(value: A) {
        block(value)
    }

}

/**
 * Create a [SimpleCoAcceptor].
 *
 * @param   block   lambda to execute with each value
 * @param   A       the accepted (input) type
 */
fun <A> simpleCoAcceptor(block: suspend (A) -> Unit) = SimpleCoAcceptor(block)

/**
 * An implementation of [CoAcceptor] that sends the value to a [SendChannel].
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
    override fun close() {
        super.close()
        channel.close()
    }

}

/**
 * An implementation of [IntCoAcceptor] that sends the value to a [ByteWriteChannel].
 *
 * The [Int] values are expected to be in the range 0..255, i.e. byte values.  That makes this class suitable as the
 * downstream acceptor for an encoder pipeline.
 */
class ByteChannelCoAcceptor(private val channel: ByteWriteChannel) : AbstractIntCoAcceptor<Unit>() {

    /**
     * Accept a value, after `closed` check and test for end of data.  Send the value to the [ByteWriteChannel].
     *
     * @param   value       the input value
     */
    override suspend fun acceptInt(value: Int) {
        channel.writeByte(value.toByte())
    }

    /**
     * Close the acceptor.
     */
    override fun close() {
        super.close()
        channel.close(null)
    }

}
