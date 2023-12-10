package day10

// ktlint-disable no-wildcard-imports
import Challenge
import kotlin.math.absoluteValue

fun main() {
    Day10.part1().let(::println)
    Day10.part2().let(::println)
}
typealias Point = Pair<Int, Int>
typealias Pipe = List<Point>

object Day10 : Challenge() {
    private val NORTH = -1 to 0
    private val EAST = 0 to 1
    private val SOUTH = 1 to 0
    private val WEST = 0 to -1

    private operator fun Point.plus(other: Point) = first + other.first to second + other.second
    private operator fun Point.minus(other: Point) = first - other.first to second - other.second

    private val points: List<Point> = run {
        lateinit var startPoint: Point
        val grid: Map<Point, Pipe> = input.lines().flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, c ->
                (y to x) to when (c) {
                    'L' -> listOf(NORTH, EAST)
                    '|' -> listOf(NORTH, SOUTH)
                    'J' -> listOf(NORTH, WEST)
                    'F' -> listOf(EAST, SOUTH)
                    '-' -> listOf(EAST, WEST)
                    '7' -> listOf(SOUTH, WEST)
                    'S' -> listOf(NORTH, EAST, SOUTH, WEST).also { startPoint = y to x }
                    else -> emptyList<Point>()
                }.map { it + (y to x) }
            }
        }.toMap()
        val firstMove = grid.getValue(startPoint).first { from -> grid.getValue(from).any { it == startPoint } }
        generateSequence(startPoint to firstMove) { (from, to) ->
            when (to) {
                startPoint -> null
                else -> to to grid.getValue(to).minus(from).first()
            }
        }.map { it.first }.toList()
    }

    override fun part1(): Int = points.size / 2

    override fun part2() = points.plus(points.first())
        .zipWithNext { (y1, x1), (_, x2) -> (x2 - x1) * y1 }
        .sum().absoluteValue - part1() + 1
}
