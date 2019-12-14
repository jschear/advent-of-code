package aoc

import org.junit.Test
import kotlin.test.assertEquals

class Day12Test {
    private val sampleOneMoons = listOf(
        Moon(Vec3(-1, 0, 2)),
        Moon(Vec3(2, -10, -7)),
        Moon(Vec3(4, -8, 8)),
        Moon(Vec3(3, 5, -1))
    )

    private val sampleTwoMoons = listOf(
        Moon(Vec3(-8, -10, 0)),
        Moon(Vec3(5, 5, 10)),
        Moon(Vec3(2, -7, 3)),
        Moon(Vec3(9, -8, -3))
    )

    private val inputMoons = listOf(
        Moon(Vec3(3, 3, 0)),
        Moon(Vec3(4, -16, 2)),
        Moon(Vec3(-10, -6, 5)),
        Moon(Vec3(-3, 0, -13))
    )

    @Test
    fun sample() {
        assertEquals(179, runSimulationEnergy(sampleOneMoons, 10))
    }

    @Test
    fun sampleTwo() {
        assertEquals(1940, runSimulationEnergy(sampleTwoMoons, 100))
    }

    @Test
    fun partOne() {
        println(runSimulationEnergy(inputMoons, 1000))
    }

    @Test
    fun partTwoSampleOne() {
        assertEquals(2772, iterationsUntilRepeatedState(sampleOneMoons))
    }

    @Test
    fun partTwoSampleTwo() {
        assertEquals(4686774924, iterationsUntilRepeatedState(sampleTwoMoons))
    }

    @Test
    fun partTwo() {
        println(iterationsUntilRepeatedState(inputMoons))
    }
}