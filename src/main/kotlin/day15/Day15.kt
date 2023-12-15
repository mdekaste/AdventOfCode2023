package day15

import Challenge

fun main() {
    Day15.part1().let(::println)
    Day15.part2().let(::println)
}

object Day15 : Challenge() {
    val parsed = input.split(",")

    private fun hash(input: String): Int = input.fold(0) { hash, c -> ((hash + c.code) * 17) % 256 }
    override fun part1() = parsed.map(::hash).sum()

    override fun part2() = parsed.fold(MutableList(256) { mutableMapOf<String, Int>() }) { acc, line ->
        acc.apply {
            val (value, focalLength) = line.split("=", "-")
            when ("-" in line) {
                true -> this[hash(value)] -= value
                false -> this[hash(value)][value] = focalLength.toInt()
            }
        }
    }.withIndex().sumOf { (i, map) -> (i + 1) * map.values.withIndex().sumOf { (j, value) -> (j + 1) * value } }
}