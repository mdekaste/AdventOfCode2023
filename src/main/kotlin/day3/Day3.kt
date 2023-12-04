package day3

import Challenge
import day3.Day3.plus

fun main() {
    Day3.part1().let(::println)
    Day3.part2().let(::println)
}

object Day3 : Challenge() {
    data class Number(
        override val pos: Pair<Int, Int>,
        var value: Int,
    ) : Segment

    sealed interface Segment {
        val pos: Pair<Int, Int>
    }

    data class Part(
        override val pos: Pair<Int, Int>,
        val kind: Char,
    ) : Segment

    val grid = buildMap<Pair<Int, Int>, Segment> {
        input.lines().forEachIndexed { y, line ->
            line.forEachIndexed { x, char ->
                val pos = y to x
                if (char.isDigit()) {
                    val digit = char.digitToInt()
                    when (val leftNeighbour = get(pos + (0 to -1)) as? Number) {
                        null -> put(pos, Number(pos, digit))
                        else -> put(
                            pos,
                            leftNeighbour.apply {
                                value = value * 10 + char.digitToInt()
                            },
                        )
                    }
                } else if (char != '.') {
                    put(pos, Part(pos, char))
                }
            }
        }
    }
    val numbers = grid.values.distinct().filterIsInstance<Number>()
    val parts = grid.values.distinct().filterIsInstance<Part>()

    override fun part1(): Any? {
        numbers.filter {
            val neighbours = listOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1)
            val otherPos = i
            true
        }
    }

    operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> =
        first + other.first to second + other.second

// operator fun Pair<Int, Int>.minus(other: Pair<Int, Int>): Pair<Int, Int> = first - other.first to second - other.second

    override fun part2(): Any? {
        return null
    }
}
