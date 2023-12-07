package day7

import Challenge
import java.util.AbstractMap.SimpleEntry

fun main() {
    Day7.part1().let(::println)
    Day7.part2().let(::println)
}

object Day7 : Challenge() {
    val parsed = input.lines().map { it.split(" ").let { (hand, value) -> Hand(hand, value.toInt()) } }

    data class Hand(
        val suits: String,
        val value: Int,
    )

    override fun part1() = solve(
        typeMapper = fun(suits: String): List<Int> = suits.groupingBy { it }.eachCount().values.sortedDescending(),
        charMapper = fun(char: Char): Int = when {
            char.isDigit() -> char.digitToInt()
            char == 'T' -> 10
            char == 'Q' -> 12
            char == 'K' -> 13
            char == 'A' -> 14
            else -> 11 // 'J'
        },
    )

    override fun part2() = solve(
        typeMapper = fun(suits: String): List<Int> = suits.groupingBy { it }.eachCount().toMutableMap().apply {
            val jokerCount = remove('J') ?: 0
            val maxEntry = maxByOrNull { it.value } ?: SimpleEntry('J', 0)
            set(maxEntry.key, maxEntry.value + jokerCount)
        }.values.sortedDescending(),
        charMapper = fun(char: Char): Int = when {
            char.isDigit() -> char.digitToInt()
            char == 'T' -> 10
            char == 'Q' -> 12
            char == 'K' -> 13
            char == 'A' -> 14
            else -> 1 // 'J'
        },
    )

    fun solve(typeMapper: (String) -> List<Int>, charMapper: (Char) -> Int): Int = parsed.sortedWith(
        compareBy<Hand> { typeMapper(it.suits)[0] }
            .thenBy { typeMapper(it.suits).getOrNull(1) ?: 0 }
            .thenBy { charMapper(it.suits[0]) }
            .thenBy { charMapper(it.suits[1]) }
            .thenBy { charMapper(it.suits[2]) }
            .thenBy { charMapper(it.suits[3]) }
            .thenBy { charMapper(it.suits[4]) },
    ).mapIndexed { index, hand -> (index + 1) * hand.value }.sum()
}
