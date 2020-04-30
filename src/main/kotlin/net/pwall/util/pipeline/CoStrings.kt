/*
 * @(#) CoStrings.kt
 *
 * co-pipelines   Pipeline library for Kotlin coroutines
 * Copyright (c) 2020 Peter Wall
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
package net.pwall.util.pipeline

/**
 * An [IntCoAcceptor] that accumulates the received data as a stream of characters and makes the result available as a
 * [String].  This is particularly useful for testing functions that output character data.
 */
class StringCoAcceptor(private val initialSize: Int = 2000) : AbstractIntCoAcceptor<String>() {

    init {
        require(initialSize in 8..65536) { "Size out of range (8..65536) - $initialSize" }
    }

    private var charArray = CharArray(initialSize)
    private var index = 0

    val size: Int
        get() = index

    override suspend fun acceptInt(value: Int) {
        if (index == charArray.size) {
            val newArray = CharArray(index.let { if (it < 65536) it * 2 else it + 65536 })
            charArray.copyInto(newArray, 0, 0, index)
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

/**
 * Output a character.
 */
suspend fun IntCoAcceptor<*>.output(ch: Char) {
    accept(ch.toInt())
}

/**
 * Output a string.
 */
suspend fun IntCoAcceptor<*>.output(cs: CharSequence) {
    cs.forEach { accept(it.toInt()) }
}

/**
 * Output a [Byte] as two hexadecimal characters.
 */
suspend fun IntCoAcceptor<*>.outputHex(byte: Byte) {
    output(Strings.hexDigits[(byte.toInt() ushr 4) and 0xF])
    output(Strings.hexDigits[byte.toInt() and 0xF])
}

/**
 * Output a [Short] as four hexadecimal characters.
 */
suspend fun IntCoAcceptor<*>.outputHex(short: Short) {
    outputHex((short.toInt() ushr 8).toByte())
    outputHex(short.toByte())
}

/**
 * Output an [Int] as a decimal string.
 */
suspend fun IntCoAcceptor<*>.outputInt(i: Int) {
    if (i < 0) {
        if (i == Int.MIN_VALUE) {
            output("-2147483648")
            return
        }
        output('-')
        outputPositiveInt(-i)
    }
    else
        outputPositiveInt(i)
}

/**
 * Output a positive [Int] as a decimal string.
 */
suspend fun IntCoAcceptor<*>.outputPositiveInt(i: Int) {
    when {
        i >= 100 -> {
            val n = i / 100
            outputPositiveInt(n)
            val r = i - n * 100
            output(Strings.tensDigits[r])
            output(Strings.digits[r])
        }
        i >= 10 -> {
            output(Strings.tensDigits[i])
            output(Strings.digits[i])
        }
        else -> output(Strings.digits[i])
    }
}

/**
 * Output a [Long] as a decimal string.
 */
suspend fun IntCoAcceptor<*>.outputLong(i: Long) {
    if (i < 0) {
        if (i == Long.MIN_VALUE) {
            output("-9223372036854775808")
            return
        }
        output('-')
        outputPositiveLong(-i)
    }
    else
        outputPositiveLong(i)
}

/**
 * Output a positive [Long] as a decimal string.
 */
suspend fun IntCoAcceptor<*>.outputPositiveLong(i: Long) {
    when {
        i >= 100 -> {
            val n = i / 100
            outputPositiveLong(n)
            val r = i - n * 100
            output(Strings.tensDigits[r.toInt()])
            output(Strings.digits[r.toInt()])
        }
        i >= 10 -> {
            output(Strings.tensDigits[i.toInt()])
            output(Strings.digits[i.toInt()])
        }
        else -> output(Strings.digits[i.toInt()])
    }
}

object Strings {

    val hexDigits: CharArray =
            charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

    val digits: CharArray = charArrayOf(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    )

    val tensDigits: CharArray = charArrayOf(
            '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
            '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
            '2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
            '3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
            '4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
            '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
            '6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
            '7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
            '8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
            '9', '9', '9', '9', '9', '9', '9', '9', '9', '9'
    )

}
