
import Foundation
import XCTest
@testable import TwentyTwenty

final class Day9Tests: XCTestCase {
    let exampleData = Day9.parseInput(input: example)
    let inputData = Day9.parseInput(input: Day9Tests.loadInput())
    
    func testExample() {
        XCTAssertEqual(127, Day9(windowSize: 5, data: exampleData).findFirstError())
    }
    
    func testPart1() {
        print(Day9(windowSize: 25, data: inputData).findFirstError() ?? "No errors")
    }
    
    func testExamplePart2() {
        XCTAssertEqual(62, Day9(windowSize: 5, data: exampleData).findWeakness())
    }
    
    func testPart2() {
        print(Day9(windowSize: 25, data: inputData).findWeakness() ?? "No weakness")
    }
    
    private class func loadInput() -> String {
        loadTxtFile(named: "Day9")
    }
}

private let example = """
35
20
15
25
47
40
62
55
65
95
102
117
150
182
127
219
299
277
309
576
"""
