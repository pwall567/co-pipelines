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

package net.pwall.pipeline

/**
 * The base interface for pipeline and acceptor classes.
 */
interface BaseCoAcceptor<out R> {

    /** `true` if all sequences in the input are complete */
    val complete: Boolean
        get() = true

    /** `true` if the acceptor is closed */
    val closed: Boolean

    /** the result of the acceptor */
    val result: R
        get() = throw UnsupportedOperationException("No result defined")

    /**
     * Flush the acceptor (required by some implementations - default is a no-operation).
     */
    suspend fun flush() {}

    /**
     * Close the acceptor.
     */
    suspend fun close()

}

/**
 * An acceptor that takes a value of the specified type.
 */
interface CoAcceptor<in A, out R> : BaseCoAcceptor<R> {

    /**
     * Accept a value.
     */
    suspend fun accept(value: A?)

}

/**
 * An acceptor that takes an integer value.  Includes default functions to cater for the common cases of strings or byte
 * arrays being used for integer values.
 */
interface IntCoAcceptor<out R> : BaseCoAcceptor<R> {

    /**
     * Accept a value.
     */
    suspend fun accept(value: Int)

}

/**
 * Common functionality for [AbstractCoAcceptor] and [AbstractIntCoAcceptor].
 */
abstract class BaseAbstractCoAcceptor<out R> : BaseCoAcceptor<R> {

    /** `true` if the acceptor is closed */
    private var _closed = false
    override val closed: Boolean
        get() = _closed

    /**
     * Close the acceptor.  Throws an exception if the acceptor is not in the "complete" state.
     */
    override suspend fun close() {
        check(complete) { "Sequence not complete" }
        _closed = true
    }

}

/**
 * Abstract implementation of [CoAcceptor].
 */
abstract class AbstractCoAcceptor<in A, out R> : BaseAbstractCoAcceptor<R>(), CoAcceptor<A, R> {

    /**
     * Accept an object.  Check for pipeline already closed, and handle end of data.  This assumes that `null` is used
     * to indicate end of data; if that is not the case this method must be overridden or an alternative implementation
     * of [CoAcceptor] used.
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
     */
    abstract suspend fun acceptObject(value: A)

}

/**
 * Abstract implementation of [IntCoAcceptor].
 */
abstract class AbstractIntCoAcceptor<out R> : BaseAbstractCoAcceptor<R>(), IntCoAcceptor<R> {

    /**
     * Accept an `int`.  Check for acceptor already closed, and handle end of data.
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
     */
    abstract suspend fun acceptInt(value: Int)

}

/**
 * A [CoAcceptor] that accumulates its input tp a [List].
 */
class ListCoAcceptor<A>(initialCapacity: Int = defaultInitialCapacity) : AbstractCoAcceptor<A, List<A>>() {

    private val list = ArrayList<A>(initialCapacity)

    override suspend fun acceptObject(value: A) {
        list.add(value)
    }

    override val result: List<A>
        get() = list

    companion object {
        const val defaultInitialCapacity = 10
    }

}

/**
 * A simple implementation of [CoAcceptor] that takes a lambda as the accepting function.
 */
class SimpleCoAcceptor<in A>(val block: suspend (A) -> Unit) : AbstractCoAcceptor<A, Unit>() {

    /**
     * Accept a value, after `closed` check and test for end of data.  Invoke the lambda with the value.
     */
    override suspend fun acceptObject(value: A) {
        block(value)
    }

    override val result: Unit
        get() = throw UnsupportedOperationException()

}

/**
 * Create a [SimpleCoAcceptor].
 */
fun <A> simpleCoAcceptor(block: suspend (A) -> Unit) = SimpleCoAcceptor(block)
