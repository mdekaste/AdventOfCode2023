package day10

// ktlint-disable no-wildcard-imports
import Challenge
import kotlin.math.absoluteValue

fun main() {
    Day10.part1().let(::println)
    Day10.part2().let(::println)
}

object Day10 : Challenge() {
    private val points = run {
        lateinit var startPoint: Pair<Int, Int>
        val grid = input.lines().flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, c ->
                (y to x) to when (c) {
                    'L' -> listOf(NORTH, EAST)
                    '|' -> listOf(NORTH, SOUTH)
                    'J' -> listOf(NORTH, WEST)
                    'F' -> listOf(EAST, SOUTH)
                    '-' -> listOf(EAST, WEST)
                    '7' -> listOf(SOUTH, WEST)
                    'S' -> listOf(NORTH, EAST, SOUTH, WEST).also { startPoint = y to x }
                    else -> emptyList()
                }.map { (y2, x2) -> y2 + y to x2 + x }
            }
        }.toMap()
        // calculate the first valid move e.g. the move going up needs to also be the move going down.
        // This is needed because the start can lay next to its own path.
        val firstMove = grid.getValue(startPoint).first { from -> grid.getValue(from).any(startPoint::equals) }
        // walk through the pipeline, where the next move is the possible moves minus the one you came from
        generateSequence(startPoint to firstMove) { (from, to) ->
            when (to) {
                startPoint -> null
                else -> to to grid.getValue(to).minus(from).first()
            }
        }.map { it.first }.toList()
    }

    override fun part1(): Int = points.size / 2

    // area under n-sided polygon generalizes nicely in a strict 2D environment
    override fun part2() = points.plus(points.first())
        .zipWithNext { (y1, x1), (_, x2) -> (x2 - x1) * y1 }
        .sum().absoluteValue - part1() + 1
}
