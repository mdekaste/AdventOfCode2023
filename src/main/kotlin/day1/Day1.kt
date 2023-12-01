package day1

import Challenge

fun main() {
    Day1.solve().let(::println)
}

object Day1 : Challenge() {
    val parsed = input.lines()

    private fun List<String>.solve() = map { it.filter(Char::isDigit) }
        .sumOf { "${it.first()}${it.last()}".toInt() }

    override fun part1() = parsed.solve()

    override fun part2() = parsed
        .map { replacements.fold(it) { line, (key, value) -> line.replace(key, value) } }
        .solve()

    private val replacements = listOf(
        "one" to "o1e",
        "two" to "t2o",
        "three" to "t3e",
        "four" to "4",
        "five" to "5e",
        "six" to "6",
        "seven" to "7n",
        "eight" to "e8t",
        "nine" to "n9e",
    )
}
