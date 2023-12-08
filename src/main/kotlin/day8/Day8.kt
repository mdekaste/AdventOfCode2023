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
        val parsed = input.splitOnEmpty().let { (lr, commands) ->
            lr.toList() to commands.lines().map { line ->
                line.split(" = ").let { (a, b) ->
                    a to (b.substring(1).substringBefore(')').split(", ").let { (x, y) -> x to y })
                }
            }.toMap()
        }
        path = sequence { while (true) yieldAll(parsed.first) }
        graph = parsed.second
    }

    fun solve(startPositions: (String) -> Boolean, endPositions: (String) -> Boolean): Long {
        val positions = graph.filterKeys(startPositions).keys
        return positions.map {
            path.foldIndexed(it) { index, key, c ->
                if(endPositions(key)){
                    return@map index.toLong()
                }
                when (c) {
                    'L' -> graph.getValue(key).first
                    else -> graph.getValue(key).second
                }
            }
            error("")
        }.reduce(::lcm)
    }

    fun lcm(a: Long, b: Long) = (a * b) / gcd(a, b)

    fun gcd(a: Long, b: Long): Long = when (b) {
        0L -> a
        else -> gcd(b, a % b)
    }

    override fun part1() = solve({ it == "AAA" }, { it == "ZZZ" })

    override fun part2() = solve({ it.endsWith('A') }, { it.endsWith('Z') })
}
