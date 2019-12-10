package aoc

import kotlin.math.abs

enum class Direction {
    RIGHT, UP, LEFT, DOWN
}

data class Move(val direction: Direction, val value: Int)

data class Point(val x: Int, val y: Int)

operator fun Point.plus(other: Point): Point = Point(x + other.x, y + other.y)

fun moves(input: String): List<Move> = input
    .split(",")
    .map {
        val stringDir = it.substring(0, 1)
        val value = it.substring(1).toInt()

        when (stringDir) {
            "R" -> Move(Direction.RIGHT, value)
            "U" -> Move(Direction.UP, value)
            "L" -> Move(Direction.LEFT, value)
            "D" -> Move(Direction.DOWN, value)
            else -> throw IllegalStateException("Unrecognized direction")
        }
    }

fun Point.pathAlong(move: Move): List<Point> =
    (1..move.value).map {
        when (move.direction) {
            Direction.RIGHT -> Point(it, 0)
            Direction.UP -> Point(0, it)
            Direction.LEFT -> Point(-it, 0)
            Direction.DOWN -> Point(0, -it)
        }
    }.map { translation -> this + translation }

fun movesToPath(moves: List<Move>): List<Point> {
    val fullPath = mutableListOf<Point>()

    var curPoint = Point(0, 0)
    moves.forEach { move ->
        val path = curPoint.pathAlong(move)
        fullPath.addAll(path)
        curPoint = path.last()
    }
    return fullPath
}

fun manhattanDistanceToIntersection(one: String, two: String): Int {
    val pathOne = movesToPath(moves(one))
    val pathTwo = movesToPath(moves(two))

    val intersections = pathOne.intersect(pathTwo)
    val distances = intersections.map { (x, y)  -> abs(x) + abs(y) }
    return distances.min()!!
}

fun minimumCombinedSteps(one: String, two: String): Int {
    val pathOne = movesToPath(moves(one))
    val distanceByPointOne = distanceByPoint(pathOne)

    val pathTwo = movesToPath(moves(two))
    val distanceByPointTwo = distanceByPoint(pathTwo)

    val intersections = pathOne.intersect(pathTwo)
    val distances = intersections.map { point ->
        val distOne = distanceByPointOne[point] ?: error("Intersection not found")
        val distTwo = distanceByPointTwo[point] ?: error("Intersection not found")
        distOne + distTwo
    }
    return distances.min()!!
}

private fun distanceByPoint(path: List<Point>): Map<Point, Int> {
    val distanceByPoint = mutableMapOf<Point, Int>()
    // This could maybe be associateWith, but we need to take the minimum distance to each point :thinking:
    for ((distance, point) in path.withIndex()) {
        val trueDistance = distance + 1
        val existingDistance = distanceByPoint[point]
        if (existingDistance == null || existingDistance > trueDistance) {
            distanceByPoint[point] = trueDistance
        }
    }
    return distanceByPoint
}
