package aoc.intcode

import java.util.*

interface InputHandler {
    fun read(): Int
}

interface OutputHandler {
    fun write(value: Int)
}

class StdIn : InputHandler {
    override fun read(): Int {
        print("> ")
        return readLine()!!.toInt()
    }
}

class StaticInput(vararg inputs: Int) : InputHandler {
    private val _inputs = ArrayDeque(inputs.toList())
    override fun read(): Int = _inputs.pop()
}

class StdOut : OutputHandler {
    override fun write(value: Int) {
        println(value)
    }
}

class ListOutput : OutputHandler {
    private val _output = mutableListOf<Int>()

    val output: List<Int>
        get() = _output

    override fun write(value: Int) {
        _output.add(value)
    }
}