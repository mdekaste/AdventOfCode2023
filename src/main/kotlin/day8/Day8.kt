package day8

import Challenge
import helpers.splitOnEmpty

fun main() {
    Day8.part1().let(::println)
    Day8.part2().let(::println)
}

object Day8 : Challenge() {
    val path: Sequence<Char>
    val graph: Map<String, Pair<String, String>>

    init {
        input.splitOnEmpty().let { (lr, commands) ->
            path = sequence { while (true) yieldAll(lr.toList()) }
            graph = commands.lines().map { line ->
                line.split(" = ").let { (a, b) ->
                    a to (b.substringAfter('(').substringBefore(')').split(", ").let { (x, y) -> x to y })
                }
            }.toMap()
        }
    }

    override fun part1() = solve({ it == "AAA" }, { it == "ZZZ" })

    override fun part2() = solve({ it.endsWith('A') }, { it.endsWith('Z') })

    fun solve(startPositions: (String) -> Boolean, endPositions: (String) -> Boolean) =
        graph.filterKeys(startPositions).keys.map {
            path.foldIndexed(it) { index, key, c ->
                if (endPositions(key)) {
                    return@map index.toLong()
                }
                when (c) {
                    'L' -> graph.getValue(key).first
                    else -> graph.getValue(key).second
                }
            }.let { error("should not exit loop") }
        }.reduce(::lcm)

    private fun lcm(a: Long, b: Long) = (a * b) / gcd(a, b)

    private fun gcd(a: Long, b: Long): Long = when (b) {
        0L -> a
        else -> gcd(b, a % b)
    }
}
