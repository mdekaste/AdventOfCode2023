package day10

import Challenge
import day10.Compass.*
import java.util.EnumSet
import kotlin.math.absoluteValue

fun main() {
    Day10.part1().let(::println)
    Day10.part2().let(::println)
}
enum class Compass { N, E, S, W }
typealias Point = Pair<Int, Int>
typealias Pipe = EnumSet<Compass>

object Day10 : Challenge() {

    val START_PIPE = EnumSet.allOf(Compass::class.java)
    val EMPTY_PIPE = EnumSet.noneOf(Compass::class.java)
    private fun Compass.otherSide() = Compass.entries[(Compass.entries.indexOf(this) + 2) % Compass.entries.size]
    private fun Pipe.travel(compass: Compass): Compass = when (this) {
        START_PIPE -> compass
        else -> minus(compass.otherSide()).first()
    }
    operator fun Point.plus(compass: Compass) = when (compass) {
        N -> first - 1 to second
        E -> first to second + 1
        S -> first + 1 to second
        W -> first to second - 1
    }

    val moves: List<Compass> = run {
        val grid = buildMap<Point, EnumSet<Compass>> {
            input.lines().forEachIndexed { y, line ->
                line.forEachIndexed { x, c ->
                    put(
                        y to x,
                        when (c) {
                            'L' -> EnumSet.of(N, E)
                            '|' -> EnumSet.of(N, S)
                            'J' -> EnumSet.of(N, W)
                            'F' -> EnumSet.of(E, S)
                            '-' -> EnumSet.of(E, W)
                            '7' -> EnumSet.of(S, W)
                            'S' -> START_PIPE
                            else -> EMPTY_PIPE
                        },
                    )
                }
            }
        }.withDefault { EMPTY_PIPE }
        val startPipe = grid.entries.first { (_, s) -> s == START_PIPE }.key
        val move = when {
            E in grid.getValue(startPipe + W) -> W
            S in grid.getValue(startPipe + N) -> N
            else -> E
        }
        generateSequence(startPipe to move) { (pos, move) ->
            val nextPos = pos + move
            nextPos.takeIf { it != startPipe }?.let { it to grid.getValue(nextPos).travel(move) }
        }.map { it.second }.toList()
    }

    override fun part1() = moves.size / 2

    override fun part2() = moves.onEach { println(it) }.fold(0 to 0) { (sum, d), move ->
        when (move) {
            N -> sum to d + 1
            S -> sum to d - 1
            W -> sum - d to d
            E -> sum + d to d
        }
    }.first.absoluteValue - (moves.size / 2) + 1
}
