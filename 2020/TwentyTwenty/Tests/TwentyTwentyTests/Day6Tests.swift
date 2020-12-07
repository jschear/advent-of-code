
import Foundation
import XCTest
@testable import TwentyTwenty

final class Day6Tests: XCTestCase {
    func testExample() {
        XCTAssertEqual(11, day6part1(input: example))
    }
    
    func testPart1() {
        print(day6part1(input: loadInput()))
    }
    
    func testExamplePart2() {
        XCTAssertEqual(6, day6part2(input: example))
    }
    
    func testPart2() {
        print(day6part2(input: loadInput()))
    }
    
    private func loadInput() -> String {
        let inputUrl = Bundle.module.url(forResource: "Day6", withExtension: "txt")!
        return try! String(contentsOf: inputUrl)
    }
}

private let example = """
abc

a
b
c

ab
ac

a
a
a
a

b
"""
