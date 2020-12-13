
import Foundation
import XCTest
@testable import TwentyTwenty

final class Day8Tests: XCTestCase {
    func testExample() {
        let program = Day8.parseInput(input: example)
        XCTAssertEqual(Result.looped(accumulator: 5), execute(program: program))
    }
    
    func testPart1() {
        let program = Day8.parseInput(input: loadInput())
        print(execute(program: program))
    }
    
    func testPart2() {
        let program = Day8.parseInput(input: loadInput())
        print(Day8.Part1.repairedProgramResult(program: program) ?? "No solution")
    }
    
    private func loadInput() -> String {
        loadTxtFile(named: "Day8")
    }
}

private let example = """
nop +0
acc +1
jmp +4
acc +3
jmp -3
acc -99
acc +1
jmp -4
acc +6
"""
