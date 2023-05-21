package net.pwall.pipeline.xml

import net.pwall.pipeline.IntCoAcceptor
import net.pwall.pipeline.xxml.CoDecoderBase

/**
 * XML decoder - decode text encoded with XML escaping.
 *
 * @author  Peter Wall
 * @param   R       the pipeline result type
 */
class XMLCoDecoder<out R>(downstream: IntCoAcceptor<R>) : CoDecoderBase<R>(XMLDecoder.table, downstream)
