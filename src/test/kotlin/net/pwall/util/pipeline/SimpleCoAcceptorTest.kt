package net.pwall.util.pipeline

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SimpleCoAcceptorTest {

    @Test fun `should pipeline data to a simple acceptor`() {
        runBlocking {
            val list = ArrayList<Int>()
            val pipeline = simpleCoAcceptor<Int> {
                list.add(it)
            }
            pipeline.accept(12345)
            pipeline.accept(67890)
            pipeline.accept(888)
            assertFalse(pipeline.closed)
            pipeline.accept(null)
            assertTrue(pipeline.closed)
            assertEquals(3, list.size)
            assertEquals(12345, list[0])
            assertEquals(67890, list[1])
            assertEquals(888, list[2])
        }
    }

}
