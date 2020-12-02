
import Foundation

private protocol PasswordPolicy {
    func complies(password: String) -> Bool
}

private struct PartOnePolicy: PasswordPolicy {
    let occurrences: ClosedRange<Int>
    let character: Character
    
    func complies(password: String) -> Bool {
        return occurrences ~= password.filter { $0 == character }.count
    }
}

private struct PartTwoPolicy: PasswordPolicy {
    let firstIndex: Int
    let secondIndex: Int
    let character: Character
    
    func complies(password: String) -> Bool {
        let firstStringIndex = password.index(password.startIndex, offsetBy: firstIndex - 1)
        let secondStringIndex = password.index(password.startIndex, offsetBy: secondIndex - 1)

        let characterAtFirst = password[firstStringIndex]
        let characterAtSecond = password[secondStringIndex]
        
        return (character == characterAtFirst && character != characterAtSecond)
            || (character == characterAtSecond && character != characterAtFirst)
    }
}

private struct ParsedLine {
    let first: Int
    let second: Int
    let character: Character
    let password: String
}

private func parseInput(input: String, createPolicy: (ParsedLine) -> PasswordPolicy) -> [(PasswordPolicy, String)] {
    let pattern = try! NSRegularExpression(pattern: #"(?<first>\d+)-(?<second>\d+) (?<character>\D): (?<password>\D+)"#)
    return input.split(separator: "\n").map { lineSubstring in
        let line = String(lineSubstring)
        let range = NSRange(line.startIndex..<line.endIndex, in: line)
        let match = pattern.firstMatch(in: line, options: [], range: range)!
        
        let parsedLine = ParsedLine(
            first: Int(extractGroup(match: match, name: "first", string: line))!,
            second: Int(extractGroup(match: match, name: "second", string: line))!,
            character: extractGroup(match: match, name: "character", string: line).first!,
            password: String(extractGroup(match: match, name: "password", string: line))
        )
        let policy = createPolicy(parsedLine)
        return (policy, parsedLine.password)
    }
}

private func extractGroup(match: NSTextCheckingResult, name: String, string: String) -> Substring {
    let range = Range(match.range(withName: name), in: string)!
    return string[range]
}

private func countCompliant(input: String, createPolicy: (ParsedLine) -> PasswordPolicy) -> Int {
    let elements = parseInput(input: input, createPolicy: createPolicy)
    let compliant = elements.filter { policy, password in policy.complies(password: password) }
    return compliant.count
}

func day2part1(input: String) -> Int {
    return countCompliant(input: input) { parsedLine in
        PartOnePolicy(occurrences: parsedLine.first...parsedLine.second, character: parsedLine.character)
    }
}

func day2part2(input: String) -> Int {
    return countCompliant(input: input) { parsedLine in
        PartTwoPolicy(firstIndex: parsedLine.first, secondIndex: parsedLine.second, character: parsedLine.character)
    }
}
