package net.pwall.pipeline.xml

import net.pwall.pipeline.IntCoAcceptor
import net.pwall.pipeline.xxml.CoDecoderBase
import net.pwall.pipeline.xxml.MappingEntry

/**
 * XML decoder - decode text encoded with XML escaping.
 *
 * @author  Peter Wall
 * @param   R       the pipeline result type
 */
class XMLCoDecoder<R>(downstream: IntCoAcceptor<R>) : CoDecoderBase<R>(table, downstream) {

    companion object {
        val table = listOf(
            MappingEntry('&'.code, "amp"),
            MappingEntry('\''.code, "apos"),
            MappingEntry('>'.code, "gt"),
            MappingEntry('<'.code, "lt"),
            MappingEntry('"'.code, "quot"),
        ).toTypedArray() // replace this with reference to table in pipelines, when that is made public
    }

}
