package aoc

import kotlin.math.atan2


fun mapToPoints(input: String): Set<Point> = input.split("\n")
    .withIndex()
    .flatMap { (row, line) ->
        line.toCharArray()
            .withIndex()
            .filter { (_, char) -> char == '#' }
            .map(IndexedValue<Char>::index)
            .map { col -> Point(col, row) }
    }
    .toSet()

fun test() {
    val points = mapToPoints(MAP)

}

fun angleBetween(a: Point, b: Point): Double {
    // tangent(angle) = opposite / adjacent
    // angle = arctan(opposite / adjacent)
    // atan2 has special cases based on the sign on x and y, because normal arctan (which operates on a single number)
    // doesn't differentiate between rotations of 180 degrees
    val dx = b.x - a.x
    val dy = b.y - a.y
    return atan2(dy.toDouble(), dx.toDouble())
}

val MAP = """
    .###.#...#.#.##.#.####..
    .#....#####...#.######..
    #.#.###.###.#.....#.####
    ##.###..##..####.#.####.
    ###########.#######.##.#
    ##########.#########.##.
    .#.##.########.##...###.
    ###.#.##.#####.#.###.###
    ##.#####.##..###.#.##.#.
    .#.#.#####.####.#..#####
    .###.#####.#..#..##.#.##
    ########.##.#...########
    .####..##..#.###.###.#.#
    ....######.##.#.######.#
    ###.####.######.#....###
    ############.#.#.##.####
    ##...##..####.####.#..##
    .###.#########.###..#.##
    #.##.#.#...##...#####..#
    ##.#..###############.##
    ##.###.#####.##.######..
    ##.#####.#.#.##..#######
    ...#######.######...####
    #....#.#.#.####.#.#.#.##
""".trimIndent()