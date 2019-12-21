package aoc

import aoc.intcode.ChannelInput
import aoc.intcode.ChannelOutput
import aoc.intcode.IntCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.receiveOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private val ROBOT_PROGRAM = listOf(3,8,1005,8,291,1106,0,11,0,0,0,104,1,104,0,3,8,1002,8,-1,10,101,1,10,10,4,10,108,0,8,10,4,10,1002,8,1,28,1,1003,20,10,2,1103,19,10,3,8,1002,8,-1,10,1001,10,1,10,4,10,1008,8,0,10,4,10,1001,8,0,59,1,1004,3,10,3,8,102,-1,8,10,1001,10,1,10,4,10,108,0,8,10,4,10,1001,8,0,84,1006,0,3,1,1102,12,10,3,8,1002,8,-1,10,101,1,10,10,4,10,1008,8,1,10,4,10,101,0,8,114,3,8,1002,8,-1,10,101,1,10,10,4,10,108,1,8,10,4,10,101,0,8,135,3,8,1002,8,-1,10,1001,10,1,10,4,10,1008,8,0,10,4,10,102,1,8,158,2,9,9,10,2,2,10,10,3,8,1002,8,-1,10,1001,10,1,10,4,10,1008,8,1,10,4,10,101,0,8,188,1006,0,56,3,8,1002,8,-1,10,1001,10,1,10,4,10,108,1,8,10,4,10,1001,8,0,212,1006,0,76,2,1005,8,10,3,8,102,-1,8,10,1001,10,1,10,4,10,108,1,8,10,4,10,1001,8,0,241,3,8,102,-1,8,10,101,1,10,10,4,10,1008,8,0,10,4,10,1002,8,1,264,1006,0,95,1,1001,12,10,101,1,9,9,1007,9,933,10,1005,10,15,99,109,613,104,0,104,1,21102,838484206484,1,1,21102,1,308,0,1106,0,412,21102,1,937267929116,1,21101,0,319,0,1105,1,412,3,10,104,0,104,1,3,10,104,0,104,0,3,10,104,0,104,1,3,10,104,0,104,1,3,10,104,0,104,0,3,10,104,0,104,1,21102,206312598619,1,1,21102,366,1,0,1105,1,412,21101,179410332867,0,1,21102,377,1,0,1105,1,412,3,10,104,0,104,0,3,10,104,0,104,0,21101,0,709580595968,1,21102,1,400,0,1106,0,412,21102,868389384552,1,1,21101,411,0,0,1106,0,412,99,109,2,21202,-1,1,1,21102,1,40,2,21102,1,443,3,21101,0,433,0,1106,0,476,109,-2,2105,1,0,0,1,0,0,1,109,2,3,10,204,-1,1001,438,439,454,4,0,1001,438,1,438,108,4,438,10,1006,10,470,1102,0,1,438,109,-2,2106,0,0,0,109,4,1202,-1,1,475,1207,-3,0,10,1006,10,493,21102,0,1,-3,21202,-3,1,1,21201,-2,0,2,21101,0,1,3,21102,1,512,0,1106,0,517,109,-4,2105,1,0,109,5,1207,-3,1,10,1006,10,540,2207,-4,-2,10,1006,10,540,22101,0,-4,-4,1106,0,608,21201,-4,0,1,21201,-3,-1,2,21202,-2,2,3,21101,0,559,0,1106,0,517,21201,1,0,-4,21102,1,1,-1,2207,-4,-2,10,1006,10,578,21101,0,0,-1,22202,-2,-1,-2,2107,0,-3,10,1006,10,600,21201,-1,0,1,21102,600,1,0,106,0,475,21202,-2,-1,-2,22201,-4,-2,-4,109,-5,2106,0,0)

@UseExperimental(ExperimentalCoroutinesApi::class)
class Robot(
    start: Point,
    private val map: Array<IntArray>
) {
    private val inputChannel = Channel<Long>(1)
    private val outputChannel = Channel<Long>()
    private val intCode = IntCode(ROBOT_PROGRAM, ChannelInput(inputChannel), ChannelOutput(outputChannel))

    private var facing: Direction = Direction.UP
    private var location: Point = start
    private var _paintedSquares = mutableSetOf<Point>()

    val paintedSquares: Set<Point>
        get() = _paintedSquares

    fun paint() {
        runBlocking {
            launch { drive() }
            intCode.execute()
        }
    }

    private suspend fun drive() {
        while (true) {
            val input = map[location.y][location.x]
            println("Sending value at $location: $input")
            inputChannel.send(input.toLong())

            val newColor = outputChannel.receiveOrNull()
            if (newColor == null) {
                println("No more output, stopping.")
                break
            }
            println("Received color: $newColor")
            val turnLong = outputChannel.receive()
            println("Received turn: $turnLong")

            paintLocation(newColor)
            turn(turnLong)
            moveOne()

            println()
        }
    }

    // turns:
    // 0 = left
    // 1 = right
    private fun turn(turnLong: Long) {
        val turn = when (turnLong) {
            0L -> Turn.LEFT
            1L -> Turn.RIGHT
            else -> error("Unexpected turn: $turnLong")
        }
        println("Currently facing $facing")
        facing = facing.turn(turn)
        println("Turning $turn, now facing $facing")
    }

    // colors:
    // 0 = black
    // 1 = white
    private fun paintLocation(color: Long) {
        map[location.y][location.x] = color.toInt()
        println("Painting $location with $color")
        _paintedSquares.add(location)
    }

    private fun moveOne() {
        location += when (facing) {
            Direction.RIGHT -> Point(1, 0)
            Direction.UP -> Point(0, -1)
            Direction.LEFT -> Point(-1, 0)
            Direction.DOWN -> Point(0, 1)
        }
        println("Moved to $location")
    }
}

enum class Turn {
    LEFT {
        override fun toString() = "L"
    },
    RIGHT {
        override fun toString() = "R"
    }
}

fun Direction.turn(turn: Turn): Direction = when (this) {
    Direction.RIGHT -> if (turn == Turn.LEFT) Direction.UP else Direction.DOWN
    Direction.UP -> if (turn == Turn.LEFT) Direction.LEFT else Direction.RIGHT
    Direction.LEFT -> if (turn == Turn.LEFT) Direction.DOWN else Direction.UP
    Direction.DOWN -> if (turn == Turn.LEFT) Direction.RIGHT else Direction.LEFT
}

fun numPaintedSquares(): Int {
    val mapSize = 201
    val map = Array(mapSize) { IntArray(mapSize) }
    val center = (mapSize - 1) / 2
    val start = Point(center, center)

    val robot = Robot(start, map)
    robot.paint()
    return robot.paintedSquares.size
}

fun registrationIdentifier(): String {
    val mapSize = 101
    val map = Array(mapSize) { IntArray(mapSize) { 1 } }
    val center = (mapSize - 1) / 2
    val start = Point(center, center)

    val robot = Robot(start, map)
    robot.paint()
    return visualize(map)
}

fun visualize(map: Array<IntArray>): String = map.joinToString("\n") { row ->
    row.joinToString("") { color -> if (color == 0) " " else "*" }
}
