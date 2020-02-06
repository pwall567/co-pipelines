package net.pwall.util.pipeline

class TestCoIntAcceptor : CoAbstractIntAcceptor<List<Int>>() {

    val list = ArrayList<Int>()

    override suspend fun acceptInt(value: Int) {
        list.add(value)
    }

    override val result: List<Int>
        get() = list

}
