
import Foundation
import XCTest
@testable import TwentyTwenty

final class Day10Tests: XCTestCase {
    let exampleAdapters = Day10.parseInput(input: example)
    let example2Adapters = Day10.parseInput(input: example2)
    let inputAdapters = Day10.parseInput(input: loadInput())
    
    func testExample() {
        XCTAssertEqual(7 * 5, Day10(adapters: exampleAdapters).part1())
    }
    
    func testExample2() {
        XCTAssertEqual(22 * 10, Day10(adapters: example2Adapters).part1())
    }
    
    func testPart1() {
        print(Day10(adapters: inputAdapters).part1())
    }
    
    func testExamplePart2() {
        XCTAssertEqual(8, Day10(adapters: exampleAdapters).countAdapterPaths())
    }
    
    func testExample2Part2() {
        XCTAssertEqual(19208, Day10(adapters: example2Adapters).countAdapterPaths())
    }
    
    func testPart2() {
        print(Day10(adapters: inputAdapters).countAdapterPaths())
    }
    
    private class func loadInput() -> String {
        loadTxtFile(named: "Day10")
    }
}

private let example = """
16
10
15
5
1
11
7
19
6
12
4
"""

private let example2 = """
28
33
18
42
31
14
46
20
48
47
24
23
49
45
19
38
39
11
1
32
25
35
8
17
7
9
4
2
34
10
3
"""
