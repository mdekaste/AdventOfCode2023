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
    override fun part2() = solve(generateOptions(parsed))

    private fun generateOptions(parsed: List<String>) = buildList {
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
        val curSet = mutableListOf(s1 to graph.getValue(s2))
        val visited = mutableSetOf(s1 to graph.getValue(s2))
        while (curSet.isNotEmpty()) {
            val (from, cur) = curSet.removeLast()
            cur.reflections[from]?.forEach {
                if (visited.add(cur.pos to it)) {
                    curSet.add(cur.pos to it)
                }
            }
        }
        visited.map { it.second }.distinct().size
    }
}

class Node(
    val pos: Point,
    val character: Char,
    graph: Map<Point, Node>
) {
    val reflections: Map<Point, List<Node>> by lazy {
        val neighbours = listOf(NORTH, SOUTH, WEST, EAST)
            .filter { graph.containsKey(it + pos) }
            .associateWith { graph.getValue(it + pos) }

        listOf(NORTH, SOUTH, WEST, EAST).associateBy({ it + pos }, {
            when (it) {
                WEST -> when (character) {
                    '.', '-' -> listOfNotNull(neighbours[EAST])
                    '|' -> listOfNotNull(neighbours[NORTH], neighbours[SOUTH])
                    '/' -> listOfNotNull(neighbours[NORTH])
                    else -> listOfNotNull(neighbours[SOUTH])
                }

                EAST -> when (character) {
                    '.', '-' -> listOfNotNull(neighbours[WEST])
                    '|' -> listOfNotNull(neighbours[NORTH], neighbours[SOUTH])
                    '/' -> listOfNotNull(neighbours[SOUTH])
                    else -> listOfNotNull(neighbours[NORTH])
                }

                NORTH -> when (character) {
                    '.', '|' -> listOfNotNull(neighbours[SOUTH])
                    '-' -> listOfNotNull(neighbours[EAST], neighbours[WEST])
                    '/' -> listOfNotNull(neighbours[WEST])
                    else -> listOfNotNull(neighbours[EAST])
                }

                else -> when (character) {
                    '.', '|' -> listOfNotNull(neighbours[NORTH])
                    '-' -> listOfNotNull(neighbours[EAST], neighbours[WEST])
                    '/' -> listOfNotNull(neighbours[EAST])
                    else -> listOfNotNull(neighbours[WEST])
                }
            }
        })
    }
}
