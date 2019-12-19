package aoc

import aoc.intcode.Program
import org.junit.Ignore
import org.junit.Test

class Day13Test {
    @Test
    @Ignore
    fun partOne() {
        val grid = Array(26) { IntArray(40) }
        val game = ArcadeGame(grid, loadProgram())
        game.run()

        val blocks = grid.flatMap(IntArray::toList).count { it == 2 }
        println(blocks)
    }

    @Test
    fun partTwo() {
        val grid = Array(26) { IntArray(40) }
        val game = ArcadeGame(grid, loadProgram(wthQuarters = true))
        game.run()

        println(game.score)
    }

    private fun loadProgram(wthQuarters: Boolean = false): Program =
        Day13Test::class.java.getResource("thirteen.txt")
            .readText()
            .trim()
            .split(",")
            .map(String::toLong)
            .let { program ->
                if (wthQuarters) {
                    program.toMutableList().also { it[0] = 2 }
                } else program
            }
}