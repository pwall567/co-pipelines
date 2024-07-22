/*
 * @(#) CoDecoders.kt
 *
 * co-pipelines   Pipeline library for Kotlin coroutines
 * Copyright (c) 2020, 2021, 2023 Peter Wall
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

@file:Suppress("ClassName")

package net.pwall.pipeline.codec

import java.nio.charset.Charset

import net.pwall.pipeline.AbstractIntCoPipeline
import net.pwall.pipeline.IntCoAcceptor
import net.pwall.pipeline.IntCoPipeline
import net.pwall.pipeline.codec.ErrorStrategy.Substitute
import net.pwall.pipeline.codec.ErrorStrategy.ThrowException

/**
 * Base class for encoder and decoder classes to implement the error strategy.
 */
abstract class CoErrorStrategyBase<out R>(
    downstream: IntCoAcceptor<R>,
    private val errorStrategy: ErrorStrategy,
) : AbstractIntCoPipeline<R>(downstream) {

    suspend fun handleError(value: Int) {
        when (errorStrategy) {
            is ThrowException -> throw EncoderException(value)
            is Substitute -> emit(errorStrategy.substitute)
        }
    }

}

/**
 * An encoder [IntCoPipeline] to convert Unicode code points to UTF-8.  Note that this encoder will convert surrogate
 * characters to 3-byte sequences without reporting an error, so it may be used as a UTF-16 to UTF-8 encoder.
 */
class CoCodePoint_UTF8<out R>(
    downstream: IntCoAcceptor<R>,
    errorStrategy: ErrorStrategy = ErrorStrategy.THROW_EXCEPTION,
) : CoErrorStrategyBase<R>(downstream, errorStrategy) {

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
            else -> handleError(value)
        }
    }

}

/**
 * An encoder [IntCoPipeline] to convert Unicode code points to UTF-16.
 */
class CoCodePoint_UTF16<out R>(
    downstream: IntCoAcceptor<R>,
    errorStrategy: ErrorStrategy = ErrorStrategy.THROW_EXCEPTION,
) : CoErrorStrategyBase<R>(downstream, errorStrategy) {

    override suspend fun acceptInt(value: Int) {
        when {
            value < 0xD800 -> emit(value)
            value < 0xE000 -> handleError(value)
            value < 0x10000 -> emit(value)
            value < 0x110000 -> {
                emit(Character.highSurrogate(value).code)
                emit(Character.lowSurrogate(value).code)
            }
            else -> handleError(value)
        }
    }

}

/**
 * A decoder [IntCoPipeline] to convert UTF-8 to Unicode code points.
 */
