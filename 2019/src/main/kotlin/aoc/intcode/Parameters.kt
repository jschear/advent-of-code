package aoc.intcode

val PARAMETER_MODES = mapOf(
    0 to ParameterMode.POSITION,
    1 to ParameterMode.IMMEDIATE
)

enum class ParameterMode { POSITION, IMMEDIATE }

data class Parameter(val mode: ParameterMode, val value: Int)

fun Parameter.resolve(program: List<Int>): Int = when (mode) {
    ParameterMode.POSITION -> program[value]
    ParameterMode.IMMEDIATE -> value
}