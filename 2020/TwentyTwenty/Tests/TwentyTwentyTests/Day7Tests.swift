
import Foundation
import XCTest
@testable import TwentyTwenty

final class Day7Tests: XCTestCase {
    func testParseInput() {
        let rules = Day7.parseInput(input: example)
        let expected = [
            "light red": Set([
                ColorQuantity(color: "bright white", quantity: 1),
                ColorQuantity(color: "muted yellow", quantity: 2)
            ]),
            "dark orange": Set([
                ColorQuantity(color: "bright white", quantity: 3),
                ColorQuantity(color: "muted yellow", quantity: 4)
            ])
        ]
        for (key, value) in expected {
            XCTAssertEqual(value, rules[key]!)
        }
    }
    
    func testExamplePart1() {
        let rules = Day7.parseInput(input: example)
        let part1 = Day7Part1(targetColor: "shiny gold", rules: rules)
        XCTAssertEqual(4, part1.countColorsCanContain())
    }
    
    func testPart1() {
        let rules = Day7.parseInput(input: loadInput())
        let part1 = Day7Part1(targetColor: "shiny gold", rules: rules)
        print(part1.countColorsCanContain())
    }
    
    func testExamplePart2() {
        let rules = Day7.parseInput(input: examplePart2)
        let part2 = Day7Part2(targetColor: "shiny gold", rules: rules)
        XCTAssertEqual(126, part2.countRequiredBags())
    }
    
    func testPart2() {
        let rules = Day7.parseInput(input: loadInput())
        let part2 = Day7Part2(targetColor: "shiny gold", rules: rules)
        print(part2.countRequiredBags())
    }
    
    private func loadInput() -> String {
        loadTxtFile(named: "Day7")
    }
}

private let example = """
light red bags contain 1 bright white bag, 2 muted yellow bags.
dark orange bags contain 3 bright white bags, 4 muted yellow bags.
bright white bags contain 1 shiny gold bag.
muted yellow bags contain 2 shiny gold bags, 9 faded blue bags.
shiny gold bags contain 1 dark olive bag, 2 vibrant plum bags.
dark olive bags contain 3 faded blue bags, 4 dotted black bags.
vibrant plum bags contain 5 faded blue bags, 6 dotted black bags.
faded blue bags contain no other bags.
dotted black bags contain no other bags.
"""

private let examplePart2 = """
shiny gold bags contain 2 dark red bags.
dark red bags contain 2 dark orange bags.
dark orange bags contain 2 dark yellow bags.
dark yellow bags contain 2 dark green bags.
dark green bags contain 2 dark blue bags.
dark blue bags contain 2 dark violet bags.
dark violet bags contain no other bags.
"""
