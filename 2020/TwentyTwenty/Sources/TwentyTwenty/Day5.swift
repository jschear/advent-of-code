
import Foundation

enum Partition {
    case low
    case high
    
    static func from(character: Character) throws -> Partition {
        switch character {
        case "B", "R":
            return high
        case "F", "L":
            return low
        default:
            throw ParseError.invalidCharacter(character)
        }
    }
}

enum ParseError: Error {
    case invalidCharacter(Character)
}

struct Ticket {
    let rowPartitions: [Partition]
    let columnPartitions: [Partition]
    
    static func create<T: StringProtocol>(from string: T) -> Ticket {
        let chars = Array(string)
        let parseChar = { (char: Character) -> Partition in try! Partition.from(character: char) }
        return Ticket(
            rowPartitions: chars[0..<7].map(parseChar),
            columnPartitions: chars[7..<10].map(parseChar)
        )
    }
}

struct Seat: Equatable {
    let row: Int
    let col: Int
    
    func id() -> Int {
        return row * 8 + col
    }
}

func locateSeat(ticket: Ticket) -> Seat {
    let rowRange = search(partitions: ticket.rowPartitions, range: 0...127)
    let colRange = search(partitions: ticket.columnPartitions, range: 0...7)
    return Seat(row: rowRange.lowerBound, col: colRange.lowerBound)
}

func search<T: Collection>(partitions: T, range: ClosedRange<Int>) -> ClosedRange<Int> where T.Element == Partition {
    guard let partition = partitions.first else { return range }
    let rest = partitions.dropFirst()
    let mid = (range.count / 2) + range.lowerBound
    switch partition {
    case .high:
        return search(partitions: rest, range: mid...range.upperBound)
    case .low:
        return search(partitions: rest, range: range.lowerBound...mid)
    }
}

func day5part1(input: String) -> Int {
    let tickets = input.split(separator: "\n").map(Ticket.create)
    let seats = tickets.map(locateSeat)
    return seats.map { $0.id() }.max()!
}

func day5part2(input: String) -> Set<Int> {
    let seatIds = input.split(separator: "\n")
        .map(Ticket.create)
        .map(locateSeat)
        .map { $0.id() }
    
    let min = seatIds.min()!
    let max = seatIds.max()!
    return Set(min...max).subtracting(seatIds)
}
