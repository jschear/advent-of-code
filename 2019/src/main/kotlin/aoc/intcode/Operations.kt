package aoc.intcode

import aoc.intcode.Operation.Add

val createAdd: () -> Operation = ::Add

val OPERATIONS = mapOf(
    1 to ::Add,
    2 to Operation.Multiply,
    3 to Operation.Input,
    4 to Operation.Output,
    5 to Operation.JumpIfTrue,
    6 to Operation.JumpIfFalse,
    7 to Operation.LessThan,
    8 to Operation.Equals,
    9 to Operation.RelativeBaseOffset,
    99 to Operation.Halt
)

class OperationResult(
    val newInstructionPointer: Int,
    val halt: Boolean = false,
    val relativeBaseOffset: Int = 0
)

fun Operation.createResult(instructionPointer: Int) = OperationResult(
    instructionPointer + numParameters + 1
)

sealed class Operation(
    val numParameters: Int
) {

    abstract suspend fun execute(
        program: MutableList<Int>,
        instructionPointer: Int,
        relativeBase: Int,
        params: List<Parameter>,
        input: InputHandler,
        output: OutputHandler
    ): OperationResult

    class Add(input: InputHandler) : Operation( 3) {
        override suspend fun execute(
            program: MutableList<Int>,
            instructionPointer: Int,
            relativeBase: Int,
            params: List<Parameter>,
            input: InputHandler,
            output: OutputHandler
        ): OperationResult {
            assert(params[2].mode == ParameterMode.POSITION)

            val operandOne = params[0].resolve(program, relativeBase)
            val operandTwo = params[1].resolve(program, relativeBase)

            program[params[2].value] = operandOne + operandTwo
            return OperationResult(instructionPointer + numParameters + 1)
        }
    }

    object Multiply : Operation(3) {
        override suspend fun execute(
            program: MutableList<Int>,
            instructionPointer: Int,
            relativeBase: Int,
            params: List<Parameter>,
            input: InputHandler,
            output: OutputHandler
        ): OperationResult {
            assert(params[2].mode == ParameterMode.POSITION)

            val operandOne = params[0].resolve(program, relativeBase)
            val operandTwo = params[1].resolve(program, relativeBase)

            program[params[2].value] = operandOne * operandTwo
            return OperationResult(instructionPointer + numParameters + 1)
        }
    }

    object Input : Operation(1) {
        override suspend fun execute(
            program: MutableList<Int>,
            instructionPointer: Int,
            relativeBase: Int,
            params: List<Parameter>,
            input: InputHandler,
            output: OutputHandler
        ): OperationResult {
            assert(params[0].mode == ParameterMode.POSITION)

            program[params[0].value] = input.read()
            return OperationResult(instructionPointer + numParameters + 1)
        }
    }

    object Output : Operation( 1) {
        override suspend fun execute(
            program: MutableList<Int>,
            instructionPointer: Int,
            relativeBase: Int,
            params: List<Parameter>,
            input: InputHandler,
            output: OutputHandler
        ): OperationResult {
            val operand = params[0].resolve(program, relativeBase)
            output.write(operand)
            return OperationResult(instructionPointer + numParameters + 1)
        }
    }

    object JumpIfTrue : Operation(2) {
        override suspend fun execute(
            program: MutableList<Int>,
            instructionPointer: Int,
            relativeBase: Int,
            params: List<Parameter>,
            input: InputHandler,
            output: OutputHandler
        ): OperationResult {
            val operandOne = params[0].resolve(program, relativeBase)
            val operandTwo = params[1].resolve(program, relativeBase)
            return if (operandOne != 0) {
                OperationResult(operandTwo)
            } else {
                OperationResult(instructionPointer + numParameters + 1)
            }
        }
    }

    object JumpIfFalse : Operation(2) {
        override suspend fun execute(
            program: MutableList<Int>,
            instructionPointer: Int,
            relativeBase: Int,
            params: List<Parameter>,
            input: InputHandler,
            output: OutputHandler
        ): OperationResult {
            val operandOne = params[0].resolve(program, relativeBase)
            val operandTwo = params[1].resolve(program, relativeBase)
            return if (operandOne == 0) {
                OperationResult(operandTwo)
            } else {
                OperationResult(instructionPointer + numParameters + 1)
            }
        }
    }

    object LessThan : Operation(3) {
        override suspend fun execute(
            program: MutableList<Int>,
            instructionPointer: Int,
            relativeBase: Int,
            params: List<Parameter>,
            input: InputHandler,
            output: OutputHandler
        ): OperationResult {
            assert(params[2].mode == ParameterMode.POSITION)

            val operandOne = params[0].resolve(program, relativeBase)
            val operandTwo = params[1].resolve(program, relativeBase)

            val result = if (operandOne < operandTwo) 1 else 0
            program[params[2].value] = result

            return OperationResult(instructionPointer + numParameters + 1)
        }
    }

    object Equals : Operation(3) {
        override suspend fun execute(
            program: MutableList<Int>,
            instructionPointer: Int,
            relativeBase: Int,
            params: List<Parameter>,
            input: InputHandler,
            output: OutputHandler
        ): OperationResult {
            assert(params[2].mode == ParameterMode.POSITION)

            val operandOne = params[0].resolve(program, relativeBase)
            val operandTwo = params[1].resolve(program, relativeBase)

            val result = if (operandOne == operandTwo) 1 else 0
            program[params[2].value] = result

            return OperationResult(instructionPointer + numParameters + 1)
        }
    }

    object RelativeBaseOffset : Operation(1) {
        override suspend fun execute(
            program: MutableList<Int>,
            instructionPointer: Int,
            relativeBase: Int,
            params: List<Parameter>,
            input: InputHandler,
            output: OutputHandler
        ): OperationResult {
            val operandOne = params[0].resolve(program, relativeBase)
            return OperationResult(instructionPointer + numParameters + 1)
        }
    }

    object Halt : Operation(0) {
        override suspend fun execute(
            program: MutableList<Int>,
            instructionPointer: Int,
            params: List<Parameter>,
            input: InputHandler,
            output: OutputHandler
        ): OperationResult = OperationResult(0, halt = true)
    }
}