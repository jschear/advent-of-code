package aoc

import aoc.intcode.IntCode
import aoc.intcode.ListInput
import aoc.intcode.ListOutput

val DROID_PROGRAM = SpringDroid::class.java.getResource("twentyone.txt")
    .readText()
    .split(",")
    .map(String::toLong)

class SpringDroid(springScriptProgram: String) {
    private val listOutput = ListOutput<Long>()
    private val intCode: IntCode = IntCode(DROID_PROGRAM, ListInput(springScriptProgram.toInput()), listOutput)

    fun execute() {
        intCode.executeBlocking()

        val (nonascii, ascii) = listOutput.values.partition { it > 127 }
        println(ascii.joinToString("") { it.toChar().toString() })
        println(nonascii)
    }
}

fun day21partOne() {
    // Takes 4 spaces to jump...
    // If there's a hole one, two, or three away and not one four away, jump.
    // (NOT A) OR (NOT B) OR (NOT C) AND (D)

    val program = """
        NOT A J
        NOT B T
        OR T J
        NOT C T
        OR T J
        AND D J
        WALK

    """.trimIndent()

    val droid = SpringDroid(program)
    droid.execute()
}

fun day21partTwo() {
    // Failure one:
    // ..@..............
    // #####.#.##.##.###
    //   0123456789
    //    ABCDEFGHI
    // Do part one, but don't jump if there's a hole at 8
    // AND H

    // Failure two:
    // ..@..............
    // #####.#.##.##.###
    //   0123456789
    //    ABCDEFGHI

    // Do part 1, but don't jump if there's a hole at 8 and no ground at 5
    // (NOT A) OR (NOT B) OR (NOT C) AND (D) AND (E OR H)
    val program = """
        NOT A J
        NOT B T
        OR T J
        NOT C T
        OR T J
        AND D J
        NOT E T
        NOT T T
        OR H T
        AND T J
        RUN

    """.trimIndent()

    val droid = SpringDroid(program)
    droid.execute()
}