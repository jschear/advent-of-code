
import Foundation
import XCTest
@testable import TwentyTwenty

final class Day12Tests: XCTestCase {
    private let exampleActions = Day12.parseInput(input: example)
    private let inputActions = Day12.parseInput(input: loadInput())

    func testExample() {
        XCTAssertEqual(25, Day12.Part1.manhattanDistanceAfterActions(actions: exampleActions))
    }

    func testPart1() {
        print(Day12.Part1.manhattanDistanceAfterActions(actions: inputActions))
    }
    
    func testExamplePart2() {
        XCTAssertEqual(286, Day12.Part2.manhattanDistanceAfterActions(actions: exampleActions))
    }
    
    func testPart2() {
        print(Day12.Part2.manhattanDistanceAfterActions(actions: inputActions))
    }
    
    private class func loadInput() -> String {
        loadTxtFile(named: "Day12")
    }
}

private let example = """
F10
N3
F7
R90
F11
"""
