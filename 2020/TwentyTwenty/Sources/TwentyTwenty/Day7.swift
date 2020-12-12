
import Foundation

typealias Color = String

struct ColorQuantity: Hashable {
    let color: Color
    let quantity: Int
}

typealias RuleSet = Dictionary<Color, Set<ColorQuantity>>

enum RuleParseError: Error {
    case invalidLine(String)
}

class Day7 {
    static func parseInput(input: String) -> RuleSet {
        let colorToColorQuanities = input.split(separator: "\n")
            .map { (line: Substring) -> (Color, Set<ColorQuantity>) in
                let trimmed = line.trimmingCharacters(in: CharacterSet.init(charactersIn: "."))
                let split = trimmed.components(separatedBy: " bags contain ")
                let color = split[0]
                let rest = split[1]
                switch rest {
                case "no other bags":
                    return (color, [])
                default:
                    let contains = rest.components(separatedBy: ", ")
                    let colorQuantities = try! contains.map { numberOfBags -> ColorQuantity in
                        var trimmedNumberOfBags: String
                        if numberOfBags.hasSuffix("bags") {
                            trimmedNumberOfBags = String(numberOfBags.dropLast(5))
                        } else if (numberOfBags.hasSuffix("bag")) {
                            trimmedNumberOfBags = String(numberOfBags.dropLast(4))
                        } else {
                            throw RuleParseError.invalidLine(String(line))
                        }
                        let colorQuantitySplit = trimmedNumberOfBags.split(separator: " ", maxSplits: 1)
                        let quantity = Int.init(colorQuantitySplit[0])
                        let subColor = colorQuantitySplit[1]
                        return ColorQuantity(color: String(subColor), quantity: quantity!)
                    }
                    return (color, Set(colorQuantities))
                }
            }
        return Dictionary.init(uniqueKeysWithValues: colorToColorQuanities)
    }
}

// How many colors can, eventually, contain at least one shiny gold bag?
class Day7Part1 {
    private let targetColor: Color
    private let rules: RuleSet
    private var knownCanContain = Set<Color>()
    
    init(targetColor: Color, rules: RuleSet) {
        self.targetColor = targetColor
        self.rules = rules
    }
    
    func countColorsCanContain() -> Int {
        return rules.keys.filter { color in canContainTargetColor(color: color) }.count
    }
    
    private func canContainTargetColor(color: Color) -> Bool {
        if knownCanContain.contains(color) {
            return true
        }
        let colorsAllowedToContain = rules[color]!.map { $0.color }
        let canContain = colorsAllowedToContain.contains(where: { newColor in
            newColor == targetColor || canContainTargetColor(color: newColor)
        })
        if canContain {
            knownCanContain.insert(color)
        }
        return canContain
    }
}

// How many individual bags are required inside your single shiny gold bag?
class Day7Part2 {
    private let targetColor: Color
    private let rules: RuleSet
    private var knownCounts = Dictionary<Color, Int>()
    
    init(targetColor: Color, rules: RuleSet) {
        self.targetColor = targetColor
        self.rules = rules
    }
    
    func countRequiredBags() -> Int {
        return countRequiredBags(color: targetColor)
    }
    
    private func countRequiredBags(color: Color) -> Int {
        if let cachedCount = knownCounts[color] {
            return cachedCount
        }
        let count = rules[color]!
            .map { $0.quantity + ($0.quantity * countRequiredBags(color: $0.color)) }
            .reduce(0, +)
        knownCounts[color] = count
        return count
    }
}
