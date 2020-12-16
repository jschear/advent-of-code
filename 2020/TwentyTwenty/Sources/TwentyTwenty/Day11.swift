
enum Space {
    case floor
    case empty
    case occupied
}

private func +(lhs: Point, rhs: Point) -> Point {
    return Point(x: lhs.x + rhs.x, y: lhs.y + rhs.y)
}

private let slopes = [
    Point(x: -1, y: -1),
    Point(x: -1, y: 0),
    Point(x: -1, y: 1),
    Point(x: 0, y: -1),
    Point(x: 0, y: 1),
    Point(x: 1, y: -1),
    Point(x: 1, y: 0),
    Point(x: 1, y: 1)
]

private struct Point {
    let x: Int
    let y: Int
    
    func neighbors() -> [Point] {
        return slopes.map { self + $0 }
    }
}

struct Day11 {
    enum ParseError: Error {
        case invalidCharacter(Character)
    }

    static func parseInput(input: String) -> [[Space]] {
        input.split(separator: "\n").map { line in
            try! line.map {
                switch $0 {
                case ".":
                    return Space.floor
                case "L":
                    return Space.empty
                case "#":
                    return Space.occupied
                default:
                    throw ParseError.invalidCharacter($0)
                }
            }
        }
    }
    
    static func occupiedAtFixedPoint(initialDeck: [[Space]], nextDeck: ([[Space]]) -> [[Space]]) -> Int {
        var currDeck = [[Space]]()
        var newDeck = initialDeck
        while currDeck != newDeck {
            currDeck = newDeck
            newDeck = nextDeck(currDeck)
        }
        return currDeck.reduce(0) { (acc: Int, row: [Space]) -> Int in
            acc + row.filter { $0 == Space.occupied }.count
        }
    }
    
    private static func lookup(deck: [[Space]], point: Point) -> Space? {
        if !deck.inRange(point: point) {
            return nil
        }
        return deck[point.y][point.x]
    }
    
    class Part1 {
        static func nextDeck(deck: [[Space]]) -> [[Space]] {
            var newDeck = deck
            for y in deck.indices {
                for x in deck[0].indices {
                    switch deck[y][x] {
                    case .empty:
                        let occupied = occupiedNeighbors(deck: deck, point: Point(x: x, y: y))
                        if occupied == 0 {
                            newDeck[y][x] = .occupied
                        }
                    case .occupied:
                        let occupied = occupiedNeighbors(deck: deck, point: Point(x: x, y: y))
                        if occupied >= 4 {
                            newDeck[y][x] = .empty
                        }
                    case .floor:
                        continue
                    }
                }
            }
            return newDeck
        }
        
        private static func occupiedNeighbors(deck: [[Space]], point: Point) -> Int {
            return point.neighbors()
                .compactMap { lookup(deck: deck, point: $0) }
                .filter { $0 == Space.occupied }
                .count
        }
    }
    
    class Part2 {
        static func nextDeck(deck: [[Space]]) -> [[Space]] {
            var newDeck = deck
            for y in deck.indices {
                for x in deck[0].indices {
                    switch deck[y][x] {
                    case .empty:
                        let occupied = occupiedVisibleChairs(deck: deck, point: Point(x: x, y: y))
                        if occupied == 0 {
                            newDeck[y][x] = .occupied
                        }
                    case .occupied:
                        let occupied = occupiedVisibleChairs(deck: deck, point: Point(x: x, y: y))
                        if occupied >= 5 {
                            newDeck[y][x] = .empty
                        }
                    case .floor:
                        continue
                    }
                }
            }
            return newDeck
        }
        
        private static func occupiedVisibleChairs(deck: [[Space]], point: Point) -> Int {
            return slopes.reduce(0) { acc, slope in
                var nextPoint = point + slope
                while deck.inRange(point: nextPoint) {
                    switch deck[nextPoint.y][nextPoint.x] {
                    case .occupied:
                        return acc + 1
                    case .floor:
                        nextPoint = nextPoint + slope
                    case .empty:
                        return acc
                    }
                }
                return acc
            }
        }
    }
}

extension Array where Element == Array<Space> {
    func asString() -> String {
        self.map {
            $0.map {
                switch $0 {
                case .empty:
                    return "L"
                case .floor:
                    return "."
                case .occupied:
                    return "#"
                }
            }.joined()
        }.joined(separator: "\n")
    }
    
    fileprivate func inRange(point: Point) -> Bool {
        return self.startIndex..<self.endIndex ~= point.y && self[0].startIndex..<self[0].endIndex ~= point.x
    }
}
