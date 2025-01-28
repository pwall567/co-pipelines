/*
 * @(#) LineCoPipeline.kt
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

package io.kstuff.pipeline

class LineCoPipeline<out R>(downstream: CoAcceptor<String, R>, maxLength: Int = 4096) :
        AbstractIntObjectCoPipeline<String, R>(downstream) {

    private val line = CharArray(maxLength)
    private var index = 0
    private var crSeen = false

    override suspend fun acceptInt(value: Int) {
        when (val char = value.toChar()) {
            '\r' -> {
                emitLine()
                crSeen = true
            }
            '\n' -> {
                if (!crSeen)
                    emitLine()
                crSeen = false
            }
            else -> {
                if (index == line.size) {
                    emit(String(line))
                    index = 0
                }
                line[index++] = char
                crSeen = false
            }
        }
    }

    private suspend fun emitLine() {
        emit(if (index == 0) "" else String(line, 0, index))
        index = 0
    }

    override suspend fun close() {
        if (index > 0)
            emitLine()
        super.close()
    }

}
