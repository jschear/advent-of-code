package aoc

import kotlin.math.abs
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@UseExperimental(ExperimentalTime::class)
fun flawedFrequencyTransmission(input: List<Int>, phases: Int): List<Int> {
    return (1..phases).fold(input) { list, phase ->
        val (value, duration) = measureTimedValue {
            runPhase(list)
        }
        println("Phase $phase took ${duration.inSeconds}")
        value
    }
}

fun runPhase(input: List<Int>): List<Int> =
    input.indices.map { outputPos ->
        input.withIndex()
            .fold(0) { acc, (index, element) -> acc + element * patternVal(index + 1, outputPos + 1) }
            .let { abs(it) % 10 }
    }

val PATTERN_BASE = listOf(0, 1, 0, -1)
fun patternVal(inputPosition: Int, outputPosition: Int): Int = PATTERN_BASE[(inputPosition / outputPosition) % 4]

fun parseInput(input: String): List<Int> = input.split("")
    .filter(String::isNotEmpty)
    .map(String::toInt)

fun List<Int>.formatOutput(): String = joinToString("", transform = Int::toString)

fun List<Int>.firstEightDigits() = take(8).formatOutput()


// This took some inspiration from reddit. The main insight is that after the nth output position,
// the pattern is all zeros, followed by all ones. Instead of multiplying each value by a coefficient from the pattern,
// we can sum up the values, starting from the end of the list.
fun partTwo(input: String): String {
    val offset = input.take(7).toInt()
    val choppedSignal = parseInput(
        input.repeat(10000).drop(offset)
    )

    return (1..100)
        .fold(choppedSignal.reversed()) { signal, _ -> runOptimizedReversedPhase(signal) }
        .reversed()
        .firstEightDigits()
}

fun runOptimizedReversedPhase(input: List<Int>): List<Int> = input
    .scan(0) { previous, value -> previous + value }
    .map { it % 10 }

// Why isn't this in the stdlib?! ;)
private inline fun <T, R> Iterable<T>.scan(initial: R, operation: (previous: R, T) -> R): List<R> {
    val accumulator = arrayListOf(initial)
    for (element in this) accumulator.add(operation(accumulator.last(), element))
    return accumulator
}



// From first attempt...
private fun patternForPosition(position: Int): Sequence<Int> = singlePatternForPosition(position)
    .repeatIndefinitely()
    .drop(1)

// 0, 1, 0, -1 (each n times)
private fun singlePatternForPosition(position: Int): Sequence<Int> = sequence {
    val repetitions = position + 1
    fun <T> identity(it: T): T = it
    yieldAll(generateSequence(0, ::identity).take(repetitions))
    yieldAll(generateSequence(1, ::identity).take(repetitions))
    yieldAll(generateSequence(0, ::identity).take(repetitions))
    yieldAll(generateSequence(-1, ::identity).take(repetitions))
}

private fun <T> Sequence<T>.repeatIndefinitely(): Sequence<T> = generateSequence { this }.flatten()
