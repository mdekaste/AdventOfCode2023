package day12

import Challenge
import kotlin.math.min

fun main() {
    Day12.part1().let(::println)
    Day12.part2().let(::println)
}

object Day12 : Challenge() {
    val parsed = input.lines().map { it.split(" ") }

    fun String.extend(amount: Int, separator: String) = List(amount) { this }.joinToString(separator)

    fun solve(amount: Int) = parsed.sumOf { (springs, damages) ->
        solve(
            springs = springs.extend(amount, "?").toList(),
            damaged = damages.extend(amount, ",").split(",").map(String::toInt),
        )
    }
    override fun part1() = solve(1)
    override fun part2() = solve(5)

    fun solve(springs: List<Char>, damaged: List<Int>): Long {
        buildMap {
            fun recursive(springs: List<Char>, damaged: List<Int>): Long = getOrPut(springs to damaged) {
                when (val damage = damaged.firstOrNull()) {
                    null -> if (springs.none { it == '#' }) 1 else 0
                    else -> {
                        val maxIndex = min(
                            springs.indexOf('#').takeIf { it >= 0 } ?: Int.MAX_VALUE,
                            springs.size - damaged.drop(1).sum() - damaged.size + 1 - damage,
                        )
                        (0..maxIndex).sumOf { index ->
                            val piece = springs.subList(index, index + damage)
                            when (piece.none { it == '.' } && springs.getOrNull(index + damage) != '#') {
                                true -> recursive(springs.drop(index + damage + 1), damaged.drop(1))
                                else -> 0
                            }
                        }
                    }
                }
            }
            return recursive(springs, damaged)
        }
    }
}
