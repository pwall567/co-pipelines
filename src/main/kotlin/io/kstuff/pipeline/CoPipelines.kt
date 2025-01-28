/*
 * @(#) CoPipelines.kt
 *
 * co-pipelines   Pipeline library for Kotlin coroutines
 * Copyright (c) 2020, 2021 Peter Wall
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

/**
 * Base interface for pipeline classes.
 */
interface BaseCoPipeline<out R> : BaseCoAcceptor<R> {

    val downstream: BaseCoAcceptor<R>

    val stageComplete: Boolean
        get() = true

    override val complete: Boolean
        get() = stageComplete && downstream.complete

    override val result: R
        get() {
            check(complete) { "Sequence is not complete" }
            return downstream.result
        }

}

/**
 * A pipeline that accepts and emits values of the specified types.
 */
interface CoPipeline<in A, in E, out R> : CoAcceptor<A, R>, BaseCoPipeline<R> {

    /**
     * Emit a value, that is, forward a value to the downstream acceptor.
     */
    suspend fun emit(value: E)

}

/**
 * A pipeline that takes integer values and emits object values.
 */
interface IntObjectCoPipeline<in E, out R> : IntCoAcceptor<R>, BaseCoPipeline<R> {

    /**
     * Emit a value, that is, forward a value to the downstream acceptor.
     */
    suspend fun emit(value: E)

}

/**
 * A pipeline that takes object values and emits integer values.
 */
interface ObjectIntCoPipeline<in A, out R> : CoAcceptor<A, R>, BaseCoPipeline<R> {

    /**
     * Emit a value, that is, forward a value to the downstream acceptor.
     */
    suspend fun emit(value: Int)

}

/**
 * A pipeline that accepts and emits integer values.
 */
interface IntCoPipeline<out R> : IntCoAcceptor<R>, BaseCoPipeline<R> {

    /**
     * Emit a value, that is, forward a value to the downstream acceptor.
     */
    suspend fun emit(value: Int)

}

/**
 * Abstract implementation of [CoPipeline].
 */
abstract class AbstractCoPipeline<in A, in E, out R>(override val downstream: CoAcceptor<E, R>) :
        AbstractCoAcceptor<A, R>(), CoPipeline<A, E, R> {

    /**
     * Close the pipeline.
     */
    override suspend fun close() {
        downstream.close()
        super.close()
    }

    /**
     * Emit a value to the downstream [CoAcceptor].
     */
    override suspend fun emit(value: E) {
        downstream.accept(value)
    }

    /**
     * Propagate the flush operation to the downstream acceptor.
     */
    override suspend fun flush() {
        downstream.flush()
    }

}

/**
 * Abstract implementation of [IntObjectCoPipeline].
 */
abstract class AbstractIntObjectCoPipeline<in E, out R>(override val downstream: CoAcceptor<E, R>) :
        AbstractIntCoAcceptor<R>(), IntObjectCoPipeline<E, R> {

    /**
     * Close the pipeline.
     */
    override suspend fun close() {
        downstream.close()
        super.close()
    }

    /**
     * Emit a value to the downstream [CoAcceptor].
     */
    override suspend fun emit(value: E) {
        downstream.accept(value)
    }

    /**
     * Propagate the flush operation to the downstream acceptor.
     */
    override suspend fun flush() {
        downstream.flush()
    }

}

/**
 * Abstract implementation of [ObjectIntCoPipeline].
 */
abstract class AbstractObjectIntCoPipeline<in A, out R>(override val downstream: IntCoAcceptor<R>) :
        AbstractCoAcceptor<A, R>(), ObjectIntCoPipeline<A, R> {

    /**
     * Close the pipeline.
     */
    override suspend fun close() {
        downstream.close()
        super.close()
    }

    /**
     * Emit a value to the downstream [IntCoAcceptor].
     */
    override suspend fun emit(value: Int) {
        downstream.accept(value)
    }

    /**
     * Propagate the flush operation to the downstream acceptor.
     */
    override suspend fun flush() {
        downstream.flush()
    }

}

/**
 * Abstract implementation of [IntCoPipeline].
 */
abstract class AbstractIntCoPipeline<out R>(override val downstream: IntCoAcceptor<R>) : AbstractIntCoAcceptor<R>(),
    IntCoPipeline<R> {

    /**
     * Close the pipeline.
     */
    override suspend fun close() {
        downstream.close()
        super.close()
    }

    /**
     * Emit a value to the downstream [IntCoAcceptor].
     */
    override suspend fun emit(value: Int) {
        downstream.accept(value)
    }

    /**
     * Propagate the flush operation to the downstream acceptor.
     */
    override suspend fun flush() {
        downstream.flush()
    }

}
