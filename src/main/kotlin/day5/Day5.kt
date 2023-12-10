package day5

import Challenge
import day5.Day5.add
import day5.Day5.rangeIntersect
import helpers.extractLongs
import helpers.splitOnEmpty
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day5.part1().let(::println)
    Day5.part2().let(::println)
    Day5.solve().let(::println)
}

object Day5 : Challenge() {
    data class Input(
        val seeds: List<Long>,
        val almanac: List<Map<LongRange, Long>>,
    )

    private val parsed = input.splitOnEmpty().let { chunks ->
        Input(
            seeds = chunks.first().extractLongs(),
            almanac = chunks.drop(1).map { chunk ->
                chunk.lines()
                    .drop(1)
                    .map { line -> line.extractLongs() }
                    .associate { (target, source, range) -> source..<source + range to target - source }
            },
        )
    }

    override fun part1() = solve(parsed.seeds.map { it..it })

    override fun part2() = solve(parsed.seeds.chunked(2).map { (seed, range) -> seed..<seed + range })

    private fun LongRange.add(add: Long): LongRange = (start + add)..(last + add)
    private fun LongRange.rangeIntersect(other: LongRange) = max(first, other.first)..min(last, other.last)

    private fun solve(seeds: List<LongRange>) = parsed.almanac.fold(seeds) { ranges, mapper ->
        ranges.flatMap { range ->
            mapper.mapNotNull { (target, step) ->
                target.rangeIntersect(range).takeUnless { it.isEmpty() }?.add(step)
            }
        }
    }.minOf { it.first }
}
