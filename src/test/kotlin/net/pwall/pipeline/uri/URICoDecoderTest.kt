/*
 * @(#) URICoDecoderTest.kt
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

package net.pwall.pipeline.uri

import kotlin.test.Test
import kotlin.test.expect
import kotlinx.coroutines.runBlocking

import net.pwall.pipeline.StringCoAcceptor
import net.pwall.pipeline.accept

class URICoDecoderTest {

    @Test fun `should decode plain string unmodified`() = runBlocking {
        URICODecoder(StringCoAcceptor()).let {
            it.accept("plain")
            expect("plain") { it.result }
        }
        URICODecoder(StringCoAcceptor()).let {
            it.accept("aMuchLongerString")
            expect("aMuchLongerString") { it.result }
        }
    }

    @Test fun `should decode percent sequence`() = runBlocking {
        URICODecoder(StringCoAcceptor()).let {
            it.accept("Hello%2C%20World%21")
            expect("Hello, World!") { it.result }
        }
        URICODecoder(StringCoAcceptor()).let {
            it.accept("a%20more-complicated%20string%3A%20a%2Fb%2Bc%25e.%28%3F%3F%3F%29")
            expect("a more-complicated string: a/b+c%e.(???)") { it.result }
        }
    }

    @Test fun `should decode plus as space`() = runBlocking {
        URICODecoder(StringCoAcceptor()).let {
            it.accept("Hello%2C+World%21")
            expect("Hello, World!") { it.result }
        }
        URICODecoder(StringCoAcceptor()).let {
            it.accept("a+more-complicated+string%3A+a%2Fb%2Bc%25e.%28%3F%3F%3F%29")
            expect("a more-complicated string: a/b+c%e.(???)") { it.result }
        }
    }

}
