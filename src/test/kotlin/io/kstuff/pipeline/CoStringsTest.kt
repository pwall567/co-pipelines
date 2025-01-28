/*
 * @(#) CoStringsTest.kt
 *
 * co-pipelines   Pipeline library for Kotlin coroutines
 * Copyright (c) 2021 Peter Wall
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

import kotlin.test.Test
import kotlinx.coroutines.runBlocking

import io.kstuff.test.shouldBe

class CoStringsTest {

    @Test fun `should output characters to IntCoAcceptor`() = runBlocking {
        val stringCoAcceptor = StringCoAcceptor()
        stringCoAcceptor.output('H')
        stringCoAcceptor.output('e')
        stringCoAcceptor.output('l')
        stringCoAcceptor.output('l')
        stringCoAcceptor.output('o')
        stringCoAcceptor.size shouldBe 5
        stringCoAcceptor.result shouldBe "Hello"
    }

    @Test fun `should output strings to IntCoAcceptor`() = runBlocking {
        val stringCoAcceptor = StringCoAcceptor()
        stringCoAcceptor.output("Hello")
        stringCoAcceptor.output(' ')
        stringCoAcceptor.output("World!")
        stringCoAcceptor.result shouldBe "Hello World!"
    }

    @Test fun `should reset output`() = runBlocking {
        val stringCoAcceptor = StringCoAcceptor()
        stringCoAcceptor.output("Hello")
        stringCoAcceptor.reset()
        stringCoAcceptor.output(' ')
        stringCoAcceptor.output("World!")
        stringCoAcceptor.result shouldBe " World!"
    }

    @Test fun `should output hex bytes to IntCoAcceptor`() = runBlocking {
        val stringCoAcceptor = StringCoAcceptor()
        stringCoAcceptor.outputHex(0x01.toByte())
        stringCoAcceptor.outputHex(0x8F.toByte())
        stringCoAcceptor.result shouldBe "018F"
    }

    @Test fun `should output hex short to IntCoAcceptor`() = runBlocking {
        val stringCoAcceptor = StringCoAcceptor()
        stringCoAcceptor.outputHex(0x018F.toShort())
        stringCoAcceptor.outputHex(0x9ABC.toShort())
        stringCoAcceptor.result shouldBe "018F9ABC"
    }

    @Test fun `should output int to IntCoAcceptor`() = runBlocking {
        val stringCoAcceptor = StringCoAcceptor()
        stringCoAcceptor.outputInt(123456)
        stringCoAcceptor.result shouldBe "123456"
        stringCoAcceptor.reset()
        stringCoAcceptor.outputInt(0)
        stringCoAcceptor.result shouldBe "0"
        stringCoAcceptor.reset()
        stringCoAcceptor.outputInt(-1)
        stringCoAcceptor.result shouldBe "-1"
        stringCoAcceptor.reset()
        stringCoAcceptor.outputInt(58974228)
        stringCoAcceptor.result shouldBe "58974228"
        stringCoAcceptor.reset()
        stringCoAcceptor.outputPositiveInt(58974228)
        stringCoAcceptor.result shouldBe "58974228"
        stringCoAcceptor.reset()
        stringCoAcceptor.outputInt(-7762835)
        stringCoAcceptor.result shouldBe "-7762835"
    }

    @Test fun `should output Long to IntCoAcceptor`() = runBlocking {
        val stringCoAcceptor = StringCoAcceptor(8)
        stringCoAcceptor.outputLong(123456789123456789)
        stringCoAcceptor.result shouldBe "123456789123456789"
        stringCoAcceptor.reset()
        stringCoAcceptor.outputLong(0)
        stringCoAcceptor.result shouldBe "0"
        stringCoAcceptor.reset()
        stringCoAcceptor.outputLong(-1)
        stringCoAcceptor.result shouldBe "-1"
        stringCoAcceptor.reset()
        stringCoAcceptor.outputLong(58974228)
        stringCoAcceptor.result shouldBe "58974228"
        stringCoAcceptor.reset()
        stringCoAcceptor.outputPositiveLong(58974228)
        stringCoAcceptor.result shouldBe "58974228"
        stringCoAcceptor.reset()
        stringCoAcceptor.outputLong(-776283544328776)
        stringCoAcceptor.result shouldBe "-776283544328776"
    }

    @Test fun `should output 2 digits to IntCoAcceptor`() = runBlocking {
        val stringCoAcceptor = StringCoAcceptor(8)
        stringCoAcceptor.output2Digits(0)
        stringCoAcceptor.result shouldBe "00"
        stringCoAcceptor.reset()
        stringCoAcceptor.output2Digits(3)
        stringCoAcceptor.result shouldBe "03"
        stringCoAcceptor.reset()
        stringCoAcceptor.output2Digits(27)
        stringCoAcceptor.result shouldBe "27"
        stringCoAcceptor.reset()
        stringCoAcceptor.output2Digits(99)
        stringCoAcceptor.result shouldBe "99"
    }

    @Test fun `should output 3 digits to IntCoAcceptor`() = runBlocking {
        val stringCoAcceptor = StringCoAcceptor(8)
        stringCoAcceptor.output3Digits(0)
        stringCoAcceptor.result shouldBe "000"
        stringCoAcceptor.reset()
        stringCoAcceptor.output3Digits(3)
        stringCoAcceptor.result shouldBe "003"
        stringCoAcceptor.reset()
        stringCoAcceptor.output3Digits(27)
        stringCoAcceptor.result shouldBe "027"
        stringCoAcceptor.reset()
        stringCoAcceptor.output3Digits(123)
        stringCoAcceptor.result shouldBe "123"
    }

}
