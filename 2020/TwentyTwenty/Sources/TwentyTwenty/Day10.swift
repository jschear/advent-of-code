
struct Day10 {
    let adapters: [Int]
    
    func part1() -> Int {
        let jumpCounts = countJumps()
        return jumpCounts[1, default: 0] * (jumpCounts[3, default: 0] + 1)
    }
    
    private func countJumps() -> Dictionary<Int, Int> {
        var adaptersWithOutlet = adapters
        adaptersWithOutlet.append(0)
        adaptersWithOutlet.sort()
        let consecutivePairs = zip(adaptersWithOutlet, adaptersWithOutlet[1...])
        
        return try! consecutivePairs.reduce(into: [:]) { counts, pair in
            let (x, y) = pair
            let difference = y - x
            if !(1...3 ~= difference) {
                throw AdapterError.missingAdapter("Can't jump from \(x) to \(y)")
            }
            counts[difference, default: 0] += 1
        }
    }
    
    func countAdapterPaths() -> Int {
        let goal = adapters.max()!
        let adapterSet = Set(adapters)
        var knownPathCountsByAdapter = Dictionary<Int, Int>()
        
        func countsPathsFrom(adapter: Int) -> Int {
            if goal == adapter {
                return 1
            }
            if let cachedCount = knownPathCountsByAdapter[adapter] {
                return cachedCount
            }
            let pathCount = [adapter + 1, adapter + 2, adapter + 3]
                .map { candidate in
                    if adapterSet.contains(candidate) {
                        return countsPathsFrom(adapter: candidate)
                    } else {
                        return 0
                    }
                }
                .reduce(0, +)
            
            knownPathCountsByAdapter[adapter] = pathCount
            return pathCount
        }
        
        return countsPathsFrom(adapter: 0)
    }
    
    static func parseInput(input: String) -> [Int] {
        input.split(separator: "\n").compactMap { Int($0) }
    }
}

enum AdapterError: Error {
    case missingAdapter(String)
}
