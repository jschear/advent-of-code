package aoc

import aoc.intcode.IntCode
import aoc.intcode.ListOutput
import aoc.intcode.VarArgInput

val DRONE_PROGRAM = Drone::class.java.getResource("nineteen.txt")
    .readText()
    .split(",")
    .map(String::toLong)

class Drone(x: Int, y: Int) {
    private val listOutput = ListOutput<Long>()
    private val intCode: IntCode = IntCode(DRONE_PROGRAM, VarArgInput(x.toLong(), y.toLong()), listOutput)

    fun send(): Long {
        intCode.executeBlocking()
        return listOutput.values.first()
    }
}

fun isBeingPulled(x: Int, y: Int): Boolean {
    return Drone(x, y).send() == 1L
}

fun pointsAffected(): Int =
    (0 until 50)
        .flatMap { y -> (0 until 50).map { x -> x to y } }
        .count { (x, y) -> isBeingPulled(x, y) }

fun closestSquare(): Int {
    var x = 0
    var y = 0

    while (!isBeingPulled(x + 99, y)) {
        // Move down until the point 99 to the right is in the beam
        y += 1
        while (!isBeingPulled(x, y + 99)) {
            // Move right until the point 99 below is in the beam
            x += 1
        }
    }
    return (x * 10000 + y)
}
