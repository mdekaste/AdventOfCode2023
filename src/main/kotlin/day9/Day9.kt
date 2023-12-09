package day9

import Challenge

fun main() {
    Day9.part1().let(::println)
    Day9.part2().let(::println)
    Day9.solve().let(::println)
}

fun List<Int>.next(): Int = lastOrNull()?.let { it + zipWithNext { a, b -> b - a }.next() } ?: 0
object Day9 : Challenge() {
    private val parsed = input.lines().map { it.split(" ").map(String::toInt) }
    override fun part1() = parsed.sumOf { it.next() }
    override fun part2() = parsed.sumOf { it.asReversed().next() }
}
