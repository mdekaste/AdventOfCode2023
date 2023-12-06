package day3

import Challenge

fun main() {
    Day3.part1().let(::println)
    Day3.part2().let(::println)
}

typealias Point = Pair<Int, Int>

object Day3 : Challenge() {
    private var parts: Set<Part>
    private var symbols: Set<Symbol>
    init {
        val graph = buildMap<Point, Segment> {
            input.lines().forEachIndexed { y, line ->
                line.forEachIndexed { x, char ->
                    when {
                        char.isDigit() -> put(
                            key = y to x,
                            value = get(y to x - 1).let { it as? Part ?: Part() }.apply {
                                positions += y to x
                                value = value * 10 + char.digitToInt()
                            },
                        )
                        char != '.' -> put(
                            key = y to x,
                            value = Symbol(
                                positions = listOf(y to x),
                                char = char,
                            ),
                        )
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
        }
        parts = graph.values.filterIsInstance<Part>().toSet()
        symbols = graph.values.filterIsInstance<Symbol>().toSet()
    }

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

    override fun part1() = parts
        .filter { it.neighbours.any { it is Symbol } }
        .sumOf { it.value }

    override fun part2() = symbols
        .filter { it.char == '*' }
        .filter { it.neighbours.size == 2 && it.neighbours.all { it is Part } }
        .sumOf { it.neighbours.filterIsInstance<Part>().let { (a, b) -> a.value * b.value } }
}
