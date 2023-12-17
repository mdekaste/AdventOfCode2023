package day3

import Challenge
import winds

fun main() {
    Day3.part1().let(::println)
    Day3.part2().let(::println)
}

object Day3 : Challenge() {
    val matcher = """(\d+|[^\.])""".toRegex()
    val parsed = input.lines().flatMapIndexed { y, s ->
        matcher.findAll(s).map { Part(y, it) }.flatMap { p -> p.positions.map { it to p } }
    }.toMap()

    override fun part1() = parsed.values.distinct()
        .filter { it.isValue() }
        .filter { p -> p.neighbours().any { !it.isValue() } }
        .sumOf { it.value() }

    override fun part2() = parsed.values.distinct()
        .filter { p -> p.isValue() }
        .mapNotNull { p ->
            p.neighbours().filter { it.isGear('*') }
                .firstNotNullOfOrNull { it.neighbours().firstOrNull { it.isValue() && it != p } }
                ?.let { p to it }
        }
        .sumOf { (a, b) -> a.value() * b.value() } / 2

    class Part(y: Int, matchResult: MatchResult) {
        val positions = matchResult.range.map { x -> y to x }
        private val pure = matchResult.value
        fun neighbours() =
            positions.flatMapTo(mutableSetOf()) { it.winds() }.mapNotNullTo(mutableSetOf()) { parsed[it] }

        fun value() = pure.toInt()
        fun isValue() = pure.toIntOrNull() != null
        fun isGear(char: Char? = null) = char?.let { it == pure[0] } ?: !isValue()
    }
}
