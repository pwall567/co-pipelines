/*
 * @(#) HTMLCoDecoderTest.kt
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

package io.kstuff.pipeline.html

import kotlin.test.Test
import kotlinx.coroutines.runBlocking

import io.kstuff.test.shouldBe

import io.kstuff.pipeline.StringCoAcceptor
import io.kstuff.pipeline.accept

class HTMLCoDecoderTest {

    @Test fun `should decode plain string unmodified`() = runBlocking {
        HTMLCoDecoder(StringCoAcceptor()).let {
            it.accept("plain")
            it.result shouldBe "plain"
        }
        HTMLCoDecoder(StringCoAcceptor()).let {
            it.accept("aMuchLongerString")
            it.result shouldBe "aMuchLongerString"
        }
    }

    @Test fun `should decode special characters`() = runBlocking {
        HTMLCoDecoder(StringCoAcceptor()).let {
            it.accept("&lt;div&gt;hello&lt;/div&gt;")
            it.result shouldBe "<div>hello</div>"
        }
        HTMLCoDecoder(StringCoAcceptor()).let {
            it.accept("&lt;div class=&quot;test&quot;&gt;It's OK &amp;amp; working&lt;/div&gt;")
            it.result shouldBe "<div class=\"test\">It's OK &amp; working</div>"
        }
    }

    @Test fun `should decode named special characters`() = runBlocking {
        HTMLCoDecoder(StringCoAcceptor()).let {
            it.accept("&lt;div&gt;&iexcl;hol&aacute;!&lt;/div&gt;")
            it.result shouldBe "<div>\u00A1hol\u00E1!</div>"
        }
        HTMLCoDecoder(StringCoAcceptor()).let {
            it.accept("&lt;div&gt;Even &mdash; more&lt;/div&gt;")
            it.result shouldBe "<div>Even \u2014 more</div>"
        }
    }

    @Test fun `should decode nonstandard special characters`() = runBlocking {
        HTMLCoDecoder(StringCoAcceptor()).let {
            it.accept("&lt;div&gt;M&#x101;ori&#x7;&lt;/div&gt;")
            it.result shouldBe "<div>M\u0101ori\u0007</div>"
        }
        HTMLCoDecoder(StringCoAcceptor()).let {
            it.accept("&#xFEFF;BOM &#x2E19; &#x20A4;")
            it.result shouldBe "\uFEFFBOM \u2E19 \u20A4"
        }
    }

}
