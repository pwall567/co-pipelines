/*
 * @(#) CoURI.kt
 *
 * co-pipelines   Pipeline library for Kotlin coroutines
 * Copyright (c) 2021, 2023 Peter Wall
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

package net.pwall.pipeline.uri

import net.pwall.pipeline.AbstractIntCoPipeline
import net.pwall.pipeline.IntCoAcceptor
import net.pwall.util.CoIntOutput.coOutput2Hex

class URIDecoder(downstream: IntCoAcceptor<String>) : AbstractIntCoPipeline<String>(downstream) {

    enum class State { NORMAL, FIRST, SECOND }

    private var state = State.NORMAL
    private var char: Int = 0

    override suspend fun acceptInt(value: Int) {
        when (state) {
            State.NORMAL -> {
                if (value == '%'.code) {
                    char = 0
                    state = State.FIRST
                } else
                    emit(value)
            }
            State.FIRST -> {
                char = fromHex(value.toChar()) shl 4
                state = State.SECOND
            }
            State.SECOND -> {
                emit(char or fromHex(value.toChar()))
                state = State.NORMAL
            }
        }

    }

    override val stageComplete: Boolean
        get() = state == State.NORMAL

    private fun fromHex(ch: Char): Int {
        return when (ch) {
            in '0'..'9' -> ch.code - '0'.code
            in 'A'..'Z' -> ch.code - 'A'.code + 10
            in 'a'..'z' -> ch.code - 'a'.code + 10
            else -> throw IllegalArgumentException("Illegal hex character - $ch")
        }
    }

}

class URIEncoder(downstream: IntCoAcceptor<String>) : AbstractIntCoPipeline<String>(downstream) {

    override suspend fun acceptInt(value: Int) {
        if (!isUnreservedURI(value.toChar())) { // TODO add options to encode space as plus and to allow dollar sign
            emit('%'.code)
            coOutput2Hex(value) { emit(it.code) }
        }
        else
            emit(value)
    }

    companion object {

        private fun isUnreservedURI(ch: Char) =
            ch in 'A'..'Z' || ch in 'a'..'z' || ch in '0'..'9' || ch == '-' || ch == '.' || ch == '_' || ch == '~'

    }

}