class CoUTF8_CodePoint<out R>(
    downstream: IntCoAcceptor<R>,
    errorStrategy: ErrorStrategy = ErrorStrategy.THROW_EXCEPTION,
) : CoErrorStrategyBase<R>(downstream, errorStrategy) {

    private val penultimate: suspend (Int) -> Unit = { i -> intermediate(i, terminal) }

    private val fourByte2: suspend (Int) -> Unit = { i -> intermediate(i, penultimate) }

    private val normal: suspend (Int) -> Unit = { i ->
        when {
            (i and 0x80) == 0 -> emit(i)
            (i and 0x40) == 0 -> handleError(i)
            (i and 0x20) == 0 -> startSequence(i and 0x1F, terminal)
            (i and 0x10) == 0 -> startSequence(i and 0x0F, penultimate)
            (i and 0x08) == 0 -> startSequence(i and 0x07, fourByte2)
            else -> handleError(i)
        }
    }

    private val terminal: suspend (Int) -> Unit = { i ->
        if ((i and 0xC0) == 0x80)
            emit((codePoint shl 6) or (i and 0x3F))
        else
            handleError(i)
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

    private suspend fun intermediate(i: Int, nextState: suspend (Int) -> Unit) {
        if ((i and 0xC0) == 0x80) {
            codePoint = (codePoint shl 6) or (i and 0x3F)
            state = nextState
        }
        else {
            handleError(i)
            state = normal
        }
    }

    override val complete: Boolean
        get() = state == normal && super.complete

}

/**
 * A decoder [IntCoPipeline] to convert UTF-8 to UTF-16.
 */
class CoUTF8_UTF16<out R>(
    downstream: IntCoAcceptor<R>,
    errorStrategy: ErrorStrategy = ErrorStrategy.THROW_EXCEPTION,
) : CoErrorStrategyBase<R>(downstream, errorStrategy) {

    private val penultimate: suspend (Int) -> Unit = { i -> intermediate(i, terminal) }

    private val fourByte2: suspend (Int) -> Unit = { i -> intermediate(i, penultimate) }

    private val normal: suspend (Int) -> Unit = { i ->
        when {
            (i and 0x80) == 0 -> emit(i)
            (i and 0x40) == 0 -> handleError(i)
            (i and 0x20) == 0 -> startSequence(i and 0x1F, terminal)
            (i and 0x10) == 0 -> startSequence(i and 0x0F, penultimate)
            (i and 0x08) == 0 -> startSequence(i and 0x07, fourByte2)
            else -> handleError(i)
        }
    }

    private val terminal: suspend (Int) -> Unit = { i ->
        if ((i and 0xC0) == 0x80) {
            val c = (codePoint shl 6) or (i and 0x3F)
            if (c < 0x10000)
                emit(c)
            else {
                emit(Character.highSurrogate(c).code)
                emit(Character.lowSurrogate(c).code)
            }
        }
        else
            handleError(i)
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

    private suspend fun intermediate(i: Int, nextState: suspend (Int) -> Unit) {
        if ((i and 0xC0) == 0x80) {
            codePoint = (codePoint shl 6) or (i and 0x3F)
            state = nextState
        }
        else {
            handleError(i)
            state = normal
        }
    }

    override val complete: Boolean
        get() = state == normal && super.complete

}

/**
 * A decoder [IntCoPipeline] to convert UTF-16 to Unicode code points.
 */
class CoUTF16_CodePoint<out R>(
    downstream: IntCoAcceptor<R>,
    errorStrategy: ErrorStrategy = ErrorStrategy.THROW_EXCEPTION,
) : CoErrorStrategyBase<R>(downstream, errorStrategy) {

    private val normal: suspend (Int) -> Unit = { i ->
        if (Character.isHighSurrogate(i.toChar())) {
            highSurrogate = i
            state = terminal
        }
        else
            emit(i)
    }

    private val terminal: suspend (Int) -> Unit = { i ->
        if (Character.isLowSurrogate(i.toChar()))
            emit(Character.toCodePoint(highSurrogate.toChar(), i.toChar()))
        else
            handleError(highSurrogate)
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

/**
 * A decoder [IntCoPipeline] to convert one-to-one mapping character encodings to Unicode code points.
 */
open class DecodingCoPipeline<out R>(
    downstream: IntCoAcceptor<R>,
    private val table: String,
    errorStrategy: ErrorStrategy = ErrorStrategy.THROW_EXCEPTION,
) : CoErrorStrategyBase<R>(downstream, errorStrategy) {

    override suspend fun acceptInt(value: Int) {
        when (value) {
            in 0..0x7F -> emit(value)
            in 0x80..0xFF -> emit(table[value - 0x80].code)
            else -> handleError(value)
        }
    }

}

/**
 * An encoder [IntCoPipeline] to convert Unicode code points to one-to-one mapping character encodings.
 */
open class EncodingCoPipeline<out R>(
    downstream: IntCoAcceptor<R>,
    private val reverseTable: IntArray,
    errorStrategy: ErrorStrategy = ErrorStrategy.THROW_EXCEPTION,
) : CoErrorStrategyBase<R>(downstream, errorStrategy) {

    override suspend fun acceptInt(value: Int) {
        if (value in 0..0x7F)
            emit(value)
        else {
            var lo = 0
            var hi = reverseTable.size
            while (lo < hi) {
                val mid = (hi + lo) ushr 1
                val item = reverseTable[mid] ushr 16
                when {
                    item < value -> lo = mid + 1
                    item > value -> hi = mid
                    else -> {
                        emit(reverseTable[mid] and 0xFFFF)
                        return
                    }
                }
            }
            handleError(value)
        }
    }

}

class CoWindows1252_UTF16<out R>(
    downstream: IntCoAcceptor<R>,
    errorStrategy: ErrorStrategy = ErrorStrategy.THROW_EXCEPTION,
) : DecodingCoPipeline<R>(downstream, table, errorStrategy) {

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
        val reverseTable: IntArray by lazy {
            table.toCharArray().mapIndexed { i, c -> (c.code shl 16) or (i + 0x80) }.toIntArray().also { it.sort() }
        }
    }

}

class CoUTF16_Windows1252<out R>(
    downstream: IntCoAcceptor<R>,
    errorStrategy: ErrorStrategy = ErrorStrategy.THROW_EXCEPTION,
) : EncodingCoPipeline<R>(downstream, CoWindows1252_UTF16.reverseTable, errorStrategy)

class CoISO8859_1_UTF16<out R>(
    downstream: IntCoAcceptor<R>,
    errorStrategy: ErrorStrategy = ErrorStrategy.THROW_EXCEPTION,
) : DecodingCoPipeline<R>(downstream, table, errorStrategy) {

    companion object {
        const val table =
                "\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087\u0088\u0089\u008A\u008B\u008C\u008D\u008E\u008F" +
                "\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097\u0098\u0099\u009A\u009B\u009C\u009D\u009E\u009F" +
                "\u00A0\u00A1\u00A2\u00A3\u00A4\u00A5\u00A6\u00A7\u00A8\u00A9\u00AA\u00AB\u00AC\u00AD\u00AE\u00AF" +
                "\u00B0\u00B1\u00B2\u00B3\u00B4\u00B5\u00B6\u00B7\u00B8\u00B9\u00BA\u00BB\u00BC\u00BD\u00BE\u00BF" +
                "\u00C0\u00C1\u00C2\u00C3\u00C4\u00C5\u00C6\u00C7\u00C8\u00C9\u00CA\u00CB\u00CC\u00CD\u00CE\u00CF" +
                "\u00D0\u00D1\u00D2\u00D3\u00D4\u00D5\u00D6\u00D7\u00D8\u00D9\u00DA\u00DB\u00DC\u00DD\u00DE\u00DF" +
                "\u00E0\u00E1\u00E2\u00E3\u00E4\u00E5\u00E6\u00E7\u00E8\u00E9\u00EA\u00EB\u00EC\u00ED\u00EE\u00EF" +
                "\u00F0\u00F1\u00F2\u00F3\u00F4\u00F5\u00F6\u00F7\u00F8\u00F9\u00FA\u00FB\u00FC\u00FD\u00FE\u00FF"
        val reverseTable: IntArray by lazy {
            table.toCharArray().mapIndexed { i, c -> (c.code shl 16) or (i + 0x80) }.toIntArray().also { it.sort() }
        }
    }

}

class CoUTF16_ISO8859_1<out R>(
    downstream: IntCoAcceptor<R>,
    errorStrategy: ErrorStrategy = ErrorStrategy.THROW_EXCEPTION,
) : EncodingCoPipeline<R>(downstream, CoISO8859_1_UTF16.reverseTable, errorStrategy)

class CoISO8859_15_UTF16<out R>(
    downstream: IntCoAcceptor<R>,
    errorStrategy: ErrorStrategy = ErrorStrategy.THROW_EXCEPTION,
) : DecodingCoPipeline<R>(downstream, table, errorStrategy) {

    companion object {
        const val table =
                "\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087\u0088\u0089\u008A\u008B\u008C\u008D\u008E\u008F" +
                "\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097\u0098\u0099\u009A\u009B\u009C\u009D\u009E\u009F" +
                "\u00A0\u00A1\u00A2\u00A3\u20AC\u00A5\u0160\u00A7\u0161\u00A9\u00AA\u00AB\u00AC\u00AD\u00AE\u00AF" +
                "\u00B0\u00B1\u00B2\u00B3\u017D\u00B5\u00B6\u00B7\u017E\u00B9\u00BA\u00BB\u0152\u0153\u0178\u00BF" +
                "\u00C0\u00C1\u00C2\u00C3\u00C4\u00C5\u00C6\u00C7\u00C8\u00C9\u00CA\u00CB\u00CC\u00CD\u00CE\u00CF" +
                "\u00D0\u00D1\u00D2\u00D3\u00D4\u00D5\u00D6\u00D7\u00D8\u00D9\u00DA\u00DB\u00DC\u00DD\u00DE\u00DF" +
                "\u00E0\u00E1\u00E2\u00E3\u00E4\u00E5\u00E6\u00E7\u00E8\u00E9\u00EA\u00EB\u00EC\u00ED\u00EE\u00EF" +
                "\u00F0\u00F1\u00F2\u00F3\u00F4\u00F5\u00F6\u00F7\u00F8\u00F9\u00FA\u00FB\u00FC\u00FD\u00FE\u00FF"
        val reverseTable: IntArray by lazy {
            table.toCharArray().mapIndexed { i, c -> (c.code shl 16) or (i + 0x80) }.toIntArray().also { it.sort() }
        }
    }

}

class CoUTF16_ISO8859_15<out R>(
    downstream: IntCoAcceptor<R>,
    errorStrategy: ErrorStrategy = ErrorStrategy.THROW_EXCEPTION,
) : EncodingCoPipeline<R>(downstream, CoISO8859_15_UTF16.reverseTable, errorStrategy)

class CoASCII_UTF16<out R>(
    downstream: IntCoAcceptor<R>,
    errorStrategy: ErrorStrategy = ErrorStrategy.THROW_EXCEPTION,
) : CoErrorStrategyBase<R>(downstream, errorStrategy) {

    override suspend fun acceptInt(value: Int) {
        if (value in 0..0x7F)
            emit(value)
        else
            handleError(value)
    }

}

class CoUTF16_ASCII<out R>(
    downstream: IntCoAcceptor<R>,
    errorStrategy: ErrorStrategy = ErrorStrategy.THROW_EXCEPTION,
) : CoErrorStrategyBase<R>(downstream, errorStrategy) {

    override suspend fun acceptInt(value: Int) {
        if (value in 0..0x7F)
            emit(value)
        else
            handleError(value)
    }

}

class SwitchableCoDecoder<R>(
    downstream: IntCoAcceptor<R>,
    errorStrategy: ErrorStrategy = ErrorStrategy.THROW_EXCEPTION,
) : CoErrorStrategyBase<R>(downstream, errorStrategy) {

    private var delegate: IntCoAcceptor<R> = CoASCII_UTF16(downstream)

    override suspend fun acceptInt(value: Int) {
        delegate.accept(value)
    }

    fun switchTo(delegate: IntCoAcceptor<R>) {
        this.delegate = delegate
    }

    fun switchTo(charsetName: String) {
        delegate = CoDecoderFactory.getDecoder(charsetName, downstream)
    }

    fun switchTo(charset: Charset) {
        delegate = CoDecoderFactory.getDecoder(charset, downstream)
    }

}

object CoDecoderFactory {

    /**
     * Get a decoder pipeline for the given character set name.
     */
    fun <R> getDecoder(
        charsetName: String,
        downstream: IntCoAcceptor<R>,
        errorStrategy: ErrorStrategy = ErrorStrategy.THROW_EXCEPTION,
    ): IntCoPipeline<R> = when {
        charsetName.equals("windows-1252", ignoreCase = true) -> CoWindows1252_UTF16(downstream, errorStrategy)
        charsetName.equals("ISO-8859-1", ignoreCase = true) -> CoISO8859_1_UTF16(downstream, errorStrategy)
        charsetName.equals("ISO-8859-15", ignoreCase = true) -> CoISO8859_15_UTF16(downstream, errorStrategy)
        charsetName.equals("US-ASCII" , ignoreCase = true) -> CoASCII_UTF16(downstream, errorStrategy)
        else -> CoUTF8_UTF16(downstream, errorStrategy)
    }

    /**
     * Get a decoder pipeline for the given character set.
     */
    fun <R> getDecoder(
        charset: Charset,
        downstream: IntCoAcceptor<R>,
        errorStrategy: ErrorStrategy = ErrorStrategy.THROW_EXCEPTION,
    ) = getDecoder(charset.name(), downstream, errorStrategy)

}

object CoEncoderFactory {

    /**
     * Get an encoder pipeline for the given character set name.
     */
    fun <R> getEncoder(
        charsetName: String,
        downstream: IntCoAcceptor<R>,
        errorStrategy: ErrorStrategy = ErrorStrategy.THROW_EXCEPTION,
    ): IntCoPipeline<R> = when {
        charsetName.equals("windows-1252", ignoreCase = true) -> CoUTF16_Windows1252(downstream, errorStrategy)
        charsetName.equals("ISO-8859-1", ignoreCase = true) -> CoUTF16_ISO8859_1(downstream, errorStrategy)
        charsetName.equals("ISO-8859-15", ignoreCase = true) -> CoUTF16_ISO8859_15(downstream, errorStrategy)
        charsetName.equals("US-ASCII", ignoreCase = true) -> CoUTF16_ASCII(downstream, errorStrategy)
        else -> CoCodePoint_UTF8(downstream, errorStrategy)
    }

    /**
     * Get an encoder pipeline for the given character set.
     */
    fun <R> getEncoder(
        charset: Charset,
        downstream: IntCoAcceptor<R>,
        errorStrategy: ErrorStrategy = ErrorStrategy.THROW_EXCEPTION,
    ) = getEncoder(charset.name(), downstream, errorStrategy)

}
