package day11

import Challenge
import java.util.*
import kotlin.math.abs

// ktlint-disable no-wildcard-imports

fun main() {
    Day11.part1().let(::println)
    Day11.part2().let(::println)
}

object Day11 : Challenge() {
    private val parsed = input.lines()
        .flatMapIndexed { y: Int, s: String -> s.mapIndexedNotNull { x, c -> (y to x).takeIf { c == '#' } } }

    private fun holes(selector: (Pair<Int, Int>) -> Int) = parsed.asSequence()
        .map(selector)
        .sorted()
        .distinct()
        .zipWithNext { a, b -> b to b - a - 1 }
        .toMap(TreeMap())

    override fun part1() = solve(2)
    override fun part2() = solve(1000000)

    private fun solve(expansionAmount: Int): Long {
        val holesY = holes { it.first }
        val holesX = holes { it.second }

        val newPlaces = parsed.map { (y, x) ->
            val holesBeforeY = holesY.headMap(y, true).values.sum()
            val holesBeforeX = holesX.headMap(x, true).values.sum()
            y + holesBeforeY * (expansionAmount - 1) to x + holesBeforeX * (expansionAmount - 1)
        }

        return newPlaces.flatMap { (y1, x1) -> newPlaces.map { (y2, x2) -> abs(y2 - y1) + abs(x2 - x1) } }
            .sumOf { it.toLong() } / 2
    }
}
