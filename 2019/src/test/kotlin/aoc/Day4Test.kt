package aoc

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Day4Test {
    @Test
    fun partOneSamples() {
        assertTrue(isValidPartOne(122345))
        assertTrue(isValidPartOne(111111))
        assertFalse(isValidPartOne(223450))
        assertFalse(isValidPartOne(123789))
    }

    @Test
    fun partOne() {
        println(dayFourPartOne())
    }

    @Test
    fun partTwoSamples() {
        assertTrue(isValidPartTwo(112233))
        assertFalse(isValidPartTwo(123444))
        assertTrue(isValidPartTwo(111122))
    }

    @Test
    fun partTwo() {
        println(dayFourPartTwo())
    }
}