package net.pwall.util.pipeline

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.expect

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
        stringCoAcceptor.outputInt(-7762835)
        expect("-7762835") { stringCoAcceptor.result }
    }

    @Test fun `should output Long to IntCoAcceptor`() = runBlocking {
        val stringCoAcceptor = StringCoAcceptor()
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
        stringCoAcceptor.outputLong(-776283544328776)
        expect("-776283544328776") { stringCoAcceptor.result }
    }

}
