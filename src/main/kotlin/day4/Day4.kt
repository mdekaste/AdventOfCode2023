package day4

import Challenge

fun main() {
    Day4.part1().let(::println)
    Day4.part2().let(::println)
}

object Day4 : Challenge() {
    private val parsed = input.lines().map { line ->
        line.split("\\D+".toRegex()).drop(1).map(String::toInt).let { numbers ->
            numbers.subList(1, 11).toSet().intersect(numbers.subList(11, 36).toSet()).indices
        }
    }

    override fun part1() = parsed.sumOf { it.fold(0) { acc, _ -> if (acc == 0) 1 else acc * 2 }.toInt() }

    override fun part2() = IntArray(parsed.size) { 1 }.apply {
        parsed.forEachIndexed { index, winningNumbers ->
            winningNumbers.forEach {
                this[index + it + 1] += this[index]
            }
        }
    }.sum()
}
