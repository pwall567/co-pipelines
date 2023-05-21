/*
 * @(#) CoStrings.kt
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

import net.pwall.util.CoIntOutput.coOutput2Digits
import net.pwall.util.CoIntOutput.coOutput2Hex
import net.pwall.util.CoIntOutput.coOutput3Digits
import net.pwall.util.CoIntOutput.coOutput4Hex
import net.pwall.util.CoIntOutput.coOutputInt
import net.pwall.util.CoIntOutput.coOutputLong
import net.pwall.util.CoIntOutput.coOutputPositiveInt
import net.pwall.util.CoIntOutput.coOutputPositiveLong

/**
 * An [IntCoAcceptor] that accumulates the received data as a stream of characters and makes the result available as a
 * [String].  This is particularly useful for testing functions that output character data.
 */
class StringCoAcceptor(private val initialSize: Int = 2048) : AbstractIntCoAcceptor<String>() {

    init {
        require(initialSize in 8..65536) { "Size out of range (8..65536) - $initialSize" }
    }

    private var charArray = CharArray(initialSize)
    private var index = 0

    val size: Int
        get() = index

    override suspend fun acceptInt(value: Int) {
        if (index == charArray.size) {
            val newArray = CharArray(index.let { if (it < 65536) it * 2 else it + 65536 }) {
                if (it < index) charArray[it] else '\u0000'
            }
            charArray = newArray
        }
        charArray[index++] = value.toChar()
    }

    override val result: String
        get() = String(charArray, 0, index)

    fun reset() {
        charArray = CharArray(initialSize)
        index = 0
    }

}

class ByteArrayCoAcceptor(private val initialSize: Int = 20) : AbstractIntCoAcceptor<ByteArray>() {

    init {
        require(initialSize in 8..65536) { "Size out of range (8..65536) - $initialSize" }
    }

    private var byteArray = ByteArray(initialSize)
    private var index = 0

    val size: Int
        get() = index

    override suspend fun acceptInt(value: Int) {
        if (index == byteArray.size) {
            val newArray = ByteArray(index.let { if (it < 65536) it * 2 else it + 65536 }) {
                if (it < index) byteArray[it] else 0
            }
            byteArray = newArray
        }
        byteArray[index++] = value.toByte()
    }

    override val result: ByteArray
        get() = ByteArray(index) { byteArray[it] }

    fun reset() {
        byteArray = ByteArray(initialSize)
        index = 0
    }

}

/**
 * Output a character.
 */
suspend fun IntCoAcceptor<*>.output(ch: Char) {
    accept(ch.code)
}

/**
 * Output a string.
 */
suspend fun IntCoAcceptor<*>.output(cs: CharSequence) {
    cs.forEach { accept(it.code) }
}

/**
 * Output a [Byte] as two hexadecimal characters.
 */
suspend fun IntCoAcceptor<*>.outputHex(byte: Byte) {
    coOutput2Hex(byte.toInt()) { output(it) }
}

/**
 * Output a [Short] as four hexadecimal characters.
 */
suspend fun IntCoAcceptor<*>.outputHex(short: Short) {
    coOutput4Hex(short.toInt()) { output(it) }
}

/**
 * Output an [Int] as a decimal string.
 */
suspend fun IntCoAcceptor<*>.outputInt(i: Int) {
    coOutputInt(i) { output(it) }
}

/**
 * Output a positive [Int] as a decimal string.
 */
suspend fun IntCoAcceptor<*>.outputPositiveInt(i: Int) {
    coOutputPositiveInt(i) { output(it) }
}

/**
 * Output a [Long] as a decimal string.
 */
suspend fun IntCoAcceptor<*>.outputLong(i: Long) {
    coOutputLong(i) { output(it) }
}

/**
 * Output a positive [Long] as a decimal string.
 */
suspend fun IntCoAcceptor<*>.outputPositiveLong(i: Long) {
    coOutputPositiveLong(i) { output(it) }
}

/**
 * Output an [Int] as two decimal digits.
 */
suspend fun IntCoAcceptor<*>.output2Digits(i: Int) {
    coOutput2Digits(i) { output(it) }
}

/**
 * Output an [Int] as three decimal digits.
 */
suspend fun IntCoAcceptor<*>.output3Digits(i: Int) {
    coOutput3Digits(i) { output(it) }
}
