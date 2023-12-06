package day3

import Challenge

fun main() {
    Day3.part1().let(::println)
    Day3.part2().let(::println)
}

object Day3 : Challenge() {
    private val parsed = input.lines().withIndex().flatMap { (y, line) ->
        line.withIndex().map { (x, char) ->
            (y to x) to char
        }
    }.toMap().let {
        buildMap<Pair<Int, Int>, Segment> {
            it.forEach { (y, x), char ->
                when {
                    char.isDigit() -> {
                        val gear = get(y to x - 1) as? Part ?: Part()
                        gear.apply {
                            positions += y to x
                            value = value * 10 + char.digitToInt()
                        }
                        put(y to x, gear)
                    }
                    char != '.' -> {
                        put(y to x, Symbol(listOf(y to x), char))
                    }
                }
            }
        }
    }.apply {
        values.forEach { segment ->
            listOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1).forEach { (y, x) ->
                segment.positions.forEach { (baseY, baseX) ->
                    this[baseY + y to baseX + x]?.also {
                        segment.neighbours.add(it)
                    }
                }
            }
        }
    }.values.toSet()

    sealed interface Segment {
        val neighbours: MutableSet<Segment>
        val positions: List<Pair<Int, Int>>
    }
    data class Part(
        override val positions: MutableList<Pair<Int, Int>> = mutableListOf(),
        var value: Int = 0,
    ) : Segment {
        override val neighbours: MutableSet<Segment> = mutableSetOf()
    }
    data class Symbol(
        override val positions: List<Pair<Int, Int>>,
        val char: Char,
    ) : Segment {
        override val neighbours: MutableSet<Segment> = mutableSetOf()
    }

    override fun part1() = parsed
        .filterIsInstance<Part>()
        .filter { it.neighbours.any { it is Symbol } }
        .sumOf { it.value }

    override fun part2() = parsed
        .filterIsInstance<Symbol>()
        .filter { it.char == '*' }
        .filter { it.neighbours.size == 2 && it.neighbours.all { it is Part } }
        .sumOf { it.neighbours.filterIsInstance<Part>().let { (a, b) -> a.value * b.value } }
}
