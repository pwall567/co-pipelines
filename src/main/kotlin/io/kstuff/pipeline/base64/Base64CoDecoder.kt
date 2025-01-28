/*
 * @(#) Base64CoDecoder.kt
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

package io.kstuff.pipeline.base64

import io.jstuff.pipeline.base64.Base64Decoder.EQUALS_SIGN_MARKER
import io.jstuff.pipeline.base64.Base64Decoder.INVALID_MARKER
import io.jstuff.pipeline.base64.Base64Decoder.MAX_DECODED_VALUE
import io.jstuff.pipeline.base64.Base64Decoder.State
import io.jstuff.pipeline.base64.Base64Decoder.WHITESPACE_MARKER
import io.jstuff.pipeline.base64.Base64Decoder.decodingArray
import io.jstuff.pipeline.codec.ErrorStrategy

import io.kstuff.pipeline.codec.CoErrorStrategyBase
import io.kstuff.pipeline.IntCoAcceptor

/**
 * Base64 decoder - decode text encoded in Base 64.  Accepts either conventional encoding or URL-safe encoding, and
 * allows whitespace between groups of 4 characters.
 *
 * @author  Peter Wall
 */
class Base64CoDecoder<out R>(
    downstream: IntCoAcceptor<R>,
    errorStrategy: ErrorStrategy = ErrorStrategy.DEFAULT,
) : CoErrorStrategyBase<R>(downstream, errorStrategy) {

    private var state: State = State.FIRST
    private var saved: Int = 0

    override suspend fun acceptInt(value: Int) {
        if ((value and 0x7F.inv()) != 0) {
            handleError(value)
            return
        }
        val decoded = decodingArray[value].toInt()
        if (decoded == INVALID_MARKER) {
            handleError(value)
            return
        }
        when (state) {
            State.FIRST -> {
                if (decoded != WHITESPACE_MARKER) {
                    if (decoded == EQUALS_SIGN_MARKER) {
                        handleError(value)
                        return
                    }
                    saved = decoded
                    state = State.SECOND
                }
            }
            State.SECOND -> {
                if (decoded > MAX_DECODED_VALUE) {
                    handleError(value)
                    state = State.FIRST
                    return
                }
                emit(((saved shl 2) and 0xFC) or ((decoded shr 4) and 0x03))
                saved = decoded
                state = State.THIRD
            }
            State.THIRD -> {
                if (decoded > MAX_DECODED_VALUE) {
                    if (decoded == EQUALS_SIGN_MARKER && (saved and 0x0F) == 0)
                        state = State.EQUALS_EXPECTED
                    else {
                        handleError(value)
                        state = State.FIRST
                    }
                }
                else {
                    emit(((saved shl 4) and 0xF0) or ((decoded shr 2) and 0x0F))
                    saved = decoded
                    state = State.FOURTH
                }
            }
            State.FOURTH -> {
                if (decoded > MAX_DECODED_VALUE) {
                    if (decoded == EQUALS_SIGN_MARKER && (saved and 0x03) == 0)
                        state = State.COMPLETE
                    else {
                        handleError(value)
                        state = State.FIRST
                    }
                }
                else {
                    emit(((saved shl 6) and 0xC0) or decoded)
                    state = State.FIRST
                }
            }
            State.EQUALS_EXPECTED -> {
                if (decoded != EQUALS_SIGN_MARKER)
                    handleError(value)
                state = State.COMPLETE
            }
            State.COMPLETE -> {
                if (decoded != WHITESPACE_MARKER)
                    handleError(value)
            }
        }
    }

    override val stageComplete: Boolean
        get() = state == State.FIRST || state == State.COMPLETE || state == State.THIRD && (saved and 0x0F) == 0 ||
                state == State.FOURTH && (saved and 0x03) == 0

}
