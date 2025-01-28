/*
 * @(#) CoFunctions.kt
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

package io.kstuff.pipeline

import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.util.stream.Stream

/**
 * Accept an [Iterator] as sequence of objects.
 */
suspend fun <A, R> CoAcceptor<A, R>.accept(iterator: Iterator<A>) {
    while (iterator.hasNext())
        accept(iterator.next())
}

/**
 * Accept an [Iterable] (_e.g._ [List]) as sequence of objects.
 */
suspend fun <A, R> CoAcceptor<A, R>.accept(iterable: Iterable<A>) {
    accept(iterable.iterator())
}

/**
 * Accept a Java [Stream] as a sequence of objects.
 */
suspend fun <A, R> CoAcceptor<A, R>.accept(stream: Stream<A>) {
    accept(stream.iterator())
}

/**
 * Accept a [CharSequence] (_e.g._ [String]) as a sequence of integer values.
 */
suspend fun <R> IntCoAcceptor<R>.accept(charSequence: CharSequence) {
    for (character in charSequence)
        accept(character.code)
}

/**
 * Accept a [CharArray] as a sequence of integer values.
 */
suspend fun <R> IntCoAcceptor<R>.accept(chars: CharArray, offset: Int = 0, length: Int = chars.size - offset) {
    for (i in offset until offset + length)
        accept(chars[i].code)
}

/**
 * Accept a [ByteArray] as a sequence of integer values.
 */
suspend fun <R> IntCoAcceptor<R>.accept(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size - offset) {
    for (i in offset until offset + length)
        accept(bytes[i].toInt() and 0xFF)
}

/**
 * Accept a [CharBuffer] as a sequence of integer values.
 */
suspend fun <R> IntCoAcceptor<R>.accept(charBuffer: CharBuffer) {
    while (charBuffer.hasRemaining())
        accept(charBuffer.get().code)
}

/**
 * Accept a [ByteArray] as a sequence of integer values.
 */
suspend fun <R> IntCoAcceptor<R>.accept(byteBuffer: ByteBuffer) {
    while (byteBuffer.hasRemaining())
        accept(byteBuffer.get().toInt() and 0xFF)
}
