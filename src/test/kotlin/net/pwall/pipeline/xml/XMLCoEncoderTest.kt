/*
 * @(#) XMLCoEncoderTest.kt
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
import kotlin.test.expect
import kotlinx.coroutines.runBlocking

import net.pwall.pipeline.StringCoAcceptor
import net.pwall.pipeline.accept

class XMLCoEncoderTest {

    @Test fun `should encode plain string unmodified`() = runBlocking {
        XMLCoEncoder(StringCoAcceptor()).let {
            it.accept("plain")
            expect("plain") { it.result}
        }
        XMLCoEncoder(StringCoAcceptor()).let {
            it.accept("aMuchLongerString")
            expect("aMuchLongerString") { it.result}
        }
    }

    @Test fun `should encode special characters`() = runBlocking {
        XMLCoEncoder(StringCoAcceptor()).let {
            it.accept("<div>hello</div>")
            expect("&lt;div&gt;hello&lt;/div&gt;") { it.result}
        }
        XMLCoEncoder(StringCoAcceptor()).let {
            it.accept("<div class=\"test\">It's OK &amp; working</div>")
            expect("&lt;div class=&quot;test&quot;&gt;It&apos;s OK &amp;amp; working&lt;/div&gt;") { it.result}
        }
    }

    @Test fun `should encode nonstandard special characters`() = runBlocking {
        XMLCoEncoder(StringCoAcceptor()).let {
            it.accept("<div>\u00A1hol\u00E1!</div>")
            expect("&lt;div&gt;&#xA1;hol&#xE1;!&lt;/div&gt;") { it.result}
        }
        XMLCoEncoder(StringCoAcceptor()).let {
            it.accept("<div>Even \u2014 more</div>")
            expect("&lt;div&gt;Even &#x2014; more&lt;/div&gt;") { it.result}
        }
    }

}
