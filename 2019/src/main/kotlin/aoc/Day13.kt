package aoc

import aoc.intcode.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.receiveOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val PADDLE = 3
private const val BALL = 4

// Tiles:
// 0: empty
// 1: wall
// 2: block
// 3: paddle
// 4: ball
@UseExperimental(ExperimentalCoroutinesApi::class)
class ArcadeGame(
    private val grid: Array<IntArray>,
    program: Program
) {
    private val inputChannel = Channel<Long>(1)
    private val outputChannel = Channel<Long>()
    private val intCode = IntCode(program, ChannelInput(inputChannel), ChannelOutput(outputChannel))

    var score: Long = 0
        private set

    fun run() = runBlocking {
        launch { intCode.execute() }
        receiveOutput()
        println("No more output.")
    }

    private suspend fun receiveOutput() {
        var ballX: Int
        var paddleX: Int? = null
        while (true) {
            val x = outputChannel.receiveOrNull()?.toInt() ?: return
            val y = outputChannel.receiveOrNull()?.toInt() ?: return
            val third = outputChannel.receiveOrNull() ?: return

            if (x == -1 && y == 0) {
                score = third
            } else {
                val tile = third.toInt()
                grid[y][x] = tile

                when (tile) {
                    PADDLE -> paddleX = x
                    BALL -> {
                        ballX = x
                        val joystickPosition = when {
                            paddleX == null -> 0L
                            paddleX < ballX -> 1L
                            paddleX > ballX -> -1L
                            else -> 0L
                        }
                        // This assumes that we get one "ball" tile output per "frame"
                        // (Where a "frame" is set the of outputs that update the grid between inputs)
                        inputChannel.send(joystickPosition)
                    }
                }
            }
//            visualize()
        }
    }

    private fun visualize() {
        print(
            grid.joinToString("\n") { row ->
                row.joinToString("") {
                    when (it) {
                        0 -> " "
                        1, 2 -> "\u25A0"
                        3 -> "-"
                        4 -> "*"
                        else -> error("Not a valid value")
                    }
                }
            }
        )
    }
}
