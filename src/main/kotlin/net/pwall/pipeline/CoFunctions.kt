/*
 * @(#) CoFunctions.kt
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

class CoCounter<in A>(private var count: Int = 0) : AbstractCoAcceptor<A, Int>() {

    override suspend fun acceptObject(value: A) {
        count++
    }

    override val result: Int
        get() = count

}

class CoFilter<in A, out R>(downstream: CoAcceptor<A, R>, private val predicate: (A) -> Boolean) :
        AbstractCoPipeline<A, A, R>(downstream) {

    override suspend fun acceptObject(value: A) {
        if (predicate(value))
            emit(value)
    }

}

class CoFold<A>(initialValue: A, private val function: (A, A) -> A) : AbstractCoAcceptor<A, A>() {

    override var result: A = initialValue

    override suspend fun acceptObject(value: A) {
        result = function(result, value)
    }

}

class ForkCoPipeline<A, out R>(downstream1: CoAcceptor<A, R>, private val downstream2: CoAcceptor<A, R>) :
        AbstractCoPipeline<A, A, R>(downstream1) {

    override suspend fun acceptObject(value: A) {
        emit(value)
        downstream2.accept(value)
    }

}

class IntCoCounter(private var count: Int = 0) : AbstractIntCoAcceptor<Int>() {

    override suspend fun acceptInt(value: Int) {
        count++
    }

    override val result: Int
        get() = count

}

class IntCoFilter<out R>(downstream: IntCoAcceptor<R>, private val predicate: (Int) -> Boolean) :
        AbstractIntCoPipeline<R>(downstream) {

    override suspend fun acceptInt(value: Int) {
        if (predicate(value))
            emit(value)
    }

}

class CoMapper<in A, E, out R>(downstream: CoAcceptor<E, R>, val function: (A) -> E) :
        AbstractCoPipeline<A, E, R>(downstream) {

    override suspend fun acceptObject(value: A) {
        emit(function(value))
    }

}
