package aoc

typealias Node = String

class Graph(input: String) {
    private val edges: Map<Node, Node>
    private val nodes: Set<Node>

    init {
        // B)C A)D
        // [[B, C], [A, D]]
        val inputs = input.split("\n").map { it.split(")") }
        edges = inputs.associate { Pair(it[1], it[0]) }
        nodes = inputs.flatten().toSet()

        println(nodes)
        println(edges)
    }

    fun totalOrbits(): Int =
        nodeToOrbits()
            .values
            .fold(0) { acc, value -> acc + value.size }

    fun minOrbitalTransfers(): Int {
        val nodeToOrbits = nodeToOrbits()
        val youOrbits = nodeToOrbits["YOU"] ?: error("Where are you...")
        val sanOrbits = nodeToOrbits["SAN"] ?: error("Where is Santa...")

        val pathDifferences = youOrbits.union(sanOrbits) - youOrbits.intersect(sanOrbits)
        return pathDifferences.size
    }

    private fun nodeToOrbits(): Map<Node, Set<Node>> {
        val nodeToOrbits: MutableMap<Node, Set<Node>> = mutableMapOf()
        for (node in nodes) {
            if (node == "COM") continue
            val orbits = mutableSetOf<Node>()
            var currNode = node
            do {
                currNode = edges[currNode] ?: error("No path!")
                orbits.add(currNode)
            } while (currNode != "COM")
            nodeToOrbits[node] = orbits
        }
        return nodeToOrbits
    }
}