package day8

import Challenge
import helpers.splitOnEmpty

fun main() {
    Day8.part1().let(::println)
    Day8.part2().let(::println)
}

object Day8 : Challenge() {
    val parsed = input.splitOnEmpty().let { (lr, commands) ->
        lr.toList() to commands.lines().map { line ->
            line.split(" = ").let { (a, b) ->
                a to (b.substring(1).substringBefore(')').split(", ").let { (x, y) -> x to y })
            }
        }.toMap()
    }

    override fun part1(): Any? {
        val sequence = sequence {
            while (true) {
                yieldAll(parsed.first.asSequence())
            }
        }
        val graph = parsed.second
        var curNode = graph.get("AAA") ?: return null
        sequence.forEachIndexed { index, c ->
            val next = when (c) {
                'L' -> curNode.first
                'R' -> curNode.second
                else -> error("")
            }
            if (next == "ZZZ") {
                return index + 1
            }
            curNode = graph.getValue(next)
        }
        return null
    }

    override fun part2(): Any? {
        val graph = parsed.second
        val nodes = graph.filter { it.key.endsWith('A') }
        var cycles = nodes.map { findCycle(it.key) }
        val stepPositions = cycles.map { it.first.toLong() }.toLongArray()
        val nextSteps: List<List<Int>> = cycles.map { it.second }
        val indicesOfNext = LongArray(stepPositions.size) { 0L }
        while (true) {
            val indexOfLowest = stepPositions.withIndex().minBy { it.value }.index
            val indexOfNext = indicesOfNext[indexOfLowest]++ % nextSteps[indexOfLowest].size
            stepPositions[indexOfLowest] += nextSteps[indexOfLowest][indexOfNext.toInt()].toLong()
            // println(stepPositions.toList() to stepPositions.distinct().size)
            if (stepPositions.distinct().size == 1) {
                return stepPositions[0]
            }
        }
    }

    fun findCycle(node: String): Pair<Int, List<Int>> {
        val sequence = sequence {
            while (true) {
                yieldAll(parsed.first.withIndex())
            }
        }
        val graph = parsed.second
        val (indexOfFirstZ, node2) = findFirstZ(node)
        var curNode = graph.getValue(node2)

        val set = mutableSetOf(node to 0)
        val indices = mutableSetOf(0)

        sequence.drop(indexOfFirstZ).forEachIndexed { index, (indexed, char) ->
            val next = when (char) {
                'L' -> curNode.first
                'R' -> curNode.second
                else -> error("")
            }
            if (next.endsWith('Z')) {
                indices.add(index + 1)
                val canAdd = set.add(next to indexed)
                if (!canAdd) {
                    return indexOfFirstZ to indices.zipWithNext { a, b -> b - a }
                }
            }
            curNode = graph.getValue(next)
        }
        error("")
    }

    fun findFirstZ(node: String): IndexedValue<String> {
        val sequence = sequence {
            while (true) {
                yieldAll(parsed.first)
            }
        }
        var curValue = parsed.second.getValue(node)
        sequence.forEachIndexed { index, c ->
            val next = when (c) {
                'L' -> curValue.first
                'R' -> curValue.second
                else -> error("")
            }
            if (next.endsWith('Z')) {
                return IndexedValue(index + 1, next)
            }
            curValue = parsed.second.getValue(next)
        }
        error("")
    }
}
