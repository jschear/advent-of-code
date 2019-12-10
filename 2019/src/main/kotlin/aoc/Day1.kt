package aoc

object One

fun moduleMasses(): List<Int> {
    val input = One::class.java.getResource("one.txt")
    return input.readText().lines().filter(String::isNotEmpty).map(String::toInt)
}

fun fuelForMass(mass: Int) = (mass / 3) - 2

fun dayOnePartOne() = moduleMasses().map(::fuelForMass).sum()

fun dayOnePartTwo() = moduleMasses()
    .map { mass ->
        val startFuel = fuelForMass(mass)
        var totalFuel = 0
        var incrementalFuel = startFuel
        while (incrementalFuel >= 0) {
            totalFuel += incrementalFuel
            incrementalFuel = fuelForMass(incrementalFuel)
        }
        return@map totalFuel
    }.sum()
