package aoc.intcode

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.toList
import java.util.*

// Input
interface InputHandler {
    suspend fun read(): Int
}

class StdIn : InputHandler {
    override suspend fun read(): Int {
        print("> ")
        return readLine()!!.toInt()
    }
}

class StaticInput(vararg inputs: Int) : InputHandler {
    private val _inputs = ArrayDeque(inputs.toList())
    override suspend fun read(): Int = _inputs.pop()
}

class ChannelInput(private val channel: Channel<Int>) : InputHandler {
    override suspend fun read(): Int = channel.receive()
}

private class InitialValuesInput(
    private val delegate: InputHandler,
    vararg initialValues: Int
) : InputHandler {
    private val _initialValues = ArrayDeque(initialValues.toList())

    override suspend fun read(): Int {
        if (!_initialValues.isEmpty()) {
            return _initialValues.pop()
        }
        return delegate.read()
    }
}

fun InputHandler.startWith(vararg values: Int): InputHandler = InitialValuesInput(this, *values)

// Output
interface OutputHandler {
    suspend fun write(value: Int)
}

class StdOut : OutputHandler {
    override suspend fun write(value: Int) = println(value)
}

class ListOutput : OutputHandler {
    private val _values = mutableListOf<Int>()

    val values: List<Int>
        get() = _values

    override suspend fun write(value: Int) {
        _values.add(value)
    }
}

class ChannelOutput(private val channel: Channel<Int>) : OutputHandler {
    override suspend fun write(value: Int) = channel.send(value)
}
