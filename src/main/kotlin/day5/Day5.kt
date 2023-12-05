package day5

import Challenge
import helpers.splitOnEmpty
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day5.part1().let(::println)
    Day5.part2().let(::println)
    Day5.solve().let(::println)
}

object Day5 : Challenge() {
    val parsed = input.splitOnEmpty().let {
        it.first().substringAfter("seeds: ").split(" ").map { it.toLong() } to it.drop(1).map {
            it.lines().drop(1).map { it.split(" ").map { it.toLong() } }
        }.map { maps ->
            buildMap {
                maps.forEach { (target, source, range) ->
                    put(source..<source + range, target - source)
                }
            }
        }
    }

    override fun part1(): Any? {
        val almanac = parsed.second
        val seeds = parsed.first
        return seeds.minOf {
            almanac.fold(it) { source, range ->
                source + (range.entries.firstOrNull { (targetRange, _) -> source in targetRange }?.value ?: 0L)
            }
        }
    }

    private fun LongRange.add(add: Long): LongRange = (start + add)..(last + add)
    private fun LongRange.rangeIntersect(other: LongRange) =
        max(first, other.first)..min(last, other.last)

    override fun part2(): Any? {
        val almanac = parsed.second
        val seeds = parsed.first.chunked(2).map { (seed, range) -> seed..<seed + range }
        fun recursiveMinimalSliver(range: LongRange, step: Int): Long {
            return when (step) {
                almanac.size -> range.first
                else -> almanac[step].mapNotNull { (target, step) ->
                    target.rangeIntersect(range).takeUnless { it.isEmpty() }?.add(step)
                }.minOfOrNull {
                    recursiveMinimalSliver(it, step + 1)
                } ?: range.first
            }
        }
        return seeds.minOf { recursiveMinimalSliver(it, 0) }
    }
}
