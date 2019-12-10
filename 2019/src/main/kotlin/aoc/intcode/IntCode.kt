package aoc.intcode

import kotlinx.coroutines.runBlocking
import kotlin.coroutines.coroutineContext
import kotlin.math.pow

typealias Program = List<Int>

class IntCode(
    inputProgram: Program,
    private val inputHandler: InputHandler = StdIn(),
    private val outputHandler: OutputHandler = StdOut()
) {
    private val program = inputProgram.toMutableList()

    fun executeBlocking() = runBlocking { execute() }

    suspend fun execute(): Program {
        var instructionPointer = 0
        while (true) {
            val instruction = program[instructionPointer]
            val opCode = instruction % 100
            val operation = OPERATIONS[opCode] ?: error("Unknown opcode: $opCode")

            val params = (1..operation.numParameters).map { paramNumber ->
                val divisor = 10f.pow(1 + paramNumber).toInt()
                val paramModeCode = (instruction / divisor) % 10
                log("  resolving param: $paramNumber, $divisor, $paramModeCode")
                val parameterMode = PARAMETER_MODES[paramModeCode] ?: error("Unknown parameter mode: $paramModeCode")
                val value = program[instructionPointer + paramNumber]
                Parameter(parameterMode, value)
            }

            log("Executing operation: $operation")
            log("  with parameters: $params")

            val result = operation.execute(program, instructionPointer, params, inputHandler, outputHandler)
            if (result.halt) {
                return program
            }
            instructionPointer = result.newInstructionPointer
        }
    }
}

const val DEBUG = false

fun log(log: String) {
    if (DEBUG) {
        println(log)
    }
}
