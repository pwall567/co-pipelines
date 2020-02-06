/*
 * @(#) CoPipeline.kt
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

import java.nio.charset.Charset

interface CoBaseAcceptor<out R> : AutoCloseable {
    val complete: Boolean
    val closed: Boolean
    val result: R
}

interface CoAcceptor<in A, out R> : CoBaseAcceptor<R> {
    suspend fun accept(value: A?)
}

interface CoIntAcceptor<out R> : CoBaseAcceptor<R> {
    suspend fun accept(value: Int)
    suspend fun accept(string: String) {
        for (character in string)
            accept(character.toInt())
        close()
    }
}

abstract class CoBaseAbstractAcceptor<out R> : CoBaseAcceptor<R> {

    override val complete: Boolean = true

    private var _closed = false
    override val closed: Boolean
        get() = _closed

    override fun close() {
        check(complete) { "Sequence not complete" }
        _closed = true
    }

    override val result: R
        get() = throw IllegalStateException("Acceptor does not have a result")

}

interface CoPipeline<in A, in E, out R> : CoAcceptor<A, R> {
    suspend fun emit(value: E)
}

abstract class CoAbstractAcceptor<in A, out R> : CoBaseAbstractAcceptor<R>(), CoAcceptor<A, R> {

    override suspend fun accept(value: A?) {
        check(!closed) { "Acceptor is closed" }
        if (value == null)
            close()
        else
            acceptObject(value)
    }

    abstract suspend fun acceptObject(value: A)

}

abstract class CoAbstractIntAcceptor<out R> : CoBaseAbstractAcceptor<R>(), CoIntAcceptor<R> {

    override suspend fun accept(value: Int) {
        check(!closed) { "Acceptor is closed" }
        if (value == -1)
            close()
        else
            acceptInt(value)
    }

    abstract suspend fun acceptInt(value: Int)

}

interface CoIntObjectPipeline<in E, out R> : CoIntAcceptor<R> {
    suspend fun emit(value: E)
}

interface CoIntPipeline<out R> : CoIntAcceptor<R> {
    suspend fun emit(value: Int)
}

abstract class CoAbstractPipeline<in A, in E, out R>(val downstream: CoAcceptor<E, R>) : CoAbstractAcceptor<A, R>(),
        CoPipeline<A, E, R> {

    override fun close() {
        downstream.close()
        super<CoAbstractAcceptor>.close()
    }

    override suspend fun emit(value: E) {
        downstream.accept(value)
    }

    override val result: R
        get() = downstream.result

    override val complete: Boolean
        get() = downstream.complete

}

abstract class CoAbstractIntObjectPipeline<in E, out R>(val downstream: CoAcceptor<E, R>) : CoAbstractIntAcceptor<R>(),
        CoIntObjectPipeline<E, R> {

    override fun close() {
        downstream.close()
        super<CoAbstractIntAcceptor>.close()
    }

    override suspend fun emit(value: E) {
        downstream.accept(value)
    }

    override val result: R
        get() = downstream.result

    override val complete: Boolean
        get() = downstream.complete

}

abstract class CoAbstractIntPipeline<out R>(val downstream: CoIntAcceptor<R>) : CoAbstractIntAcceptor<R>(),
        CoIntPipeline<R> {

    override fun close() {
        downstream.close()
        super.close()
    }

    override suspend fun emit(value: Int) {
        downstream.accept(value)
    }

    override val result: R
        get() = downstream.result

    override val complete: Boolean
        get() = downstream.complete

}

class CoCodePoint_UTF8<R>(downstream: CoIntAcceptor<R>) : CoAbstractIntPipeline<R>(downstream) {

    override suspend fun acceptInt(value: Int) {
        when {
            value <= 0x7F -> emit(value)
            value <= 0x7FF -> {
                emit(0xC0 or (value shr 6))
                emit(0x80 or (value and 0x3F))
            }
            value <= 0xFFFF -> {
                emit(0xE0 or (value shr 12))
                emit(0x80 or ((value shr 6) and 0x3F))
                emit(0x80 or (value and 0x3F))
            }
            value <= 0x10FFFF -> {
                emit(0xF0 or (value shr 18))
                emit(0x80 or ((value shr 12) and 0x3F))
                emit(0x80 or ((value shr 6) and 0x3F))
                emit(0x80 or (value and 0x3F))
            }
            else -> throw IllegalArgumentException("Illegal code point")
        }
    }
}

class CoCodePoint_UTF16<R>(downstream: CoIntAcceptor<R>) : CoAbstractIntPipeline<R>(downstream) {

    override suspend fun acceptInt(value: Int) {
        if (Character.isBmpCodePoint(value))
            emit(value)
        else {
            emit(Character.highSurrogate(value).toInt())
            emit(Character.lowSurrogate(value).toInt())
        }
    }
}

class CoUTF8_CodePoint<R>(downstream: CoIntAcceptor<R>) : CoAbstractIntPipeline<R>(downstream) {

    private val threeByte1: suspend (Int) -> Unit = { i -> intermediate(i, terminal) }

    private val fourByte2: suspend (Int) -> Unit = { i -> intermediate(i, terminal) }

    private val fourByte1: suspend (Int) -> Unit = { i -> intermediate(i, fourByte2) }

    private val normal: suspend (Int) -> Unit = { i ->
        when {
            i == -1 || (i and 0x80) == 0 -> emit(i)
            (i and 0x40) == 0 -> throw IllegalArgumentException("Illegal character in UTF-8")
            (i and 0x20) == 0 -> startSequence(i and 0x1F, terminal)
            (i and 0x10) == 0 -> startSequence(i and 0x0F, threeByte1)
            (i and 0x08) == 0 -> startSequence(i and 0x07, fourByte1)
            else -> throw IllegalArgumentException("Illegal character in UTF-8")
        }
    }

    private val terminal: suspend (Int) -> Unit = { i ->
        checkTrailing(i)
        emit((codePoint shl 6) or (i and 0x3F))
        state = normal
    }

    private var state = normal
    private var codePoint = 0

    override suspend fun acceptInt(value: Int) {
        state(value)
    }

    private fun startSequence(i: Int, nextState: suspend (Int) -> Unit) {
        codePoint = i
        state = nextState
    }

    private fun intermediate(i: Int, nextState: suspend (Int) -> Unit) {
        checkTrailing(i)
        codePoint = (codePoint shl 6) or (i and 0x3F)
        state = nextState
    }

    private fun checkTrailing(i: Int) {
        if ((i and 0xC0) != 0x80)
            throw IllegalArgumentException("Illegal character in UTF-8")
    }

    override val complete: Boolean
        get() = state == normal && super.complete

}

class CoUTF16_CodePoint<R>(downstream: CoIntAcceptor<R>) : CoAbstractIntPipeline<R>(downstream) {

    private val normal: suspend (Int) -> Unit = { i ->
        if (Character.isHighSurrogate(i.toChar())) {
            highSurrogate = i
            state = terminal
        }
    }

    private val terminal: suspend (Int) -> Unit = { i ->
        require(Character.isLowSurrogate(i.toChar())) { "Illegal character in surrogate sequence" }
        emit(Character.toCodePoint(highSurrogate.toChar(), i.toChar()))
        state = normal
    }

    private var state = normal
    private var highSurrogate = 0

    override suspend fun acceptInt(value: Int) {
        state(value)
    }

    override val complete: Boolean
        get() = state == normal && super.complete

}

open class CoDecodingPipeline<R>(downstream: CoIntAcceptor<R>, val table: String) :
        CoAbstractIntPipeline<R>(downstream) {

    override suspend fun acceptInt(value: Int) {
        when (value) {
            in 0..0x7F -> emit(value)
            in 0x80..0xFF -> emit(table[value - 0x80].toInt())
            else -> throw IllegalArgumentException("Illegal character")
        }
    }

}

class CoWindows1252_CodePoint<R>(downstream: CoIntAcceptor<R>) : CoDecodingPipeline<R>(downstream, table) {

    companion object {
        const val table =
                "\u20AC\u0081\u201A\u0192\u201E\u2026\u2020\u2021\u02C6\u2030\u0160\u2039\u0152\u008D\u017D\u008F" +
                "\u0090\u2018\u2019\u201C\u201D\u2022\u2013\u2014\u02DC\u2122\u0161\u203A\u0153\u009D\u017E\u0178" +
                "\u00A0\u00A1\u00A2\u00A3\u00A4\u00A5\u00A6\u00A7\u00A8\u00A9\u00AA\u00AB\u00AC\u00AD\u00AE\u00AF" +
                "\u00B0\u00B1\u00B2\u00B3\u00B4\u00B5\u00B6\u00B7\u00B8\u00B9\u00BA\u00BB\u00BC\u00BD\u00BE\u00BF" +
                "\u00C0\u00C1\u00C2\u00C3\u00C4\u00C5\u00C6\u00C7\u00C8\u00C9\u00CA\u00CB\u00CC\u00CD\u00CE\u00CF" +
                "\u00D0\u00D1\u00D2\u00D3\u00D4\u00D5\u00D6\u00D7\u00D8\u00D9\u00DA\u00DB\u00DC\u00DD\u00DE\u00DF" +
                "\u00E0\u00E1\u00E2\u00E3\u00E4\u00E5\u00E6\u00E7\u00E8\u00E9\u00EA\u00EB\u00EC\u00ED\u00EE\u00EF" +
                "\u00F0\u00F1\u00F2\u00F3\u00F4\u00F5\u00F6\u00F7\u00F8\u00F9\u00FA\u00FB\u00FC\u00FD\u00FE\u00FF"
    }

}

object CoDecoderFactory {

    fun <R> getDecoder(charsetName: String, downstream: CoIntAcceptor<R>): CoAbstractIntPipeline<R> {
        return when (charsetName) {
            "windows-1252" -> CoWindows1252_CodePoint(downstream)
            else -> CoUTF8_CodePoint(downstream)
        }
    }

    fun <R> getDecoder(charset: Charset, downstream: CoIntAcceptor<R>) = getDecoder(charset.name(), downstream)

}
