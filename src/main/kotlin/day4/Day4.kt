package day4

import Challenge

fun main() {
    Day4.part1().let(::println)
    Day4.part2().let(::println)
    println(2 ushr 2)
    Day4.solve().let(::println)
}

object Day4 : Challenge() {

    private val parsed = input.lines().map { line ->
        line.split("\\D+".toRegex()).drop(1).map(String::toInt).let { numbers ->
            numbers.subList(1, 11).intersect(numbers.subList(11, 36).toSet()).size
        }
    }

    override fun part1() = parsed.sumOf { 1 shl it shr 1 }

    override fun part2() = IntArray(parsed.size) { 1 }.apply {
        for ((index, winningNumbers) in parsed.withIndex()) {
            repeat(winningNumbers) {
                this[index + it + 1] += this[index]
            }
        }
    }.sum()
}
