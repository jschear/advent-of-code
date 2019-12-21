package aoc

import aoc.intcode.ChannelInput
import aoc.intcode.ChannelOutput
import aoc.intcode.IntCode
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AftScaffoldingControlAndInformationInterface(
    private val asciiProgram: String? = null
) {
    private val intCode: IntCode
    private val inputChannel = Channel<Long>()
    private val outputChannel = Channel<Long>()

    init {
        var program = AftScaffoldingControlAndInformationInterface::class.java.getResource("seventeen.txt")
            .readText()
            .split(",")
            .map(String::toLong)

        if (asciiProgram != null) {
            // Wake up!
            program = program.toMutableList().also { it[0] = 2 }
        }

        intCode = IntCode(program, ChannelInput(inputChannel), ChannelOutput(outputChannel))
    }

    fun scaffolding(): Pair<Long, Array<LongArray>> = runBlocking {
        launch { intCode.execute() }

        if (asciiProgram != null) {
            launch {
                asciiProgram.toInput().forEach {
                    inputChannel.send(it)
                }
            }
        }

        val output = outputChannel.toList()
        val rowLength = output.indexOf(10L) + 1
        val dustCollected = output.last()

        val scaffolding = output.dropLast(1)
            .chunked(rowLength)
            .map { it.dropLast(1) }
            .map(List<Long>::toLongArray)
            .toTypedArray()

        dustCollected to scaffolding
    }
}

fun day17partOne(): Int {
    val ascii = AftScaffoldingControlAndInformationInterface()
    val (_, scaffolding) = ascii.scaffolding()
    println(scaffolding.visualize())
    return scaffolding.findIntersections()
        .map(Point::alignmentParameter)
        .sum()
}

fun day17partTwo() {
    val noInputAscii = AftScaffoldingControlAndInformationInterface()
    val (_, scaffolding) = noInputAscii.scaffolding()

    val fullPath = scaffolding.findSteps()
    val moveFns = fullPath.toMovementFunctions()
    println(moveFns.joinToString(transform = MoveFn::toString))

    // Solved this by hand -- hopefully I'll come back and do this programmatically :)
    val asciiProgram = """
        C,A,C,A,B,C,B,C,B,A
        L,8,R,6,L,6
        L,8,L,4,R,12,L,6,L,4
        R,12,L,8,L,4,L,4
        n
        
    """.trimIndent()
    val ascii = AftScaffoldingControlAndInformationInterface(asciiProgram)
    val (dustCollected, _) = ascii.scaffolding()
    println(dustCollected)
}

fun Array<LongArray>.visualize(): String = joinToString("\n") {
    it.joinToString("") { long -> long.toChar().toString() }
}

fun Array<LongArray>.getByPoint(point: Point): Long = this[point.y][point.x]

fun Array<LongArray>.points(): List<Point> = indices.flatMap { row ->
    this[0].indices.map { col -> Point(col, row) }
}

fun Array<LongArray>.inRange(point: Point): Boolean {
    return point.x in this[0].indices && point.y in indices
}

fun Array<LongArray>.isOnScaffolding(point: Point): Boolean = inRange(point) && getByPoint(point).toChar() == '#'

fun Array<LongArray>.findIntersections(): List<Point> = points()
    .filter { point ->
        isOnScaffolding(point) && point.adjacents().all { isOnScaffolding(it) }
    }

private fun Array<LongArray>.findByValue(value: Long): Point? {
    for ((y, row) in this.withIndex()) {
        for ((x, candidate) in row.withIndex()) {
            if (candidate == value) {
                return Point(x, y)
            }
        }
    }
    return null
}

fun Array<LongArray>.findSteps(): List<Step> {
    val startPoint = listOf('^' to Direction.UP, '<' to Direction.LEFT, '>' to Direction.RIGHT, 'v' to Direction.DOWN)
        .asSequence()
        .mapNotNull { (robot, direction) -> findByValue(robot.toLong())?.let { Position(it, direction)} }
        .first()

    val moves = mutableListOf<Step>()
    var currPosition: Position = startPoint

    do {
        val movePosition = currPosition.move()
        val movePoint = movePosition.point

        val (step, nextPosition) =
            if (isOnScaffolding(movePoint)) {
                Step.Moved to movePosition
            } else {
                val validTurns = Turn.values()
                    .map { turn -> turn to currPosition.turn(turn) }
                    .filter { (_, turnPosition) -> isOnScaffolding(turnPosition.move().point) }
                when {
                    validTurns.isEmpty() -> null to currPosition
                    validTurns.size == 1 -> {
                        val (turn, turnPosition) = validTurns[0]
                        Step.Turned(turn) to turnPosition
                    }
                    else -> error("Could turn in either direction")
                }
            }

        if (step != null) {
            moves.add(step)
        }
        currPosition = nextPosition
    } while (step != null)

    return moves
}

fun Point.adjacents(): List<Point> = listOf(
    this + Point(0, 1),
    this + Point(0, -1),
    this + Point(-1, 0),
    this + Point(1, 0)
)

fun Point.alignmentParameter(): Int = x * y

fun String.toInput(): List<Long> = map(Char::toLong)

data class Position(val point: Point, val facing: Direction) {
    fun move(): Position {
        val newPoint = point + when (facing) {
            Direction.RIGHT -> Point(1, 0)
            Direction.UP -> Point(0, -1)
            Direction.LEFT -> Point(-1, 0)
            Direction.DOWN -> Point(0, 1)
        }
        return Position(newPoint, facing)
    }

    fun turn(turn: Turn) = Position(point, facing.turn(turn))
}

sealed class Step {
    class Turned(val turn: Turn) : Step() {
        override fun toString() = turn.toString()
    }
    object Moved : Step() {
        override fun toString() = "M"
    }
}

sealed class MoveFn {
    class Turned(val turn: Turn) : MoveFn() {
        override fun toString() = turn.toString()
    }
    class Moved(val spaces: Int) : MoveFn() {
        override fun toString() = spaces.toString()
    }
}

fun List<Step>.toMovementFunctions(): List<MoveFn> {
    val movementFunctions = mutableListOf<MoveFn>()
    var moveCount = 0
    for (item in this) {
        when (item) {
            is Step.Moved -> moveCount++
            is Step.Turned -> {
                if (moveCount > 0) {
                    movementFunctions.add(MoveFn.Moved(moveCount))
                }
                moveCount = 0
                movementFunctions.add(MoveFn.Turned(item.turn))
            }
        }
    }
    if (moveCount != 0) {
        movementFunctions.add(MoveFn.Moved(moveCount))
    }
    return movementFunctions
}
