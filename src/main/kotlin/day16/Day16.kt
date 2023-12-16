package day16

import Challenge
import Challenge.Companion.EAST
import Challenge.Companion.NORTH
import Challenge.Companion.SOUTH
import Challenge.Companion.WEST
import Challenge.Companion.plus
import Point

fun main() {
    Day16().part1().let(::println)
    Day16().part2().let(::println)
}

class Day16 : Challenge() {
    private val parsed = input.lines()
    private val graph = buildMap {
        parsed.forEachIndexed { y, s ->
            s.forEachIndexed { x, c ->
                put(y to x, Node(y to x, c, this))
            }
        }
    }

    override fun part1() = solve(listOf((0 to -1) to (0 to 0)))
    override fun part2() = solve(startOptions(parsed))

    private fun startOptions(parsed: List<String>) = buildList {
        val height = parsed.size
        val width = parsed.first().length
        for (x in parsed.first().indices) {
            add((-1 to x) to (0 to x))
            add((height to x) to (height - 1 to x))
        }
        for (y in parsed.indices) {
            add((y to -1) to (y to 0))
            add((y to width) to (y to width - 1))
        }
    }

    fun solve(startPoints: List<Pair<Point, Point>>): Int = startPoints.maxOf { (s1, s2) ->
        buildSet {
            add(s1 to s2)
            DeepRecursiveFunction<Pair<Point, Point>, Unit> { (from, cur) ->
                graph.getValue(cur).reflections.getValue(from).forEach {
                    if (add(cur to it.pos)) {
                        callRecursive(cur to it.pos)
                    }
                }
            }(s1 to s2)
        }.map { it.second }.distinct().size
    }
}

enum class Tile(val character: Char, val reflections: Map<Point, List<Point>>) {
    VERT(
        '|', mapOf(
            WEST to listOf(NORTH, SOUTH),
            EAST to listOf(NORTH, SOUTH),
            NORTH to listOf(SOUTH),
            SOUTH to listOf(NORTH)
        )
    ),
    SLASH(
        '/', mapOf(
            WEST to listOf(NORTH),
            EAST to listOf(SOUTH),
            NORTH to listOf(WEST),
            SOUTH to listOf(EAST)
        )
    ),
    DOT(
        '.', mapOf(
            WEST to listOf(EAST),
            EAST to listOf(WEST),
            NORTH to listOf(SOUTH),
            SOUTH to listOf(NORTH)
        )
    ),
    HOR(
        '-', mapOf(
            WEST to listOf(EAST),
            EAST to listOf(WEST),
            NORTH to listOf(WEST, EAST),
            SOUTH to listOf(WEST, EAST)
        )
    ),
    BACK(
        '\\', mapOf(
            WEST to listOf(SOUTH),
            EAST to listOf(NORTH),
            NORTH to listOf(EAST),
            SOUTH to listOf(WEST)
        )
    );
    companion object{
        val MAP = entries.associateBy { it.character }
    }
}

class Node(
    val pos: Point,
    val character: Char,
    graph: Map<Point, Node>
) {
    val reflections: Map<Point, List<Node>> by lazy {
        Tile.MAP.getValue(character).reflections.mapKeys { (key, _) -> key + pos }
            .mapValues { (_, values) -> values.mapNotNull { graph[it + pos] } }
    }
}
