package day6

import Challenge
import kotlin.math.*

fun main() {
    Day6.part1().let(::println)
    Day6.part2().let(::println)
}

object Day6 : Challenge() {
    override fun part1() = input.lines()
        .map { it.split(" ").mapNotNull { it.toLongOrNull() } }
        .let { (a, b) -> a.zip(b) }
        .fold(1L) { acc, (time, distance) -> acc * solve(time, distance) }

    override fun part2(): Any? {
        val (time, distance) = input.lines().map { it.substringAfter(":").replace(" ", "").toLong() }
        return solve(time, distance)
    }

    private fun solve(time: Long, distance: Long): Long {
        fun solveQuadratic(a: Double, b: Double, c: Double): Long {
            val determinant = b * b - 4.0 * a * c
            val root1 = (-b + sqrt(determinant)) / (2 * a)
            val root2 = (-b - sqrt(determinant)) / (2 * a)
            return ceil(root2).toLong() - ceil(root1).toLong()
        }
        return solveQuadratic(-1.0, time.toDouble(), -distance.toDouble())
    }
}
