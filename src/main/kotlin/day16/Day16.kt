package day16

import Challenge
import Challenge.Companion.EAST
import Challenge.Companion.NORTH
import Challenge.Companion.SOUTH
import Challenge.Companion.WEST
import Point
import day16.Direction.*

fun main() {
    Day16.part1().let(::println)
    Day16.part2().let(::println)
    Day16.solve().let(::println)
}

object Day16 : Challenge() {

    private val graph = input.lines().flatMapIndexed { y, s ->
        s.mapIndexed { x, c -> y to x to Mirror.MAP.getValue(c) }
    }.toMap()

    val pathFinder =
        DeepRecursiveFunction<Triple<Point, Direction, MutableSet<Pair<Point, Direction>>>, Unit> { (point, direction, set) ->
            if (point in graph.keys && set.add(point to direction)) {
                graph.getValue(point).fromTo.getValue(direction).forEach {
                    callRecursive(Triple(point + it.position, it, set))
                }
            }
        }

    fun solve(point: Point, direction: Direction) = buildSet {
        pathFinder(Triple(point, direction, this))
    }.map { it.first }.distinct().size

    override fun part1(): Int = solve(0 to 0, E)

    override fun part2() = buildList {
        val yMax = graph.maxOf { it.key.first }
        val xMax = graph.maxOf { it.key.second }
        graph.keys.filter { it.first == 0 }.mapTo(this) { S to it }
        graph.keys.filter { it.second == 0 }.mapTo(this) { E to it }
        graph.keys.filter { it.first == yMax }.mapTo(this) { N to it }
        graph.keys.filter { it.second == xMax }.mapTo(this) { W to it }
    }.maxOf { solve(it.second, it.first) }
}

enum class Direction(val position: Point) {
    N(NORTH), E(EAST), S(SOUTH), W(WEST)
}

enum class Mirror(val character: Char, val fromTo: Map<Direction, Array<Direction>>) {
    VERT('|', mapOf(N to arrayOf(N), E to arrayOf(N, S), S to arrayOf(S), W to arrayOf(N, S))),
    HOR('-', mapOf(N to arrayOf(W, E), E to arrayOf(E), S to arrayOf(W, E), W to arrayOf(W))),
    SLASH('/', mapOf(N to arrayOf(E), E to arrayOf(N), S to arrayOf(W), W to arrayOf(S))),
    BACK('\\', mapOf(N to arrayOf(W), E to arrayOf(S), S to arrayOf(E), W to arrayOf(N))),
    DOT('.', mapOf(N to arrayOf(N), E to arrayOf(E), S to arrayOf(S), W to arrayOf(W)));

    companion object {
        val MAP = entries.associateBy { it.character }
    }
}