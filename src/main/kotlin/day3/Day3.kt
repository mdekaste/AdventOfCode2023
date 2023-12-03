package day3

import Challenge
import day3.Day3.plus

fun main() {
    Day3.part1().let(::println)
    Day3.part2().let(::println)
}

object Day3 : Challenge() {
    val parsed = input.lines().withIndex().flatMap { (y, line) ->
        line.withIndex().map { (x, char) ->
            (y to x) to char
        }
    }.toMap()

    sealed interface Segment
    data class Number(
        val startPos: Pair<Int, Int>,
        val number: Int,
        val isPart: Boolean,
    ) : Segment

    data class Part(
        val pos: Pair<Int, Int>,
        val kind: Char,
    ) : Segment

    override fun part1(): Any? {
        val parts = buildMap {
            var pos = 0 to 0
            var number = 0
            var isPart = false
            parsed.entries.forEach { (point, char) ->
                if (char.isDigit()) {
                    number *= 10
                    number += char.digitToInt()
                    if (isAdjacent(point)) {
                        isPart = true
                    }
                }
                if (!char.isDigit()) {
                    if (number != 0) {
                        put(pos, Number(pos, number, isPart))
                        pos = point
                        number = 0
                        isPart = false
                    }
                }
            }
        }
        return parts.entries.filter { (key, value) ->
            value.isPart
        }.sumOf { (key, value) -> value.number }
    }

//    fun buildNumber(pos: Pair<Int, Int>, char: Char): Int {
//        var startPos = pos
//        while (true) {
//            startPos += (0 to -1)
//            if (parsed[startPos] == null) {
//                break
//            }
//            if (!parsed[startPos]!!.isDigit()) {
//                break
//            }
//        }
//        startPos += (0 to 1)
//        var number = parsed[startPos]!!.takeIf { it.isDigit() }?.digitToInt() ?: return 0
//        while (true) {
//            startPos += (0 to 1)
//            if (parsed[startPos] == null) {
//                break
//            }
//            if (!parsed[startPos]!!.isDigit()) {
//                break
//            }
//            number = number * 10 + parsed.getValue(startPos).digitToInt()
//        }
//        return number
//    }

    fun isAdjacent(point: Pair<Int, Int>): Boolean {
        val adjacency = listOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1)
        return adjacency.any {
            val character = parsed[point + it]
            when (character) {
                null -> false
                else -> when {
                    character.isDigit() -> false
                    character == '.' -> false
                    else -> true
                }
            }
        }
    }

    operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> =
        first + other.first to second + other.second

    // operator fun Pair<Int, Int>.minus(other: Pair<Int, Int>): Pair<Int, Int> = first - other.first to second - other.second

    override fun part2(): Any? {
        val parts = buildMap<Pair<Int, Int>, Segment> {
            var pos = 0 to -1
            var number = 0
            var isPart = false
            var prevWasntNumber = true
            parsed.entries.forEach { (point, char) ->
                if (char.isDigit()) {
                    if (prevWasntNumber) {
                        pos = point
                    }
                    prevWasntNumber = false
                    number *= 10
                    number += char.digitToInt()
                    if (isAdjacent(point)) {
                        isPart = true
                    }
                }
                if (!char.isDigit()) {
                    prevWasntNumber = true
                    if (number != 0) {
                        val numberClass = Number(pos, number, isPart)
                        put(pos, Number(pos, number, isPart))
                        val numberLength = number.toString().length
                        if (numberLength > 1) {
                            put(pos + (0 to 1), numberClass)
                        }
                        if (numberLength > 2) {
                            put(pos + (0 to 2), numberClass)
                        }
                    }
                    if (char != '.') {
                        put(point, Part(point, char))
                    }
                    number = 0
                    isPart = false
                }
            }
        }
        return parts.filter { (key, value) -> value is Part }.map { (key, value) ->
            value as Part
            if (value.kind != '*') {
                return@map 0
            }
            println(key to value)
            val neighbours = listOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1)
            val toReturn = neighbours.mapNotNull {
                parts[key + it] as? Number
            }.distinct().takeIf { it.size == 2 }?.let { (a, b) ->
                println(a to b)
                a.number * b.number
            } ?: 0
            toReturn
        }.sum()
    }
}
