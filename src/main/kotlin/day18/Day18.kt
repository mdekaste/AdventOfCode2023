package day18

import Challenge
import EAST
import NORTH
import Point
import SOUTH
import WEST
import plus
import times
import kotlin.math.absoluteValue

fun main() {
    Day18.part1().let(::println)
    Day18.part2().let(::println)
    Day18.solve().let(::println)
}

enum class Dir(val point: Point) { R(EAST), D(SOUTH), L(WEST), U(NORTH) }
object Day18 : Challenge() {
    val parsed = input.lineSequence().map { line ->
        line.split(" ").let { (a, b, c) ->
            Triple(Dir.valueOf(a), b.toInt(), c.substring(2..7).toInt(16))
        }
    }

    override fun part1() = parsed.map { (a, b, _) -> a to b }.solve()
    override fun part2() = parsed.map { (_, _, c) -> Dir.entries[c % 16] to c / 16 }.solve()

    private fun Sequence<Pair<Dir, Int>>.solve() =
        runningFold(0 to 0) { point, (dir, amount) -> point + dir.point * amount }
            .zipWithNext { (y1, x1), (_, x2) -> (x2 - x1) * y1.toLong() }
            .sum().absoluteValue + sumOf { it.second } / 2 + 1
}