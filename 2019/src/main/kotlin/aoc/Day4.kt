package aoc

const val START = 367479
const val END = 893698

fun dayFourPartOne(): Int = (START..END).filter(::isValidPartOne).count()

fun isValidPartOne(password: Int): Boolean {
    val digits = password.toString()
        .split("")
        .filter(String::isNotEmpty)
        .map(String::toInt)

    val zippedDigits = digits.zipWithNext()
    return zippedDigits.all { (first, second) -> first <= second } && zippedDigits.any { (first, second) -> first == second }
}

fun dayFourPartTwo(): Int = (START..END).filter(::isValidPartTwo).count()

fun isValidPartTwo(password: Int): Boolean {
    val digits = password.toString()
        .split("")
        .filter(String::isNotEmpty)
        .map(String::toInt)

    val zippedDigits = digits.zipWithNext()
    val allIncreasing = zippedDigits.all { (first, second) -> first <= second }

    val hasTwoConsecutive = (0 until 10)
        .filter { num -> digits.count { it == num } == 2 }
        .any()

    return allIncreasing && hasTwoConsecutive
}