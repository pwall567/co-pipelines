package net.pwall.pipeline

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.expect

class CoUtilityTest {

    @Test fun `should count items`() = runBlocking {
        val counter = CoCounter<String>()
        counter.accept("a")
        counter.accept("b")
        counter.accept("c")
        counter.accept("d")
        expect(4) { counter.result }
    }

}
