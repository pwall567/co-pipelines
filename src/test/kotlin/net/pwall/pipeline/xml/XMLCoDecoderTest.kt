/*
 * @(#) XMLCoDecoderTest.kt
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

package net.pwall.pipeline.xml

import kotlin.test.Test
import kotlinx.coroutines.runBlocking

import io.kstuff.test.shouldBe

import net.pwall.pipeline.StringCoAcceptor
import net.pwall.pipeline.accept

class XMLCoDecoderTest {

    @Test fun `should decode plain string unmodified`() = runBlocking {
        XMLCoDecoder(StringCoAcceptor()).let {
            it.accept("plain")
            it.result shouldBe "plain"
        }
        XMLCoDecoder(StringCoAcceptor()).let {
            it.accept("aMuchLongerString")
            it.result shouldBe "aMuchLongerString"
        }
    }

    @Test fun `should decode special characters`() = runBlocking {
        XMLCoDecoder(StringCoAcceptor()).let {
            it.accept("&lt;div&gt;hello&lt;/div&gt;")
            it.result shouldBe "<div>hello</div>"
        }
        XMLCoDecoder(StringCoAcceptor()).let {
            it.accept("&lt;div class=&quot;test&quot;&gt;It&apos;s OK &amp;amp; working&lt;/div&gt;")
            it.result shouldBe "<div class=\"test\">It's OK &amp; working</div>"
        }
    }

    @Test fun `should decode nonstandard special characters`() = runBlocking {
        XMLCoDecoder(StringCoAcceptor()).let {
            it.accept("&lt;div&gt;&#xA1;hol&#xE1;!&lt;/div&gt;")
            it.result shouldBe "<div>\u00A1hol\u00E1!</div>"
        }
        XMLCoDecoder(StringCoAcceptor()).let {
            it.accept("&lt;div&gt;Even &#x2014; more&lt;/div&gt;")
            it.result shouldBe "<div>Even \u2014 more</div>"
        }
    }

}
