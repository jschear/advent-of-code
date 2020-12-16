
import Foundation
import XCTest
@testable import TwentyTwenty

final class Day11Tests: XCTestCase {
    private let exampleWaitingArea = Day11.parseInput(input: example)
    private let inputWaitingArea = Day11.parseInput(input: loadInput())

    func testSerialization() {
        XCTAssertEqual(example, exampleWaitingArea.asString())
        print(exampleWaitingArea.asString())
    }
    
    func testExample() {
        XCTAssertEqual(37, Day11.occupiedAtFixedPoint(initialDeck: exampleWaitingArea, nextDeck: Day11.Part1.nextDeck))
    }

    func testPart1() {
       print(Day11.occupiedAtFixedPoint(initialDeck: inputWaitingArea, nextDeck: Day11.Part1.nextDeck))
    }
    
    func testExamplePart2() {
        XCTAssertEqual(26, Day11.occupiedAtFixedPoint(initialDeck: exampleWaitingArea, nextDeck: Day11.Part2.nextDeck))
    }
    
    func testPart2() {
        print(Day11.occupiedAtFixedPoint(initialDeck: inputWaitingArea, nextDeck: Day11.Part2.nextDeck))
    }
    
    private class func loadInput() -> String {
        loadTxtFile(named: "Day11")
    }
}

private let example = """
L.LL.LL.LL
LLLLLLL.LL
L.L.L..L..
LLLL.LL.LL
L.LL.LL.LL
L.LLLLL.LL
..L.L.....
LLLLLLLLLL
L.LLLLLL.L
L.LLLLL.LL
"""
