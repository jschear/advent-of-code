package aoc

import kotlin.math.absoluteValue

typealias Chemical = String

data class ChemicalAmount(val amount: Int, val chemical: Chemical)

// Every chemical, except for ORE, is produced by a single reaction.
data class Reaction(val reactants: List<ChemicalAmount>, val product: ChemicalAmount)

const val ORE = "ORE"
const val FUEL = "FUEL"

fun parseInput(input: String): List<Reaction> = input.lines()
    .map { line ->
        val (reactantsString, productsString) = line.split("=>")
        val reactants = reactantsString.split(",").map(String::toChemicalAmount)
        val product = productsString.toChemicalAmount()
        Reaction(reactants, product)
    }

private fun String.toChemicalAmount(): ChemicalAmount {
    val (amountString, chemical) = trim().split(" ")
    return ChemicalAmount(amountString.toInt(), chemical)
}

fun minimumOreOneFuel(reactions: List<Reaction>): Long = minimumOre(1, reactions)

fun minimumOre(numFuel: Long, reactions: List<Reaction>): Long {
    val required = mutableMapOf(FUEL to numFuel)

    while (required.any { (chemical, amount) -> chemical != ORE && amount > 0 }) {
        val (chemical, amountRequired) = required.entries.first { it.key != ORE && it.value > 0 }
        val reaction = reactions.find { it.product.chemical == chemical } ?: error("No reaction that creates $chemical")
        val repetitions = amountRequired ceilDiv reaction.product.amount.toLong()
        for ((amount, reactant) in reaction.reactants) {
            required[reactant] = required.getOrDefault(reactant, 0) + (amount * repetitions)
        }
        val extra = (reaction.product.amount * repetitions) - amountRequired
        required[chemical] = -extra
    }

    return required[ORE]!!
}

fun fuelForOneTrillionOre(reactions: List<Reaction>): Long {
    val oneTrillion = 1_000_000_000_000L
    val fuelForOneTrillion = binarySearchLongsBy(oneTrillion, 1, oneTrillion) { numFuel -> minimumOre(numFuel, reactions) }

    return if (fuelForOneTrillion < 0) {
        // We need the amount of fuel _before_ it costs one trillion ore, so one before the insertion point
        val insertionPoint = -fuelForOneTrillion - 1
        insertionPoint - 1
    } else {
        fuelForOneTrillion
    }
}

/**
 * Adapted from stdlib. See [binarySearchBy].
 */
fun <K : Comparable<*>> binarySearchLongsBy(key: K, from: Long, to: Long, selector: (Long) -> K): Long {
    var low = from
    var high = to - 1L

    while (low <= high) {
        val mid = (low + high).ushr(1) // safe from overflows
        val midVal = selector(mid)
        val cmp = compareValues(midVal, key)

        when {
            cmp < 0 -> low = mid + 1L
            cmp > 0 -> high = mid - 1L
            else -> return mid // key found
        }
    }
    return -(low + 1)  // key not found
}

private infix fun Long.ceilDiv(other: Long): Long = this / other + if (this % other != 0L) 1L else 0L
