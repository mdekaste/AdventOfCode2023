package day12

import Challenge
import kotlin.math.min

fun main() {
    Day12.part1().let(::println)
    Day12.part2().let(::println)
}

object Day12 : Challenge() {
    val parsed = input.lines().map {
        it.split(" ").let { (a, b) ->
            a.toList() to b.split(",").map { it.toInt() }
        }
    }

    override fun part1(): Any? {
        return parsed.map { (springs, damaged) ->
            solve2(springs, damaged)
        }.sum()
    }
    override fun part2(): Any? {
        return parsed.map { (springs, damaged) ->
            List(5) { springs }.joinToString("?"){ it.joinToString("") }.toList() to List(5) { damaged }.flatten() }
            .map { (springs, damaged) ->
                solve2(springs, damaged)
            }.sum()
    }

    fun solve2(springs: List<Char>, damaged: List<Int>): Long {
        buildMap<Pair<List<Char>, List<Int>>, Long> {
            fun recursive(springs: List<Char>, damaged: List<Int>): Long = getOrPut(springs to damaged) {
                val damage = damaged.firstOrNull() ?: return@getOrPut if (springs.none { it == '#' }) {
                    1
                } else {
                    0
                }
                val firstIndex = 0
                val maxCheck = springs.size - (damaged.drop(1).sum() + damaged.drop(1).size) - damage
                val maxCheck2 = springs.indexOf('#').takeIf { it >= 0 } ?: Int.MAX_VALUE
                val maxCheck3 = min(maxCheck, maxCheck2)
                (firstIndex..maxCheck3).sumOf { index ->
                    val piece = springs.subList(index, index + damage)
                    if (piece.none { it == '.' } && springs.getOrNull(index + damage) != '#') {
                        recursive(
                            springs.drop(index + damage + 1),
                            damaged.drop(1),
                        )
                    } else {
                        0
                    }
                }
            }
            return recursive(springs, damaged)
        }
    }
}
