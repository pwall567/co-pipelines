/*
 * @(#) CoDecoderBase.kt
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

package io.kstuff.pipeline.xxml

import io.jstuff.pipeline.xxml.MappingEntry

import io.kstuff.pipeline.AbstractIntCoPipeline
import io.kstuff.pipeline.IntCoAcceptor

/**
 * Base class for XML and HTML decoders.
 *
 * @author  Peter Wall
 */
open class CoDecoderBase<out R>(private val table: Array<MappingEntry>, downstream: IntCoAcceptor<R>) :
        AbstractIntCoPipeline<R>(downstream) {

    enum class State { NORMAL, AMPERSAND, CHARS, HASH, DIGITS, HEX }

    private val sb = StringBuilder()
    private var number: Int = 0
    private var state: State = State.NORMAL

    override suspend fun acceptInt(value: Int) {
        val ch = value.toChar()
        when (state) {
            State.NORMAL -> {
                if (ch == '&')
                    state = State.AMPERSAND
                else
                    emit(value)
            }
            State.AMPERSAND -> {
                when (ch) {
                    in 'A'..'Z', in 'a'..'z' -> {
                        sb.setLength(0)
                        sb.append(ch)
                        state = State.CHARS
                    }
                    '#' -> {
                        number = 0
                        state = State.HASH
                    }
                    else -> throw IllegalArgumentException("Illegal escape sequence")
                }
            }
            State.CHARS -> {
                when (ch) {
                    in 'A'..'Z', in 'a'..'z' -> {
                        if (sb.length < 12)
                            sb.append(ch)
                        else
                            throw IllegalArgumentException("Illegal escape sequence")
                    }
                    ';' -> {
                        state = State.NORMAL
                        val entity = sb.toString()
                        var lo = 0
                        var hi = table.size
                        while (lo < hi) {
                            val mid = (lo + hi) ushr 1
                            val entry = table[mid]
                            val entryString = entry.string
                            if (entity == entryString) {
                                emit(entry.codePoint)
                                return
                            }
                            if (entity < entryString)
                                hi = mid
                            else
                                lo = mid + 1
                        }
                        throw IllegalArgumentException("Illegal escape sequence")
                    }
                    else -> throw IllegalArgumentException("Illegal escape sequence")
                }
            }
            State.HASH -> {
                when (ch) {
                    in '0'..'9' -> {
                        number = value - '0'.code
                        state = State.DIGITS
                    }
                    'x' -> state = State.HEX
                    else -> throw IllegalArgumentException("Illegal escape sequence")
                }
            }
            State.DIGITS -> {
                when (ch) {
                    in '0'..'9' -> {
                        if (number < 9999999)
                            number = number * 10 + value - '0'.code
                        else
                            throw IllegalArgumentException("Illegal escape sequence")
                    }
                    ';' -> {
                        emit(number)
                        state = State.NORMAL
                    }
                    else -> throw IllegalArgumentException("Illegal escape sequence")
                }
            }
            State.HEX -> {
                when (ch) {
                    in '0'..'9' -> aggregateHex(value - '0'.code)
                    in 'A'..'F' -> aggregateHex(value - 'A'.code + 10)
                    in 'a'..'f' -> aggregateHex(value - 'a'.code + 10)
                    ';' -> {
                        emit(number)
                        state = State.NORMAL
                    }
                    else -> throw IllegalArgumentException("Illegal escape sequence")
                }
            }
        }
    }

    private fun aggregateHex(digit: Int) {
        if (number >= 0xFFFFFF)
            throw IllegalArgumentException("Illegal escape sequence")
        number = (number shl 4) or digit
    }

}
