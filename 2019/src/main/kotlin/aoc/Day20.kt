package aoc

import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import java.util.*

class Graph(
    val edges: Multimap<Point, Point>,
    val outerPortals: Map<Point, Point>,
    val innerPortals: Map<Point, Point>
)

class Input(
    val graph: Graph,
    val start: Vec3,
    val goal: Vec3,
    val grid: List<List<Char>>,
    val withRecursion: Boolean
)

fun padMaze(maze: String): List<List<Char>> {
    val grid = maze.lines().map { it.toList().pad(2, ' ') }
    val padding = List(grid[0].size) { ' ' }
    return grid.pad(2, padding)
}

fun <T> List<T>.pad(times: Int, pad: T): List<T> {
    return toMutableList().apply {
        repeat(times) {
            add(0, pad)
            add(pad)
        }
    }
}

private enum class MazeEdge { OUTER, INNER }
private data class PortalEntrance(val point: Point, val edge: MazeEdge)

fun parseDonutMaze(maze: String, withRecursion: Boolean = false): Input {
    val grid = padMaze(maze)

    val edges: Multimap<Point, Point> = MultimapBuilder.hashKeys().hashSetValues().build()
    val portals: Multimap<String, PortalEntrance> = MultimapBuilder.hashKeys().hashSetValues().build()
    var start: Point? = null
    var goal: Point? = null

    for ((y, row) in grid.withIndex()) {
        for ((x, character) in row.withIndex()) {
            val point = Point(x, y)
            when (character) {
                '.' -> point.adjacents().filter { grid.atPoint(it) == '.' }.forEach { edges.put(point, it) }
                in 'A'..'Z' -> {
                    Direction.values()
                        .map { direction ->
                            direction to point.pointsInDirection(direction)
                                .take(2)
                                .map { it to grid.atPoint(it) }
                                .toList()
                        }
                        .singleOrNull { (_, pointVals) -> pointVals[0].second in 'A'..'Z' && pointVals[1].second == '.' }
                        ?.let { (direction, pointVals) ->
                            val (firstPointVal, secondPointVal) = pointVals

                            if (character == 'A' && firstPointVal.second == 'A') {
                                start = secondPointVal.first
                            } else if (character == 'Z' && firstPointVal.second == 'Z') {
                                goal = secondPointVal.first
                            } else {
                                val portalName = portalName(direction, grid.atPoint(point), firstPointVal.second)
                                val edge = getEdge(direction, point, grid[0].size / 2)
                                portals.put(portalName, PortalEntrance(secondPointVal.first, edge))
                            }
                        }
                }
            }
        }
    }
    // Turn portals into edges
    println(portals)
    val innerPortals = mutableMapOf<Point, Point>()
    val outerPortals = mutableMapOf<Point, Point>()
    for (portal in portals.keySet()) {
        val portalEntrances = portals.get(portal).toList()
        require(portalEntrances.size == 2) { "Portal without two sides!" }
        val (first, second) = portalEntrances
        addPortal(first, second, innerPortals, outerPortals)
        addPortal(second, first, innerPortals, outerPortals)
    }
    val vec3Start = Vec3(requireNotNull(start) { "No start!" })
    val vec3Goal = Vec3(requireNotNull(goal) { "No goal!" })
    return Input(Graph(edges, outerPortals, innerPortals), vec3Start, vec3Goal, grid, withRecursion)
}

private fun addPortal(
    start: PortalEntrance, end: PortalEntrance, innerPortals: MutableMap<Point, Point>, outerPortals: MutableMap<Point, Point>
) {
    when (start.edge) {
        MazeEdge.OUTER -> outerPortals[start.point] = end.point
        MazeEdge.INNER -> innerPortals[start.point] = end.point
    }
}

private fun getEdge(direction: Direction, point: Point, midway: Int): MazeEdge = when (direction) {
    Direction.DOWN -> if (point.y < midway) MazeEdge.OUTER else MazeEdge.INNER
    Direction.RIGHT -> if (point.x < midway) MazeEdge.OUTER else MazeEdge.INNER
    Direction.LEFT -> if (point.x > midway) MazeEdge.OUTER else MazeEdge.INNER
    Direction.UP -> if (point.y > midway) MazeEdge.OUTER else MazeEdge.INNER
}

fun portalName(direction: Direction, firstChar: Char, secondChar: Char): String = when (direction) {
    Direction.RIGHT, Direction.DOWN -> "$firstChar$secondChar"
    Direction.UP, Direction.LEFT -> "$secondChar$firstChar"
}

fun Point.moveIn(direction: Direction): Point = this + when (direction) {
    Direction.RIGHT -> Point(1, 0)
    Direction.UP -> Point(0, -1)
    Direction.LEFT -> Point(-1, 0)
    Direction.DOWN -> Point(0, 1)
}

fun Point.pointsInDirection(direction: Direction): Sequence<Point> =
    generateSequence(moveIn(direction)) { it.moveIn(direction) }

fun <T> List<List<T>>.atPoint(point: Point): T = this[point.y][point.x]

// BFS again, why not
fun Input.shortestPath(): Int {
    val distances = mutableMapOf(start to 0)
    val visited = mutableSetOf(start)
    val queue = LinkedList(listOf(start))

    while (queue.isNotEmpty()) {
        val node = queue.removeFirst()
        val distanceToPoint = distances[node] ?: error("No distance for point $node")

        if (node == goal) {
            return distanceToPoint
        }

        for (nextNode in nextNodes(node)) {
            if (nextNode !in visited) {
                distances[nextNode] = distanceToPoint + 1
                visited.add(nextNode)
                queue.add(nextNode)
            }
        }
//        visualize(grid, visited)
    }
    return -1
}

fun Input.nextNodes(node: Vec3): List<Vec3> {
    val point = node.to2d()
    val nextVec3s = graph.edges[point].map { Vec3(it, node.z) }.toMutableList()

    if (node.z != 0 || !withRecursion) {
        graph.outerPortals[point]?.let { nextVec3s.add(Vec3(it, node.z - 1)) }
    }
    graph.innerPortals[point]?.let { nextVec3s.add(Vec3(it, node.z + 1)) }

    return if (!withRecursion) {
        nextVec3s.map { Vec3(it.x, it.y, 0) }
    } else {
        nextVec3s
    }
}

fun visualize(grid: List<List<Char>>, visited: Set<Vec3>) {
    val mutableGrid = grid.map { it.toMutableList() }.toMutableList()
    for (node in visited) {
        mutableGrid[node.y][node.x] = '!'
    }
    println(mutableGrid.joinToString("\n") { it.joinToString("") })
}