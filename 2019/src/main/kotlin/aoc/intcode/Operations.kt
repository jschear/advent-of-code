package aoc.intcode

val OPERATIONS = mapOf(
    1L to Operation.Add,
    2L to Operation.Multiply,
    3L to Operation.Input,
    4L to Operation.Output,
    5L to Operation.JumpIfTrue,
    6L to Operation.JumpIfFalse,
    7L to Operation.LessThan,
    8L to Operation.Equals,
    9L to Operation.RelativeBaseOffset,
    99L to Operation.Halt
)

class OperationResult(
    val newInstructionPointer: Int,
    val halt: Boolean = false,
    val relativeBaseOffset: Int = 0
)

sealed class Operation(val numParameters: Int) {

    abstract suspend fun execute(
        program: MutableList<Long>,
        instructionPointer: Int,
        relativeBase: Int,
        params: List<Parameter>,
        input: InputHandler<Long>,
        output: OutputHandler<Long>
    ): OperationResult

    object Add : Operation( 3) {
        override suspend fun execute(
            program: MutableList<Long>,
            instructionPointer: Int,
            relativeBase: Int,
            params: List<Parameter>,
            input: InputHandler<Long>,
            output: OutputHandler<Long>
        ): OperationResult {
            assert(params[2].mode != ParameterMode.IMMEDIATE)

            val operandOne = params[0].resolve(program, relativeBase)
            val operandTwo = params[1].resolve(program, relativeBase)

            program[params[2].address(relativeBase)] = operandOne + operandTwo
            return OperationResult(instructionPointer + numParameters + 1)
        }
    }

    object Multiply : Operation(3) {
        override suspend fun execute(
            program: MutableList<Long>,
            instructionPointer: Int,
            relativeBase: Int,
            params: List<Parameter>,
            input: InputHandler<Long>,
            output: OutputHandler<Long>
        ): OperationResult {
            assert(params[2].mode != ParameterMode.IMMEDIATE)

            val operandOne = params[0].resolve(program, relativeBase)
            val operandTwo = params[1].resolve(program, relativeBase)

            program[params[2].address(relativeBase)] = operandOne * operandTwo
            return OperationResult(instructionPointer + numParameters + 1)
        }
    }

    object Input : Operation(1) {
        override suspend fun execute(
            program: MutableList<Long>,
            instructionPointer: Int,
            relativeBase: Int,
            params: List<Parameter>,
            input: InputHandler<Long>,
            output: OutputHandler<Long>
        ): OperationResult {
            assert(params[0].mode != ParameterMode.IMMEDIATE)
            program[params[0].address(relativeBase)] = input.read()
            return OperationResult(instructionPointer + numParameters + 1)
        }
    }

    object Output : Operation( 1) {
        override suspend fun execute(
            program: MutableList<Long>,
            instructionPointer: Int,
            relativeBase: Int,
            params: List<Parameter>,
            input: InputHandler<Long>,
            output: OutputHandler<Long>
        ): OperationResult {
            val operand = params[0].resolve(program, relativeBase)
            output.write(operand)
            return OperationResult(instructionPointer + numParameters + 1)
        }
    }

    object JumpIfTrue : Operation(2) {
        override suspend fun execute(
            program: MutableList<Long>,
            instructionPointer: Int,
            relativeBase: Int,
            params: List<Parameter>,
            input: InputHandler<Long>,
            output: OutputHandler<Long>
        ): OperationResult {
            val operandOne = params[0].resolve(program, relativeBase)
            val operandTwo = params[1].resolve(program, relativeBase)
            return if (operandOne != 0L) {
                OperationResult(operandTwo.toInt())
            } else {
                OperationResult(instructionPointer + numParameters + 1)
            }
        }
    }

    object JumpIfFalse : Operation(2) {
        override suspend fun execute(
            program: MutableList<Long>,
            instructionPointer: Int,
            relativeBase: Int,
            params: List<Parameter>,
            input: InputHandler<Long>,
            output: OutputHandler<Long>
        ): OperationResult {
            val operandOne = params[0].resolve(program, relativeBase)
            val operandTwo = params[1].resolve(program, relativeBase)
            return if (operandOne == 0L) {
                OperationResult(operandTwo.toInt())
            } else {
                OperationResult(instructionPointer + numParameters + 1)
            }
        }
    }

    object LessThan : Operation(3) {
        override suspend fun execute(
            program: MutableList<Long>,
            instructionPointer: Int,
            relativeBase: Int,
            params: List<Parameter>,
            input: InputHandler<Long>,
            output: OutputHandler<Long>
        ): OperationResult {
            assert(params[2].mode != ParameterMode.IMMEDIATE)

            val operandOne = params[0].resolve(program, relativeBase)
            val operandTwo = params[1].resolve(program, relativeBase)

            val result = if (operandOne < operandTwo) 1L else 0L
            program[params[2].address(relativeBase)] = result

            return OperationResult(instructionPointer + numParameters + 1)
        }
    }

    object Equals : Operation(3) {
        override suspend fun execute(
            program: MutableList<Long>,
            instructionPointer: Int,
            relativeBase: Int,
            params: List<Parameter>,
            input: InputHandler<Long>,
            output: OutputHandler<Long>
        ): OperationResult {
            assert(params[2].mode != ParameterMode.IMMEDIATE)

            val operandOne = params[0].resolve(program, relativeBase)
            val operandTwo = params[1].resolve(program, relativeBase)

            val result = if (operandOne == operandTwo) 1L else 0L
            program[params[2].address(relativeBase)] = result

            return OperationResult(instructionPointer + numParameters + 1)
        }
    }

    object RelativeBaseOffset : Operation(1) {
        override suspend fun execute(
            program: MutableList<Long>,
            instructionPointer: Int,
            relativeBase: Int,
            params: List<Parameter>,
            input: InputHandler<Long>,
            output: OutputHandler<Long>
        ): OperationResult {
            val operandOne = params[0].resolve(program, relativeBase)
            return OperationResult(instructionPointer + numParameters + 1, relativeBaseOffset = operandOne.toInt())
        }
    }

    object Halt : Operation(0) {
        override suspend fun execute(
            program: MutableList<Long>,
            instructionPointer: Int,
            relativeBase: Int,
            params: List<Parameter>,
            input: InputHandler<Long>,
            output: OutputHandler<Long>
        ): OperationResult = OperationResult(0, halt = true)
    }
}