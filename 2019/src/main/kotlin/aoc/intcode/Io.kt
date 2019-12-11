package aoc.intcode

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.toList
import java.util.*

// Input
interface InputHandler<out T> {
    suspend fun read(): T
}

class StdIn : InputHandler<Long> {
    override suspend fun read(): Long {
        print("> ")
        return readLine()!!.toLong()
    }
}

class StaticInput<T>(vararg inputs: T) : InputHandler<T> {
    private val _inputs = ArrayDeque(inputs.toList())
    override suspend fun read(): T = _inputs.pop()
}

class ChannelInput<T>(private val channel: Channel<T>) : InputHandler<T> {
    override suspend fun read(): T = channel.receive()
}

private class InitialValuesInput<T>(
    private val delegate: InputHandler<T>,
    vararg initialValues: T
) : InputHandler<T> {
    private val _initialValues = ArrayDeque(initialValues.toList())

    override suspend fun read(): T {
        if (!_initialValues.isEmpty()) {
            return _initialValues.pop()
        }
        return delegate.read()
    }
}

fun <T> InputHandler<T>.startWith(vararg values: T): InputHandler<T> = InitialValuesInput(this, *values)

// Output
interface OutputHandler<in T> {
    suspend fun write(value: T)
}

class StdOut : OutputHandler<Long> {
    override suspend fun write(value: Long) = println(value)
}

class ListOutput<T> : OutputHandler<T> {
    private val _values = mutableListOf<T>()

    val values: List<T>
        get() = _values

    override suspend fun write(value: T) {
        _values.add(value)
    }
}

class ChannelOutput<T>(private val channel: Channel<T>) : OutputHandler<T> {
    override suspend fun write(value: T) = channel.send(value)
}
