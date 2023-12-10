package day10

// ktlint-disable no-wildcard-imports
import Challenge
import day10.Compass.*
import java.util.*
import kotlin.math.absoluteValue

fun main() {
    Day10.part1().let(::println)
    Day10.part2().let(::println)
}

enum class Compass { N, E, S, W }
typealias Point = Pair<Int, Int>
typealias Pipe = EnumSet<Compass>

object Day10 : Challenge() {
    private val START_PIPE = EnumSet.allOf(Compass::class.java)
    private val EMPTY_PIPE = EnumSet.noneOf(Compass::class.java)
    private fun Compass.reciprocal() = Compass.entries[(Compass.entries.indexOf(this) + 2) % Compass.entries.size]
    private fun Pipe.otherside(compass: Compass): Compass = when (this) {
        START_PIPE -> compass
        else -> minus(compass.reciprocal()).first()
    }

    operator fun Point.plus(compass: Compass) = when (compass) {
        N -> first - 1 to second
        E -> first to second + 1
        S -> first + 1 to second
        W -> first to second - 1
    }

    private val directions: List<Compass> = run {
        val grid = input.lines().flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, c ->
                y to x to when (c) {
                    'L' -> EnumSet.of(N, E)
                    '|' -> EnumSet.of(N, S)
                    'J' -> EnumSet.of(N, W)
                    'F' -> EnumSet.of(E, S)
                    '-' -> EnumSet.of(E, W)
                    '7' -> EnumSet.of(S, W)
                    'S' -> START_PIPE
                    else -> EMPTY_PIPE
                }
            }
        }.toMap().withDefault { EMPTY_PIPE }
        val startPipe = grid.entries.first { (_, s) -> s == START_PIPE }.key
        val direction = when {
            E in grid.getValue(startPipe + W) -> W
            S in grid.getValue(startPipe + N) -> N
            else -> E
        }
        generateSequence(startPipe to direction) { (point, direction) ->
            point.plus(direction).takeUnless(startPipe::equals)?.let { it to grid.getValue(it).otherside(direction) }
        }.map { (_, direction) -> direction }.toList()
    }

    override fun part1() = directions.size / 2

    override fun part2() = directions.fold(0 to 0) { (sum, vector), compass ->
        when (compass) {
            N -> sum + vector to vector
            E -> sum to vector + 1
            S -> sum - vector to vector
            W -> sum to vector - 1
        }
    }.first.absoluteValue - part1() + 1
}
