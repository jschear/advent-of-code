
private enum TurnError: Error {
    case illegalDegreeValue(Int)
}

enum CompassDirection: Int {
    case north = 0, east = 90, south = 180, west = 270
    
    func move(direction: TurnDirection, degrees: Int) throws -> CompassDirection {
        let rightDegrees = (direction == .right) ? degrees : 360 - degrees
        let newRawValue = (self.rawValue + rightDegrees) % 360
        if let newDirection = CompassDirection(rawValue: newRawValue) {
            return newDirection
        } else {
            throw TurnError.illegalDegreeValue(degrees)
        }
    }
}

enum TurnDirection {
    case left, right
}

enum Action {
    case forward(magnitude: Int)
    case translate(direction: CompassDirection, magnitude: Int)
    case turn(direction: TurnDirection, degrees: Int)
}

private struct Point {
    let x: Int
    let y: Int
    
    func move(direction: CompassDirection, magnitude: Int) -> Point {
        switch direction {
        case .north:
            return Point(x: self.x, y: self.y + magnitude)
        case .south:
            return Point(x: self.x, y: self.y - magnitude)
        case .east:
            return Point(x: self.x + magnitude, y: self.y)
        case .west:
            return Point(x: self.x - magnitude, y: self.y)
        }
    }
    
    func rotate(direction: TurnDirection, degrees: Int) throws -> Point {
        let rightDegrees = (direction == .right) ? degrees : 360 - degrees
        switch rightDegrees {
        case 0:
            return self
        case 90:
            return Point(x: self.y, y: -self.x)
        case 180:
            return Point(x: -self.x, y: -self.y)
        case 270:
            return Point(x: -self.y, y: self.x)
        default:
            throw TurnError.illegalDegreeValue(degrees)
        }
    }
}

private struct ShipPosition {
    let bearing: CompassDirection
    let position: Point
    
    func take(action: Action) -> ShipPosition {
        switch action {
        case let .forward(magnitude):
            return ShipPosition(
                bearing: bearing,
                position: position.move(direction: bearing, magnitude: magnitude)
            )
        case let .translate(direction, magnitude):
            return ShipPosition(
                bearing: bearing,
                position: position.move(direction: direction, magnitude: magnitude)
            )
        case let .turn(direction, degrees):
            return ShipPosition(
                bearing: try! bearing.move(direction: direction, degrees: degrees),
                position: position
            )
        }
    }
}

private struct WaypointShip {
    let shipPosition: Point
    let waypointVector: Point
    
    func take(action: Action) -> WaypointShip {
        switch action {
        case let .forward(magnitude):
            let newPosition = Point(
                x: shipPosition.x + waypointVector.x * magnitude,
                y: shipPosition.y + waypointVector.y * magnitude
            )
            return WaypointShip(shipPosition: newPosition, waypointVector: waypointVector)
        case let .translate(direction, magnitude):
            return WaypointShip(
                shipPosition: shipPosition,
                waypointVector: waypointVector.move(direction: direction, magnitude: magnitude)
            )
        case let .turn(direction, degrees):
            return WaypointShip(
                shipPosition: shipPosition,
                waypointVector: try! waypointVector.rotate(direction: direction, degrees: degrees)
            )
        }
    }
}

struct Day12 {
    enum ParseError: Error {
        case invalidLine(String)
    }
    
    static func parseInput(input: String) -> [Action] {
        try! input.split(separator: "\n").map { line in
            let number = Int(line.dropFirst(1))!
            switch line.prefix(1) {
            case "N":
                return .translate(direction: .north, magnitude: number)
            case "S":
                return .translate(direction: .south, magnitude: number)
            case "E":
                return .translate(direction: .east, magnitude: number)
            case "W":
                return .translate(direction: .west, magnitude: number)
            case "L":
                return .turn(direction: .left, degrees: number)
            case "R":
                return .turn(direction: .right, degrees: number)
            case "F":
                return .forward(magnitude: number)
            default:
                throw ParseError.invalidLine(String(line))
            }
        }
    }
    
    class Part1 {
        static func manhattanDistanceAfterActions(actions: [Action]) -> Int {
            var currPosition = ShipPosition(bearing: .east, position: Point(x: 0, y: 0))
            for action in actions {
                currPosition = currPosition.take(action: action)
            }
            return abs(currPosition.position.x) + abs(currPosition.position.y)
        }
    }
    
    class Part2 {
        static func manhattanDistanceAfterActions(actions: [Action]) -> Int {
            var currPosition = WaypointShip(
                shipPosition: Point(x: 0, y: 0),
                waypointVector: Point(x: 10, y: 1)
            )
            for action in actions {
                currPosition = currPosition.take(action: action)
            }
            return abs(currPosition.shipPosition.x) + abs(currPosition.shipPosition.y)
        }
    }
}
