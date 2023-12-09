package day9

import Challenge

fun main() {
    Day9.part1().let(::println)
    Day9.part2().let(::println)
    Day9.solve().let(::println)
}

object Day9 : Challenge() {
    private val parsed = input.lines().map { it.split(" ").map(String::toInt) }
    private fun next(list: List<Int>): Int = list.lastOrNull()
        ?.let { last -> last + list.zipWithNext { a, b -> b - a }.let(::next) }
        ?: 0

    override fun part1() = parsed.sumOf(::next)
    override fun part2() = parsed.map(List<Int>::reversed).sumOf(::next)
}
