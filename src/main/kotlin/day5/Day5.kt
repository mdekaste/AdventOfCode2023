package day5

import Challenge
import helpers.extractLongs
import helpers.intersect
import helpers.plus
import helpers.splitOnEmpty

fun main() {
    Day5.part1().let(::println)
    Day5.part2().let(::println)
    Day5.solve().let(::println)
}

object Day5 : Challenge() {
    val seeds: List<Long>
    val almanac: List<Map<LongRange, Long>>

    init {
        input.splitOnEmpty().also { chunks ->
            seeds = chunks.first().extractLongs()
            almanac = chunks.drop(1).map { chunk ->
                chunk.lines()
                    .drop(1)
                    .map { line -> line.extractLongs() }
                    .associate { (target, source, range) -> source..<source + range to target - source }
            }
        }
    }

    override fun part1() = solve(seeds.map { it..it })

    override fun part2() = solve(seeds.chunked(2).map { (seed, range) -> seed..<seed + range })

    private fun solve(seeds: List<LongRange>) = almanac.fold(seeds) { ranges, mapper ->
        ranges.flatMap { range ->
            mapper.mapNotNull { (target, step) ->
                target.intersect(range).takeUnless { it.isEmpty() }?.plus(step)
            }
        }
    }.minOf { it.first }
}
