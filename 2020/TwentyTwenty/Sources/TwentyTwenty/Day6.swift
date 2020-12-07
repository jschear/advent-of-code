
import Foundation

func day6part1(input: String) -> Int {
    return sumOfCounts(input: input) { $0.union($1) }
}

func day6part2(input: String) -> Int {
    return sumOfCounts(input: input) { $0.intersection($1) }
}

private func sumOfCounts(input: String, combineSets: (Set<Character>, Set<Character>) -> Set<Character>) -> Int {
    return input.components(separatedBy: "\n\n")
        .map { groupStrings in
            // Create a [Set<Character>] for each group
            groupStrings.components(separatedBy: "\n").map(Set.init)
        }
        .map { (group: [Set<Character>]) -> Set<Character> in
            // Reduce each group to a single Set<Character>
            let first = group.first!
            let rest = group.dropFirst()
            return rest.reduce(first, combineSets)
        }
        .map { $0.count }
        .reduce(0, +)
}
