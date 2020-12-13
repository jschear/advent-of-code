
import Foundation

struct Instruction {
    let op: Operation
    let arg: Int
}

enum Operation {
    case acc
    case jmp
    case nop
}

enum Result: Equatable {
    case looped(accumulator: Int)
    case completed(accumulator: Int)
}

func execute(program: [Instruction]) -> Result {
    var visitedInstructions = Set<Int>()
    var accumulator = 0
    var instructionPointer = 0
    
    while instructionPointer != program.count {
        if visitedInstructions.contains(instructionPointer) {
            return Result.looped(accumulator: accumulator)
        }
        visitedInstructions.insert(instructionPointer)
        let instruction = program[instructionPointer]
        switch instruction.op {
        case .acc:
            accumulator += instruction.arg
            instructionPointer += 1
        case .jmp:
            instructionPointer += instruction.arg
        case .nop:
            instructionPointer += 1
        }
    }
    return Result.completed(accumulator: accumulator)
}

class Day8 {
    class Part1 {
        static func repairedProgramResult(program: [Instruction]) -> Int? {
            for (index, instruction) in program.enumerated() {
                var newProgram: [Instruction]
                switch instruction.op {
                case .jmp:
                    newProgram = program
                    newProgram[index] = Instruction(op: .nop, arg: instruction.arg)
                case .nop:
                    newProgram = program
                    newProgram[index] = Instruction(op: .jmp, arg: instruction.arg)
                default:
                    continue
                }
                
                switch execute(program: newProgram) {
                case .looped(_):
                    continue
                case let .completed(accumulator):
                    return accumulator
                }
            }
            return nil
        }
    }
    
    static func parseInput(input: String) -> [Instruction] {
        try! input.split(separator: "\n")
            .map { line in
                let split = line.split(separator: " ")
                let stringOp = split[0]
                let stringArg = split[1]
                
                let op: Operation
                switch stringOp {
                case "acc":
                    op = .acc
                case "jmp":
                    op = .jmp
                case "nop":
                    op = .nop
                default:
                    throw ParseError.invalidLine(String(line))
                }
                
                let arg = Int(stringArg)!
                return Instruction(op: op, arg: arg)
            }
    }
    
    enum ParseError: Error {
        case invalidLine(String)
    }
}
