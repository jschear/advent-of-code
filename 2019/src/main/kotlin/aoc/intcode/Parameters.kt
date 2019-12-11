package aoc.intcode

val PARAMETER_MODES = mapOf(
    0L to ParameterMode.POSITION,
    1L to ParameterMode.IMMEDIATE,
    2L to ParameterMode.RELATIVE
)

enum class ParameterMode { POSITION, IMMEDIATE, RELATIVE }

data class Parameter(val mode: ParameterMode, val value: Long)

fun Parameter.resolve(program: Program, relativeBase: Int): Long = when (mode) {
    ParameterMode.IMMEDIATE -> value
    else -> program[address(relativeBase)]
}

fun Parameter.address(relativeBase: Int): Int = when (mode) {
    ParameterMode.POSITION -> value.toInt()
    ParameterMode.RELATIVE -> value.toInt() + relativeBase
    ParameterMode.IMMEDIATE -> error("Immediate paramters do not reference an address")
}