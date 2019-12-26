package aoc

import aoc.intcode.ChannelInput
import aoc.intcode.ChannelOutput
import aoc.intcode.IntCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.receiveOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

fun day15partOne(): Int {
    val size = 50
    val grid = Array(size) { IntArray(size) { UNEXPLORED } }
    val startPosition = Point(grid.size / 2, grid.size / 2)
    val droid = RepairDroid(grid, startPosition)
    droid.execute()
    return shortestPathLength(grid, startPosition)
}

fun day15partTwo(): Int {
    val size = 50
    val grid = Array(size) { IntArray(size) { UNEXPLORED } }
    val startPosition = Point(grid.size / 2, grid.size / 2)
    val droid = RepairDroid(grid, startPosition)
    droid.execute()
    return minutesToFill(grid)
}

// north (1), south (2), west (3), and east (4)
enum class DroidMove(val id: Long, val pointDiff: Point) {
    NORTH(1, Point(0, 1)) {
        override fun reverse() = SOUTH
    },
    SOUTH(2, Point(0, -1)) {
        override fun reverse() = NORTH
    },
    WEST(3, Point(-1, 0)) {
        override fun reverse() = EAST
    },
    EAST(4, Point(1, 0)) {
        override fun reverse() = WEST
    };

    abstract fun reverse(): DroidMove
}

const val UNEXPLORED = 0
const val EMPTY = 1
const val WALL = 2
const val OXYGEN = 3

val MOVE_RESULTS = mapOf(
    0L to MoveResult.HIT_WALL,
    1L to MoveResult.MOVED,
    2L to MoveResult.REACHED_OXYGEN
)

enum class MoveResult {
    HIT_WALL, MOVED, REACHED_OXYGEN
}

@UseExperimental(ExperimentalCoroutinesApi::class)
class RepairDroid(
    private val grid: Array<IntArray>,
    private val startPosition: Point
) {
    private val inputChannel = Channel<Long>()
    private val outputChannel = Channel<Long>()
    private val intCode: IntCode

    init {
        val program = RepairDroid::class.java.getResource("fifteen.txt")
            .readText()
            .split(",")
            .map(String::toLong)
        intCode = IntCode(program, ChannelInput(inputChannel), ChannelOutput(outputChannel))
    }

    fun execute() = runBlocking {
        val intCodeJob = launch { intCode.execute() }
        io()
        intCodeJob.cancel()
    }

    private suspend fun io() {
        val backtrackStack = mutableListOf<DroidMove>()
        var backtracking = false

        var position = startPosition
        var lastMove = DroidMove.NORTH

        val onSuccessfulMove: (Int) -> Point = { newValue ->
            if (!backtracking) {
                backtrackStack.add(lastMove.reverse())
            }
            (position + lastMove.pointDiff).also {
                grid.putByPoint(it, newValue)
            }
        }

        inputChannel.send(lastMove.id)

        while (true) {
            val output = outputChannel.receiveOrNull() ?: return
            val moveResult = MOVE_RESULTS[output] ?: error("Invalid output")

            log("Tried to move ${lastMove.name}, result: ${moveResult.name}")

            position = when (moveResult) {
                MoveResult.HIT_WALL -> {
                    val wallPosition = position + lastMove.pointDiff
                    grid.putByPoint(wallPosition, WALL)
                    position
                }
                MoveResult.MOVED -> onSuccessfulMove(EMPTY)
                MoveResult.REACHED_OXYGEN -> onSuccessfulMove(OXYGEN)
            }

            val moveToUnexplored = grid.adjacentMoves(position)
                .firstOrNull { (_, value) -> value == UNEXPLORED }
                ?.let { (move, _) -> move }

            // Move to an unexplored space, or backtrack.
            val move = if (moveToUnexplored != null) {
                backtracking = false
                moveToUnexplored.also {
                    log("Moving to unexplored: ${it.name}")
                }
            } else {
                backtracking = true
                if (backtrackStack.isEmpty()) {
                    visualize()
                    return
                } else {
                    backtrackStack.removeLast().also {
                        log("Backtracking: ${it.name}")
                    }
                }
            }

            inputChannel.send(move.id)
            lastMove = move
        }
    }

    private fun visualize() {
        val output = grid.joinToString("\n") { row ->
            row.joinToString("") {
                when (it) {
                    UNEXPLORED -> "?"
                    EMPTY -> " "
                    WALL -> "*"
                    OXYGEN -> "!"
                    else -> error("Unexpected value")
                }
            }
        }
        println(output)
    }
}

private fun log(log: String) {
//    println(log)
}

fun <T> MutableList<T>.removeLast(): T = this.removeAt(size - 1)

private fun Array<IntArray>.getByPoint(point: Point): Int = this[point.y][point.x]

private fun Array<IntArray>.putByPoint(point: Point, value: Int) {
    this[point.y][point.x] = value
}

private fun Array<IntArray>.findByValue(value: Int): Point? {
    for ((y, row) in this.withIndex()) {
        for ((x, candidate) in row.withIndex()) {
            if (candidate == value) {
                return Point(x, y)
            }
        }
    }
    return null
}

private fun Array<IntArray>.adjacentMoves(point: Point): List<Pair<DroidMove, Int>> =
    DroidMove.values()
        .map { move -> move to this.getByPoint(move.pointDiff + point) }

private fun Array<IntArray>.possibleMoves(point: Point): List<Point> =
    DroidMove.values()
        .map { move ->
            val newPoint = move.pointDiff + point
            newPoint to this.getByPoint(newPoint)
        }
        .filter { (_, newValue) -> newValue != WALL }
        .map { (newPoint, _) -> newPoint }

private fun shortestPathLength(grid: Array<IntArray>, start: Point): Int {
    // breadth-first search
    val distances = mutableMapOf(start to 0)
    val visited = mutableSetOf(start)
    val queue = mutableListOf(start)

    while (queue.isNotEmpty()) {
        val point = queue.removeLast()
        val distanceToPoint = distances[point] ?: error("No distance for point $point")

        if (grid.getByPoint(point) == OXYGEN) {
            return distanceToPoint
        }

        for (nextPoint in grid.possibleMoves(point)) {
            if (nextPoint !in visited) {
                distances[nextPoint] = distanceToPoint + 1
                visited.add(nextPoint)
                queue.add(nextPoint)
            }
        }
    }
    return -1
}

// The number of minutes is the maximum number of steps from the oxygen to another point.
// Use BFS to find the shortest distance to each point, then take the max.
private fun minutesToFill(grid: Array<IntArray>): Int {
    val start = grid.findByValue(OXYGEN) ?: error("No oxygen!")

    val distances = mutableMapOf(start to 0)
    val visited = mutableSetOf(start)
    val queue = LinkedList(listOf(start))

    while (queue.isNotEmpty()) {
        val point = queue.removeFirst()
        val distanceToPoint = distances[point] ?: error("No distance for point $point")
        for (nextPoint in grid.possibleMoves(point)) {
            if (nextPoint !in visited) {
                distances[nextPoint] = distanceToPoint + 1
                visited.add(nextPoint)
                queue.add(nextPoint)
            }
        }
    }

    return distances.values.max() ?: error("No values?")
}