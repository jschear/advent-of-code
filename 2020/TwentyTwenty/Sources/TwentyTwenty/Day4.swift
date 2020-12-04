import Foundation

func day4part1(input: String) -> Int {
    let passports = parseInput(input: input)
    return passports.filter(Part1Policy().isValid).count
}

func day4part2(input: String) -> Int {
    let passports = parseInput(input: input)
    return passports.filter(Part2Policy().isValid).count
}

private typealias Passport = Dictionary<String, String>

private protocol Policy {
    func isValid(passport: Passport) -> Bool
}

private class Part1Policy: Policy {
    func isValid(passport: Passport) -> Bool {
        return ["byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid"].allSatisfy {
            passport.keys.contains($0)
        }
    }
}

private class Part2Policy: Policy {
    func isValid(passport: Passport) -> Bool {
        return intInRange(value: passport["byr"], range: 1920...2002)
            && intInRange(value: passport["iyr"], range: 2010...2020)
            && intInRange(value: passport["eyr"], range: 2020...2030)
            && validHeight(value: passport["hgt"])
            && validHairColor(value: passport["hcl"])
            && validEyeColor(value: passport["ecl"])
            && validPassportId(value: passport["pid"])
    }
    
    private func intInRange(value: String?, range: ClosedRange<Int>) -> Bool {
        guard let unwrappedValue = value else { return false }
        guard let intValue = Int.init(unwrappedValue) else { return false }
        return range ~= intValue
    }
    
    private func validHeight(value: String?) -> Bool {
        guard let unwrappedValue = value else { return false }
        let splitIndex = unwrappedValue.index(unwrappedValue.endIndex, offsetBy: -2)
        switch (unwrappedValue[..<splitIndex], unwrappedValue[splitIndex...]) {
        case (let rest, "in"):
            return intInRange(value: String(rest), range: 59...76)
        case (let rest, "cm"):
            return intInRange(value: String(rest), range: 150...193)
        default:
            return false
        }
    }
    
    private func validHairColor(value: String?) -> Bool {
        guard let unwrapped = value else { return false }
        return nil != unwrapped.range(of: #"^#[\da-f]{6}$"#, options: .regularExpression)
    }
    
    private func validEyeColor(value: String?) -> Bool {
        guard let unwrapped = value else { return false }
        let colors: Set = ["amb", "blu", "brn", "gry", "grn", "hzl", "oth"]
        return colors.contains(unwrapped)
    }
    
    private func validPassportId(value: String?) -> Bool {
        guard let unwrapped = value else { return false }
        return nil != unwrapped.range(of: #"^\d{9}$"#, options: .regularExpression)
    }
}

private func parseInput(input: String) -> [Passport] {
    return input.components(separatedBy: "\n\n")
        .map { block in block.components(separatedBy: CharacterSet.init(charactersIn: "\n ")) }
        .map { blockEntries -> Passport in
            let pairs = blockEntries.map { entry -> (key: String, value: String) in
                let parsed = entry.components(separatedBy: ":")
                return (parsed[0], parsed[1])
            }
            return Dictionary.init(uniqueKeysWithValues: pairs)
        }
}
