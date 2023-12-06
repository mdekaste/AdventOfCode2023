package day6

import Challenge
import helpers.extractInts

fun main() {
    Day6.part1().let(::println)
    Day6.part2().let(::println)
}

object Day6 : Challenge() {
    val parsed = input.lines().map { it.extractInts() }.let {
        mapOf(
            it[0][0] to it[1][0],
            it[0][1] to it[1][1],
            it[0][2] to it[1][2],
            it[0][3] to it[1][3],
        )
    }

    override fun part1(): Any? {
        return parsed.entries.fold(1) { acc, (time, distance) ->
            acc * (0..time).map { i -> (time - i) * i }.count { it > distance }
        }
    }

    override fun part2(): Any? {
        val (time, distance) = input.lines().map { it.extractInts().joinToString("") }.map { it.toLong() }
        return (0..time).map { i -> (time - i) * i }.count { it > distance }
    }
}
