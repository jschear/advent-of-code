
func day1part1(input: String) -> Int? {
    let elements = parseInput(input: input)
    for i in 0..<elements.count {
        for j in i..<elements.count {
            if elements[i] + elements[j] == 2020 {
                return elements[i] * elements[j]
            }
        }
    }
    return nil
}

func day1part2(input: String) -> Int? {
    let elements = parseInput(input: input)
    for i in 0..<elements.count {
        for j in i..<elements.count {
            for k in j..<elements.count {
                if elements[i] + elements[j] + elements[k] == 2020 {
                    return elements[i] * elements[j] * elements[k]
                }
            }
        }
    }
    return nil
}

private func parseInput(input: String) -> [Int] {
    return input.split(separator: "\n").map { Int($0)! }
}

// More fun but more complicated and slower.
func day1part1Combinations(input: String) -> Int? {
    return solve(input: input, combinationsOf: 2)
}

func day1part2Combinations(input: String) -> Int? {
    return solve(input: input, combinationsOf: 3)
}

private func solve(input: String, combinationsOf k: Int) -> Int? {
    let values = parseInput(input: input)
    let combos = combinations(elements: ArraySlice.init(values), k: k)
    let solution = combos.first { 2020 == $0.reduce(0, +) }
    return solution?.reduce(1, *)
}

private func combinations<T>(elements: ArraySlice<T>, k: Int) -> ArraySlice<ArraySlice<T>> {
    guard k > 0 else { return [[]] }
    guard !elements.isEmpty else { return [] }
    let first = elements.first!
    let rest = elements.dropFirst()
    let combinationsWithFirst = combinations(elements: rest, k: k - 1).map { $0 + [first] }
    let combinationsWithoutFirst = combinations(elements: rest, k: k)
    return combinationsWithFirst + combinationsWithoutFirst
}
