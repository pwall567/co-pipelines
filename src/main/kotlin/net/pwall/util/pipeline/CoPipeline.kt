package net.pwall.util.pipeline

interface CoAcceptor<in A, out R> : AutoCloseable {
    val complete: Boolean
    val closed: Boolean
    suspend fun accept(value: A)
    val result: R
}

interface CoPipeline<in A, in E, out R> : CoAcceptor<A, R> {
    suspend fun emit(value: E)
}

abstract class CoAbstractAcceptor<in A, out R> : CoAcceptor<A, R> {

    override val complete: Boolean = true

    private var _closed = false
    override val closed: Boolean
        get() = _closed

    override fun close() {
        check(complete) { "Sequence not complete" }
        _closed = true
    }

}
