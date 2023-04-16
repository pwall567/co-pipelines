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

package net.pwall.pipeline

/**
 * Base interface for pipeline classes.
 *
 * @param   R       the result type
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
 *
 * @param   A       the accepted (input) type
 * @param   E       the emitted (downstream) type
 * @param   R       the result type
 */
interface CoPipeline<in A, in E, out R> : CoAcceptor<A, R>, BaseCoPipeline<R> {

    /**
     * Emit a value, that is, forward a value to the downstream acceptor.
     *
     * @param   value   the value
     */
    suspend fun emit(value: E)

}

/**
 * A pipeline that takes integer values and emits object values.
 *
 * @param   E       the emitted (downstream) type
 * @param   R       the result type
 */
interface IntObjectCoPipeline<in E, out R> : IntCoAcceptor<R>, BaseCoPipeline<R> {

    /**
     * Emit a value, that is, forward a value to the downstream acceptor.
     *
     * @param   value   the value
     */
    suspend fun emit(value: E)

}

/**
 * A pipeline that takes object values and emits integer values.
 *
 * @param   A       the accepted (input) type
 * @param   R       the result type
 */
interface ObjectIntCoPipeline<in A, out R> : CoAcceptor<A, R>, BaseCoPipeline<R> {

    /**
     * Emit a value, that is, forward a value to the downstream acceptor.
     *
     * @param   value   the value
     */
    suspend fun emit(value: Int)

}

/**
 * A pipeline that accepts and emits integer values.
 *
 * @param   R       the result type
 */
interface IntCoPipeline<out R> : IntCoAcceptor<R>, BaseCoPipeline<R> {

    /**
     * Emit a value, that is, forward a value to the downstream acceptor.
     *
     * @param   value   the value
     */
    suspend fun emit(value: Int)

}

/**
 * Abstract implementation of [CoPipeline].
 *
 * @param   A       the accepted (input) type
 * @param   E       the emitted (downstream) type
 * @param   R       the result type
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
     *
     * @param   value   the value to be forwarded
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
 *
 * @param   E       the emitted (downstream) type
 * @param   R       the result type
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
     *
     * @param   value   the value to be forwarded
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
 *
 * @param   A       the accepted (input) type
 * @param   R       the result type
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
     *
     * @param   value   the value to be forwarded
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
 *
 * @param   R       the result type
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
     *
     * @param   value   the value to be forwarded
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
