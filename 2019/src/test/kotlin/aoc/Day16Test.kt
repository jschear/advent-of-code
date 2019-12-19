package aoc

import org.junit.Test
import kotlin.test.assertEquals

class Day16Test {

    private val sampleInput = "12345678"
    private val bigSampleInput = "80871224585914546619083218645595"
    private val bigSampleInputTwo = "19617804207202209144916044189917"
    private val bigSampleInputThree = "69317163492948606335995924319873"

    private val input = "59775675999083203307460316227239534744196788252810996056267313158415747954523514450220630777434694464147859581700598049220155996171361500188470573584309935232530483361639265796594588423475377664322506657596419440442622029687655170723364080344399753761821561397734310612361082481766777063437812858875338922334089288117184890884363091417446200960308625363997089394409607215164553325263177638484872071167142885096660905078567883997320316971939560903959842723210017598426984179521683810628956529638813221927079630736290924180307474765551066444888559156901159193212333302170502387548724998221103376187508278234838899434485116047387731626309521488967864391"

    @Test
    fun sampleOnePhase() {
        assertEquals(
            "48226158",
            flawedFrequencyTransmission(parseInput(sampleInput), 1).formatOutput()
        )
    }

    @Test
    fun sampleTwoPhases() {
        assertEquals(
            "34040438",
            flawedFrequencyTransmission(parseInput(sampleInput), 2).formatOutput()
        )
    }

    @Test
    fun sampleThreePhases() {
        assertEquals(
            "03415518",
            flawedFrequencyTransmission(parseInput(sampleInput), 3).formatOutput()
        )
    }

    @Test
    fun sampleFourPhases() {
        assertEquals(
            "01029498",
            flawedFrequencyTransmission(parseInput(sampleInput), 4).formatOutput()
        )
    }

    @Test
    fun firstEightBigSampleOne() {
        assertEquals(
            "24176176",
            flawedFrequencyTransmission(parseInput(bigSampleInput), 100).firstEightDigits()
        )
    }

    @Test
    fun firstEightBigSampleTwo() {
        assertEquals(
            "73745418",
            flawedFrequencyTransmission(parseInput(bigSampleInputTwo), 100).firstEightDigits()
        )
    }

    @Test
    fun firstEightBigSampleThree() {
        assertEquals(
            "52432133",
            flawedFrequencyTransmission(parseInput(bigSampleInputThree), 100).firstEightDigits()
        )
    }

    @Test
    fun testPartOne() {
        println(flawedFrequencyTransmission(parseInput(input), 100).firstEightDigits())
    }

    @Test
    fun partTwoSampleOne() {
        assertEquals("84462026", partTwo("03036732577212944063491565474664"))
    }

    @Test
    fun partTwoSampleTwo() {
        assertEquals("78725270", partTwo("02935109699940807407585447034323"))
    }

    @Test
    fun partTwoSampleThree() {
        assertEquals("53553731", partTwo("03081770884921959731165446850517"))
    }

    @Test
    fun testPartTwo() {
        println(partTwo(input))
    }
}