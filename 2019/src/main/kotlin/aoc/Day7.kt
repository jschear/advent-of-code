package aoc

import aoc.intcode.IntCode
import aoc.intcode.ListOutput
import aoc.intcode.StaticInput

private val PROGRAM = listOf(3,8,1001,8,10,8,105,1,0,0,21,42,55,64,85,98,179,260,341,422,99999,3,9,101,2,9,9,102,5,9,9,1001,9,2,9,1002,9,5,9,4,9,99,3,9,1001,9,5,9,1002,9,4,9,4,9,99,3,9,101,3,9,9,4,9,99,3,9,1002,9,4,9,101,3,9,9,102,5,9,9,101,4,9,9,4,9,99,3,9,1002,9,3,9,1001,9,3,9,4,9,99,3,9,1002,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,101,1,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,101,1,9,9,4,9,3,9,101,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,1002,9,2,9,4,9,3,9,1001,9,2,9,4,9,99,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,101,2,9,9,4,9,99,3,9,1002,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,101,2,9,9,4,9,3,9,101,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,102,2,9,9,4,9,99,3,9,102,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,1001,9,1,9,4,9,3,9,1001,9,1,9,4,9,3,9,101,1,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,1002,9,2,9,4,9,99,3,9,1001,9,1,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,1002,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,1002,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,102,2,9,9,4,9,99)

class Amplifier(phase: Int, inputSignal: Int) {
    private val listOutput = ListOutput()
    private val intCode = IntCode(PROGRAM, StaticInput(phase, inputSignal), listOutput)

    fun execute(): Int {
        intCode.execute()
        return listOutput.output.last()
    }
}

fun daySevenPartOne(): Int {
    val permutations = listOf(0, 1, 2, 3, 4).permutations()
    val outputs = permutations.map { phases ->
        val ampAOut = Amplifier(phases[0], 0).execute()
        val ampBOut = Amplifier(phases[0], ampAOut).execute()
        val ampCOut = Amplifier(phases[0], ampBOut).execute()
        val ampDOut = Amplifier(phases[0], ampCOut).execute()
        val ampEOut = Amplifier(phases[0], ampDOut).execute()
        ampEOut
    }
    return outputs.max()!!
}

fun <T> List<T>.permutations(): List<List<T>> =
    if (size == 1) {
        listOf(this)
    } else {
        indices.flatMap { i ->
            val first = this[i]
            val rest = toMutableList().apply { removeAt(i) }
            rest.permutations().map { permuted -> listOf(first) + permuted }
        }
    }