
struct Point: Hashable {
    let x: Int
    let y: Int
    
    func move(by: Point, maxX: Int) -> Point {
        Point(
            x: (self.x + by.x) % maxX,
            y: self.y + by.y
        )
    }
}

func day3part1(input: String) -> Int {
    let (dimensions, trees) = parseInput(input: input)
    let path = steps(slope: Point(x: 3, y: 1), dimensions: dimensions)
    let hits = trees.intersection(path)
    return hits.count
}

func day3part2(input: String) -> Int {
    let (dimensions, trees) = parseInput(input: input)
    let slopes = [
        Point(x: 1, y: 1),
        Point(x: 3, y: 1),
        Point(x: 5, y: 1),
        Point(x: 7, y: 1),
        Point(x: 1, y: 2)
    ]
    return slopes
        .map { slope in steps(slope: slope, dimensions: dimensions) }
        .map { steps in trees.intersection(steps).count }
        .reduce(1, *)
}

private func steps(slope: Point, dimensions: Point) -> [Point] {
    var steps = [Point]()
    var curr = Point(x: 0, y: 0)
    while curr.y < dimensions.y {
        steps.append(curr)
        curr = curr.move(by: slope, maxX: dimensions.x)
    }
    return steps
}

private func parseInput(input: String) -> (dimensions: Point, trees: Set<Point>) {
    let lines = input.split(separator: "\n")
    let dimensions = Point(x: lines.first!.count, y: lines.count)
    let points = lines.enumerated().flatMap { y, line in
        line.enumerated().compactMap { x, character in
            character == "#" ? Point(x: x, y: y) : nil
        }
    }
    return (dimensions, Set(points))
}
