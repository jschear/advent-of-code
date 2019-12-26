package aoc

import java.math.BigInteger
import kotlin.math.abs

data class Vec3(val x: Int, val y: Int, val z: Int) {

    constructor(point: Point, z: Int = 0) : this(point.x, point.y, z)

    operator fun plus(other: Vec3) = Vec3(x + other.x, y + other.y, z + other.z)

    fun pull(other: Vec3) = Vec3(
        pullDimension(other, Vec3::x),
        pullDimension(other, Vec3::y),
        pullDimension(other, Vec3::z)
    )

    private fun pullDimension(other: Vec3, selector: (Vec3) -> Int): Int {
        val a = selector(this)
        val b = selector(other)
        return when {
            a > b -> -1
            a < b -> 1
            else -> 0
        }
    }

    fun sumAbs(): Int = abs(x) + abs(y) + abs(z)

    fun to2d() = Point(x, y)

    companion object {
        val ZERO = Vec3(0, 0, 0)
    }
}

data class Moon(
    val position: Vec3,
    val velocity: Vec3 = Vec3.ZERO
) {
    fun applyGravity(moons: Collection<Moon>): Moon {
        val changeInVelocity = moons
            .filterNot { it == this }
            .map { otherMoon -> position.pull(otherMoon.position) }
            .fold(Vec3.ZERO, Vec3::plus)
        return Moon(position, velocity + changeInVelocity)
    }

    fun applyVelocity() = Moon(position + velocity, velocity)

    fun energy(): Int = position.sumAbs() * velocity.sumAbs()
}

private fun step(moons: List<Moon>) = moons
    .map { it.applyGravity(moons) }
    .map(Moon::applyVelocity)

fun runSimulationEnergy(moons: List<Moon>, steps: Int): Int {
    var currMoons = moons
    (0 until steps).forEach { _ -> currMoons = step(currMoons) }
    return currMoons
        .map(Moon::energy)
        .sum()
}

fun iterationsUntilRepeatedState(moons: List<Moon>): Long {
    val xIters = runForSingleDimension(moons) { it.position.x to it.velocity.x }.toBigInteger()
    val yIters = runForSingleDimension(moons) { it.position.y to it.velocity.y }.toBigInteger()
    val zIters = runForSingleDimension(moons) { it.position.z to it.velocity.z }.toBigInteger()
    return lcm(lcm(xIters, yIters), zIters).toLong()
}

fun lcm(a: BigInteger, b: BigInteger): BigInteger = a.times(b).divide(a.gcd(b))

fun <T> runForSingleDimension(moons: List<Moon>, transform: (Moon) -> T): Long {
    val old = moons.map(transform)
    var currMoons = moons
    var iteration = 0L
    while (true) {
        currMoons = step(currMoons)
        iteration++
        if (old == currMoons.map(transform)) {
            return iteration
        }
    }
}