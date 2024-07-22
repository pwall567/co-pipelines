/*
 * @(#) CoEncoderBase.kt
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

package net.pwall.pipeline.codec

import net.pwall.pipeline.AbstractIntCoPipeline
import net.pwall.pipeline.IntCoAcceptor
import net.pwall.util.CoIntOutput

/**
 * Base class for encoders.
 *
 * @author  Peter Wall
 */
abstract class CoEncoderBase<out R>(downstream: IntCoAcceptor<R>) : AbstractIntCoPipeline<R>(downstream) {

    suspend fun emit(ch: Char) {
        emit(ch.code)
    }

    suspend fun emit(string: String) {
        for (ch in string)
            emit(ch)
    }

    suspend fun emitHex(i: Int) {
        CoIntOutput.coOutputIntHex(i) { emit(it) }
    }

}
