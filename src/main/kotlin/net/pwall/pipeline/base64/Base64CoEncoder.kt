/*
 * @(#) Base64CoEncoder.kt
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

import net.pwall.pipeline.IntCoAcceptor
import net.pwall.pipeline.base64.Base64Encoder.State
import net.pwall.pipeline.base64.Base64Encoder.encodingArrayMain
import net.pwall.pipeline.base64.Base64Encoder.encodingArrayURL
import net.pwall.pipeline.codec.CoErrorStrategyBase
import net.pwall.pipeline.codec.ErrorStrategy

class Base64CoEncoder<out R>(
    downstream: IntCoAcceptor<R>,
    private val urlSafe: Boolean = false,
    errorStrategy: ErrorStrategy = ErrorStrategy.DEFAULT,
) : CoErrorStrategyBase<R>(downstream, errorStrategy) {

    private var state: State = State.FIRST
    private val encodingArray: ByteArray = if (urlSafe) encodingArrayURL else encodingArrayMain
    private var saved: Int = 0

    override suspend fun acceptInt(value: Int) {
        if (value > 0xFF) {
            handleError(value)
            return
        }
        when (state) {
            State.FIRST -> {
                emit(encodingArray[(value shr 2) and 0x3F].toInt())
                saved = value
                state = State.SECOND
            }
            State.SECOND -> {
                emit(encodingArray[((saved shl 4) and 0x30) or ((value shr 4) and 0x0F)].toInt())
                saved = value
                state = State.THIRD
            }
            State.THIRD -> {
                emit(encodingArray[((saved shl 2) and 0x3C) or ((value shr 6) and 0x03)].toInt())
                emit(encodingArray[value and 0x3F].toInt())
                state = State.FIRST
            }
        }
    }

    override suspend fun close() {
        when (state) {
            State.FIRST -> {}
            State.SECOND -> {
                emit(encodingArray[(saved shl 4) and 0x30].toInt())
                if (!urlSafe) {
                    emit('='.code)
                    emit('='.code)
                }
            }
            State.THIRD -> {
                emit(encodingArray[(saved shl 2) and 0x3C].toInt())
                if (!urlSafe)
                    emit('='.code)
            }
        }
    }

}
