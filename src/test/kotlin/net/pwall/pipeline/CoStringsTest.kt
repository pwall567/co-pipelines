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

package net.pwall.pipeline

import kotlin.test.Test
import kotlin.test.expect
import kotlinx.coroutines.runBlocking

class CoStringsTest {

    @Test fun `should output characters to IntCoAcceptor`() = runBlocking {
        val stringCoAcceptor = StringCoAcceptor()
        stringCoAcceptor.output('H')
        stringCoAcceptor.output('e')
        stringCoAcceptor.output('l')
        stringCoAcceptor.output('l')
        stringCoAcceptor.output('o')
        expect(5) { stringCoAcceptor.size }
        expect("Hello") { stringCoAcceptor.result }
    }

    @Test fun `should output strings to IntCoAcceptor`() = runBlocking {
        val stringCoAcceptor = StringCoAcceptor()
        stringCoAcceptor.output("Hello")
        stringCoAcceptor.output(' ')
        stringCoAcceptor.output("World!")
        expect("Hello World!") { stringCoAcceptor.result }
    }

    @Test fun `should reset output`() = runBlocking {
        val stringCoAcceptor = StringCoAcceptor()
        stringCoAcceptor.output("Hello")
        stringCoAcceptor.reset()
        stringCoAcceptor.output(' ')
        stringCoAcceptor.output("World!")
        expect(" World!") { stringCoAcceptor.result }
    }

    @Test fun `should output hex bytes to IntCoAcceptor`() = runBlocking {
        val stringCoAcceptor = StringCoAcceptor()
        stringCoAcceptor.outputHex(0x01.toByte())
        stringCoAcceptor.outputHex(0x8F.toByte())
        expect("018F") { stringCoAcceptor.result }
    }

    @Test fun `should output hex short to IntCoAcceptor`() = runBlocking {
        val stringCoAcceptor = StringCoAcceptor()
        stringCoAcceptor.outputHex(0x018F.toShort())
        stringCoAcceptor.outputHex(0x9ABC.toShort())
        expect("018F9ABC") { stringCoAcceptor.result }
    }

    @Test fun `should output int to IntCoAcceptor`() = runBlocking {
        val stringCoAcceptor = StringCoAcceptor()
        stringCoAcceptor.outputInt(123456)
        expect("123456") { stringCoAcceptor.result }
        stringCoAcceptor.reset()
        stringCoAcceptor.outputInt(0)
        expect("0") { stringCoAcceptor.result }
        stringCoAcceptor.reset()
        stringCoAcceptor.outputInt(-1)
        expect("-1") { stringCoAcceptor.result }
        stringCoAcceptor.reset()
        stringCoAcceptor.outputInt(58974228)
        expect("58974228") { stringCoAcceptor.result }
        stringCoAcceptor.reset()
        stringCoAcceptor.outputPositiveInt(58974228)
        expect("58974228") { stringCoAcceptor.result }
        stringCoAcceptor.reset()
        stringCoAcceptor.outputInt(-7762835)
        expect("-7762835") { stringCoAcceptor.result }
    }

    @Test fun `should output Long to IntCoAcceptor`() = runBlocking {
        val stringCoAcceptor = StringCoAcceptor(8)
        stringCoAcceptor.outputLong(123456789123456789)
        expect("123456789123456789") { stringCoAcceptor.result }
        stringCoAcceptor.reset()
        stringCoAcceptor.outputLong(0)
        expect("0") { stringCoAcceptor.result }
        stringCoAcceptor.reset()
        stringCoAcceptor.outputLong(-1)
        expect("-1") { stringCoAcceptor.result }
        stringCoAcceptor.reset()
        stringCoAcceptor.outputLong(58974228)
        expect("58974228") { stringCoAcceptor.result }
        stringCoAcceptor.reset()
        stringCoAcceptor.outputPositiveLong(58974228)
        expect("58974228") { stringCoAcceptor.result }
        stringCoAcceptor.reset()
        stringCoAcceptor.outputLong(-776283544328776)
        expect("-776283544328776") { stringCoAcceptor.result }
    }

    @Test fun `should output 2 digits to IntCoAcceptor`() = runBlocking {
        val stringCoAcceptor = StringCoAcceptor(8)
        stringCoAcceptor.output2Digits(0)
        expect("00") { stringCoAcceptor.result }
        stringCoAcceptor.reset()
        stringCoAcceptor.output2Digits(3)
        expect("03") { stringCoAcceptor.result }
        stringCoAcceptor.reset()
        stringCoAcceptor.output2Digits(27)
        expect("27") { stringCoAcceptor.result }
        stringCoAcceptor.reset()
        stringCoAcceptor.output2Digits(99)
        expect("99") { stringCoAcceptor.result }
    }

    @Test fun `should output 3 digits to IntCoAcceptor`() = runBlocking {
        val stringCoAcceptor = StringCoAcceptor(8)
        stringCoAcceptor.output3Digits(0)
        expect("000") { stringCoAcceptor.result }
        stringCoAcceptor.reset()
        stringCoAcceptor.output3Digits(3)
        expect("003") { stringCoAcceptor.result }
        stringCoAcceptor.reset()
        stringCoAcceptor.output3Digits(27)
        expect("027") { stringCoAcceptor.result }
        stringCoAcceptor.reset()
        stringCoAcceptor.output3Digits(123)
        expect("123") { stringCoAcceptor.result }
    }

}
