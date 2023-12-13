package day13

import Challenge
import helpers.splitOnEmpty
import kotlin.math.min

fun main() {
    Day13.part1().let(::println)
    Day13.part2().let(::println)
}

object Day13 : Challenge() {
    val parsed = input.splitOnEmpty().map { board ->
        board.lines()
    }
    override fun part1(): Any? {
        return parsed.map { it.toReflectionIndex() }.sum()
    }

    fun List<String>.toReflectionIndex(not: Int = -1): Int {
        return (1 until size).mapNotNull {
            val above = subList(0, it)
            val under = subList(it, size)
            if (above.matches(under)) {
                100 * it
            } else {
                null
            }
        }.firstOrNull { it != not } ?: this.flip(not)
    }

    fun List<String>.flip(not: Int = -1): Int {
        val result = List(first().length) { List(size) { ' ' }.toMutableList() }
        for (x in 0 until size) {
            for (y in 0 until first().length) {
                result[y][x] = get(x).get(y)
            }
        }
        val actual = result.map { it.joinToString("") }
        return (
            (1 until actual.size).mapNotNull {
                val above = actual.subList(0, it)
                val under = actual.subList(it, actual.size)
                if (above.matches(under)) {
                    it
                } else {
                    null
                }
            }.firstOrNull{ it != not } ?: 0
            )
    }

    fun List<String>.matches(other: List<String>): Boolean {
        for (i in 0..<min(size, other.size)) {
            val thisIndex = size - 1 - i
            if (get(thisIndex) != other[i]) {
                return false
            }
        }
        return true
    }

    override fun part2(): Any? {
        return parsed.map { boards ->
            boards.fixSmudge().let { (source, others) ->
                val sourceReflectionIndex = source.toReflectionIndex()
                val x = others.map { it.toReflectionIndex(sourceReflectionIndex) }.filter { it != 0 }.firstOrNull() { it != sourceReflectionIndex } ?: -1
                x
            }
        }.sum()
    }

    fun List<String>.fixSmudge(): Pair<List<String>, List<List<String>>> {
        val result = mutableListOf<List<String>>()
        for (y in 0 until size) {
            for (x in 0 until first().length) {
                val resultingSet = map { it.toMutableList() }.toList()
                result += resultingSet.apply {
                    val char = this[y][x]
                    if (char == '#') {
                        resultingSet[y][x] = '.'
                    } else {
                        resultingSet[y][x] = '#'
                    }
                }.map { it.joinToString("") }
            }
        }
        return this to result
    }
}
