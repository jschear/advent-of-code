package aoc

import org.junit.Test
import kotlin.test.assertEquals

class Day5Test {
    @Test
    fun testSamples() {
        val testProgram = listOf(3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99)
            .map(Int::toLong)
        assertEquals(999, executeProgram(testProgram, 7))
        assertEquals(1000, executeProgram(testProgram, 8))
        assertEquals(1001, executeProgram(testProgram, 15))
    }

    @Test
    fun testPartOne() {
        println(dayFivePartOne())
    }

    @Test
    fun testPartTwo() {
        println(dayFivePartTwo())
    }
}