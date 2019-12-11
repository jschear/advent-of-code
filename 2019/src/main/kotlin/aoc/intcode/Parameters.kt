package aoc.intcode

val PARAMETER_MODES = mapOf(
    0 to ParameterMode.POSITION,
    1 to ParameterMode.IMMEDIATE,
    2 to ParameterMode.RELATIVE
)

enum class ParameterMode { POSITION, IMMEDIATE, RELATIVE }

data class Parameter(val mode: ParameterMode, val value: Int)

fun Parameter.resolve(program: List<Int>, relativeBase: Int): Int = when (mode) {
    ParameterMode.POSITION -> program[value]
    ParameterMode.IMMEDIATE -> value
    ParameterMode.RELATIVE -> program[relativeBase + value]
}