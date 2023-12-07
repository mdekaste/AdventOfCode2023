package day7

import Challenge

fun main() {
    Day7.part1().let(::println)
    Day7.part2().let(::println)
}

object Day7 : Challenge() {
    val parsed = input.lines().map {
        it.split(" ").let { (a, b) ->
            a to b.toInt()
        }
    }
    override fun part1(): Any? {
        return parsed.sortedWith(
            compareBy<Pair<String, Int>> {
                it.first.groupingBy { it }.eachCount().values.max()
            }.thenBy {
                it.first.groupingBy { it }.eachCount().values.sortedByDescending { it }[1]
            }.thenBy {
                it.first.first().let(::charToValue)
            }.thenBy {
                it.first[1].let(::charToValue)
            }.thenBy {
                it.first[2].let(::charToValue)
            }.thenBy {
                it.first[3].let(::charToValue)
            }.thenBy {
                it.first[4].let(::charToValue)
            },
        ).mapIndexed { index, pair ->
            (index + 1) * pair.second
        }.sum()
    }

    fun charToValue(char: Char): Int = when {
        char.isDigit() -> char.digitToInt()
        char == 'T' -> 10
        char == 'J' -> 11
        char == 'Q' -> 12
        char == 'K' -> 13
        char == 'A' -> 14
        else -> error("")
    }

    fun charToValue2(char: Char): Int = when {
        char.isDigit() -> char.digitToInt()
        char == 'T' -> 10
        char == 'J' -> 1
        char == 'Q' -> 12
        char == 'K' -> 13
        char == 'A' -> 14
        else -> error("")
    }

    override fun part2(): Any? {
        return parsed.sortedWith(
            compareBy<Pair<String, Int>> { hand ->
                hand.first.groupingBy { it }.eachCount().maxOf {  if(it.key == 'J') it.value else it.value + hand.first.count{ it == 'J'} }
            }.thenBy {
                it.first.replace("J", "").groupingBy { it }.eachCount().values.sortedByDescending { it }.getOrNull(1) ?: 0
            }.thenBy {
                it.first.first().let(::charToValue2)
            }.thenBy {
                it.first[1].let(::charToValue2)
            }.thenBy {
                it.first[2].let(::charToValue2)
            }.thenBy {
                it.first[3].let(::charToValue2)
            }.thenBy {
                it.first[4].let(::charToValue2)
            },
        ).onEach { println(it) }.mapIndexed { index, pair ->
            (index + 1) * pair.second
        }.sum()
    }
}
