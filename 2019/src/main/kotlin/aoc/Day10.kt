package aoc

import kotlin.math.PI
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

fun angleBetween(a: Point, b: Point): Double {
    // tangent(angle) = opposite / adjacent
    // angle = arctan(opposite / adjacent)
    // atan2 has special cases based on the sign on x and y, because normal arctan (which operates on a single number)
    // doesn't differentiate between rotations of 180 degrees
    val dx = b.x - a.x
    val dy = b.y - a.y
    return atan2(dy.toDouble(), dx.toDouble())
}

// down  = atan2(1.0, 0.0)  = pi / 2
// right = atan2(0.0, 1.0)  = 0.0
// up    = atan2(-1.0, 0.0) = -pi / 2
// left  = atan2(0.0, -1.0) = pi
// We want to start with "up", so we need to add (pi / 2) radians, and convert to positive angles
private fun Double.rotate(): Double {
    val newAngle = this + (PI / 2)
    return if (newAngle < 0) {
        newAngle + (2 * PI)
    } else {
        newAngle
    }
}

fun maxNumVisibleAsteroids(input: String): Int {
    val asteroids = mapToPoints(input)
    val numVisible = asteroids.map { station ->
        asteroids.distinctBy { asteroid -> angleBetween(station, asteroid) }.count()
    }
    return numVisible.max()!!
}

fun twoHundredthVaporized(input: String): Int {
    val asteroids = mapToPoints(input)

    val station = asteroids.maxBy { candidate ->
        asteroids
            .distinctBy { asteroid -> angleBetween(candidate, asteroid) }
            .count()
    }!!

    val others = asteroids.toMutableSet().apply { remove(station) }

    val scanOrderedPoints = others.groupBy { angleBetween(station, it).rotate() }
        .mapValues { (_, points) ->
            // Sort all asteroids at the same angle by distance
            points.sortedBy(station::distanceTo)
        }
        .flatMap { (angle, points) ->
            // distanceRank is the index of the scan in which the asteroid will be destroyed
            points.withIndex().map { (distanceRank, point) -> RankedPoint(distanceRank, angle, point) }
        }
        .sorted()
        .map(RankedPoint::point)

    val twoHundredth = scanOrderedPoints[199]
    return 100 * twoHundredth.x + twoHundredth.y
}

data class RankedPoint(
    val distanceRank: Int,
    val angle: Double,
    val point: Point
) : Comparable<RankedPoint> {

    override fun compareTo(other: RankedPoint): Int = COMPARATOR.compare(this, other)

    companion object {
        private val COMPARATOR = Comparator.comparingInt(RankedPoint::distanceRank)
            .thenComparing(Comparator.comparingDouble(RankedPoint::angle))
    }
}
