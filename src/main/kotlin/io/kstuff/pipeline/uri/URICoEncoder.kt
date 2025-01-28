/*
 * @(#) URICoEncoder.kt
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

package io.kstuff.pipeline.uri

import io.jstuff.pipeline.uri.URIEncoder.isUnreservedURI
import io.kstuff.util.CoIntOutput.coOutput2Hex

import io.kstuff.pipeline.AbstractIntCoPipeline
import io.kstuff.pipeline.IntCoAcceptor

/**
 * URI encoder - encode text using URI percent-encoding.
 *
 * @author  Peter Wall
 */
class URICoEncoder<out R>(
    downstream: IntCoAcceptor<R>,
    private val encodeSpaceAsPlus: Boolean = false,
) : AbstractIntCoPipeline<R>(downstream) {

    override suspend fun acceptInt(value: Int) {
        when {
            value == ' '.code && encodeSpaceAsPlus -> emit('+'.code)
            !isUnreservedURI(value) -> {
                emit('%'.code)
                coOutput2Hex(value) { emit(it.code) }
            }
            else -> emit(value)
        }
    }

}
