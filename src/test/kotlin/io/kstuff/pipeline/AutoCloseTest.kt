/*
 * @(#) AutoCloseTest.kt
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
import io.kstuff.test.shouldBeNonNull

class AutoCloseTest {

    @Test fun `should close acceptor at end of use`() = runBlocking {
        val acceptor = ListCoAcceptor<String>()
        val list = acceptor.use { it.accept("Hello!") }
        acceptor.closed shouldBe true
        list.size shouldBe 1
        list[0] shouldBe "Hello!"
    }

    @Test fun `should close acceptor even when exception thrown`() = runBlocking {
        val acceptor = ListCoAcceptor<String>()
        var exceptionThrown = false
        try {
            acceptor.use { throw IllegalArgumentException() }
        }
        catch (ignore: IllegalArgumentException) {
            exceptionThrown = true
        }
        exceptionThrown shouldBe true
        acceptor.closed shouldBe true
    }

    @Test fun `should handle exception thrown in close`() = runBlocking {
        val acceptor = TestAcceptor1
        var exceptionThrown = false
        try {
            acceptor.use { it.accept("abc") }
        }
        catch (ignore: IllegalStateException) {
            exceptionThrown = true
        }
        exceptionThrown shouldBe true
    }

    @Test fun `should handle exception thrown in both main body and close`() = runBlocking {
        val acceptor = TestAcceptor1
        var exception: Exception? = null
        try {
            acceptor.use { throw IllegalArgumentException() }
        }
        catch (e: IllegalArgumentException) {
            exception = e
        }
        exception.shouldBeNonNull()
        (exception.suppressed[0] is TestCloseException) shouldBe true
    }

    object TestAcceptor1 : AbstractCoAcceptor<String, String>() {

        override suspend fun acceptObject(value: String) {
            // do nothing
        }

        override suspend fun close() {
            throw TestCloseException()
        }

        override val result: String
            get() = ""

    }

    class TestCloseException : IllegalStateException("Test Close")

}
