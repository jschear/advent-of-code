package aoc

import aoc.intcode.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private val PROGRAM = listOf(3,8,1001,8,10,8,105,1,0,0,21,42,55,64,85,98,179,260,341,422,99999,3,9,101,2,9,9,102,5,9,9,1001,9,2,9,1002,9,5,9,4,9,99,3,9,1001,9,5,9,1002,9,4,9,4,9,99,3,9,101,3,9,9,4,9,99,3,9,1002,9,4,9,101,3,9,9,102,5,9,9,101,4,9,9,4,9,99,3,9,1002,9,3,9,1001,9,3,9,4,9,99,3,9,1002,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,101,1,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,101,1,9,9,4,9,3,9,101,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,1002,9,2,9,4,9,3,9,1001,9,2,9,4,9,99,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,101,2,9,9,4,9,99,3,9,1002,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,101,2,9,9,4,9,3,9,101,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,102,2,9,9,4,9,99,3,9,102,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,1001,9,1,9,4,9,3,9,1001,9,1,9,4,9,3,9,101,1,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,1002,9,2,9,4,9,99,3,9,1001,9,1,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,1002,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,1002,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,102,2,9,9,4,9,99)
    .map(Int::toLong)

fun daySevenPartOne(): Long = listOf(0, 1, 2, 3, 4)
    .permutations()
    .map { phases ->
        (0..4).fold(0L) { signal, i ->
            val output = ListOutput<Long>()
            val intCode = IntCode(
                PROGRAM,
                VarArgInput(phases[i].toLong(), signal),
                output
            )
            intCode.executeBlocking()
            output.values.last()
        }
    }
    .max()!!

fun daySevenPartTwo(): Long = listOf(5, 6, 7, 8, 9)
    .permutations()
    .map { phases -> runWithFeedback(phases) }
    .max()!!

fun runWithFeedback(phases: List<Int>): Long {
    val intCodeBuilders = List(5) { IntCodeBuilder() }

    intCodeBuilders
        .zip(phases)
        .zipWithNext() // pair consecutive items: [1, 2, 3] -> [[1, 2], [2, 3]]
        .forEach { (first, second) ->
            val (sender, _) = first
            val (receiver, phase) = second

            val channel = Channel<Long>()
            sender.output = ChannelOutput(channel)
            receiver.input = ChannelInput(channel).startWith(phase.toLong())
        }

    val start = intCodeBuilders.first()
    val end = intCodeBuilders.last()

    // Capacity of two, because we'd like the last send to complete
    val channel = Channel<Long>(2)
    start.input = ChannelInput(channel).startWith(phases.first().toLong(), 0L)
    end.output = ChannelOutput(channel)

    val intCodes = intCodeBuilders.map(IntCodeBuilder::build)

    return runBlocking {
        coroutineScope {
            intCodes.withIndex().map { (index, intCode) ->
                launch {
                    intCode.execute()
                    println("Amplifier $index halted!")
                }
            }
        }
        channel.receive()
    }
}

class IntCodeBuilder(
    var input: InputHandler<Long>? = null,
    var output: OutputHandler<Long>? = null
) {
    fun build() = IntCode(PROGRAM, input!!, output!!)
}

fun <T> List<T>.permutations(): List<List<T>> =
    if (size == 1) {
        listOf(this)
    } else {
        indices.flatMap { i ->
            val rest = toMutableList().apply { removeAt(i) }
            rest.permutations().map { permuted -> listOf(this[i]) + permuted }
        }
    }