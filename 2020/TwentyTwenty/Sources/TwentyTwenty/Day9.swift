
struct Day9 {
    let windowSize: Int
    let data: [Int]
    
    func findFirstError() -> Int? {
        if let errorIndex = findFirstErrorIndex() {
            return data[errorIndex]
        }
        return nil
    }
    
    private func findFirstErrorIndex() -> Int? {
        return (windowSize..<data.count).first { targetIndex in
            let target = data[targetIndex]
            let windowStartIndex = targetIndex - windowSize
            let window = data[windowStartIndex..<targetIndex]
            return !hasTwoNumbersThatSumTo(target: target, window: window)
        }
    }
    
    private func hasTwoNumbersThatSumTo(target: Int, window: ArraySlice<Int>) -> Bool {
        (window.startIndex..<window.endIndex).contains { index in
            let number = window[index]
            var rest = window
            rest.remove(at: index)
            return rest.contains(target - number)
        }
    }
    
    func findWeakness() -> Int? {
        guard let errorIndex = findFirstErrorIndex() else { return nil }
        let error = data[errorIndex]
        guard let slice = findWeaknessSlice(error: error) else { return nil }
        return slice.min()! + slice.max()!
    }
    
    private func findWeaknessSlice(error: Int) -> ArraySlice<Int>? {
        // n^2 I guess~~
        for i in data.indices {
            for j in (i + 1)..<data.endIndex {
                let slice = data[i...j]
                let sum = slice.reduce(0, +)
                if sum == error {
                    return slice
                } else if sum > error {
                    break
                }
            }
        }
        return nil
    }
    
    static func parseInput(input: String) -> [Int] {
        input.split(separator: "\n").compactMap { Int.init($0) }
    }
}

