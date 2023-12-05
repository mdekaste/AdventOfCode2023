package day5

import Challenge
import helpers.splitOnEmpty

fun main() {
    println(0..1)
    Day5.part1().let(::println)
    Day5.part2().let(::println)
}

object Day5 : Challenge() {
    val parsed = input.splitOnEmpty().let {
        it.first().substringAfter("seeds: ").split(" ").map { it.toLong() } to it.drop(1).map {
            it.lines().drop(1).map { it.split(" ").map { it.toLong() } }
        }
    }

    override fun part1(): Any? {
        val almanac = parsed.second.let { almanac ->
            almanac.map { maps ->
                maps.map { (source, target, range) ->
                    (source until source + range) to (target until target + range)
                }
            }
        }
        val seeds = parsed.first
        val min = seeds.minOf {
            var endpoint = it
            almanac.forEach { maps ->
                val (targetRange, sourceRange) = maps.firstOrNull { (_, sourceRange) -> endpoint in sourceRange }
                    ?: (endpoint..<endpoint to endpoint..<endpoint)
                val distanceToStart = endpoint - sourceRange.first
                endpoint = targetRange.first + distanceToStart
            }
            endpoint
        }
        return min
    }

    override fun part2(): Any? {
        val almanac = parsed.second.let { almanac ->
            almanac.map { maps ->
                maps.map { (source, target, range) ->
                    (target until target + range) to (source until source + range)
                }
            }
        }.map {
            val sortedList = it.sortedBy { it.first.first }
            val firstRange = Long.MIN_VALUE until sortedList.first().first.first
            val lastRange = (sortedList.last().first.last + 1) until Long.MAX_VALUE
            val extended = listOf(firstRange to firstRange) + sortedList + (lastRange to lastRange)
            val map = extended.toMap().toMutableMap()
            val mapExtend = map.keys.zipWithNext { a, b ->
                val range = (a.last + 1) until b.first
                if (!range.isEmpty()) {
                    range to range
                } else {
                    null
                }
            }.mapNotNull { it }.toMap()
            map += mapExtend
            map
        }
        println(almanac)
        val seeds = parsed.first.chunked(2).map { (seed, range) -> seed until seed + range }
        val result = buildList<Pair<LongRange, LongRange>> {
            fun recursiveSlivers(seedRange: LongRange, range: LongRange, step: Int) {
                if (step >= almanac.size) {
                    add(seedRange to range)
                } else {
                    val potentialTargets = almanac[step]
                    potentialTargets.mapNotNull {
                        val intersection = it.key.rangeIntersect(range)
                        if (!intersection.isEmpty()) {
                            val distanceFromStart = intersection.start - it.key.start
                            val distanceFromEnd = intersection.last - it.key.last
                            val targetRange =
                                (it.value.start + distanceFromStart)..(it.value.last + distanceFromEnd)
                            intersection to targetRange
                        } else {
                            null
                        }
                    }.forEach {
                        recursiveSlivers(seedRange, it.second, step + 1)
                    }
                }
            }
            for (seed in seeds) {
                recursiveSlivers(seed, seed, 0)
            }
        }
        result.forEach { println(it) }
        return result.minBy { it.second.first }

//        val almanac = parsed.second.let { almanac ->
//            almanac.map { maps ->
//                maps.map { (source, target, range) ->
//                    (source until source + range) to (target until target + range)
//                }
//            }
//        }
//        val seeds = parsed.first.chunked(2).map { (seed, range) -> seed until seed + range }
// //        val totalAlmanac = listOf(seeds.map { it to it }) + almanac
//        val result = buildList<Pair<LongRange, LongRange>> {
//            fun recursiveSlivers(seedRange: LongRange, range: LongRange, step: Int) {
//                if (step > almanac.size) {
//                    add(seedRange to range)
//                } else {
//                    val potentialTargets = almanac[step]
//                    potentialTargets.mapNotNull {
//                        val intersection = it.first.rangeIntersect(range)
//                        if (!intersection.isEmpty()) {
//                            val distanceFromStart = intersection.start - it.first.start
//                            val distanceFromEnd = intersection.last - it.first.last
//                            val targetRange =
//                                (it.second.start + distanceFromStart) until (it.second.last + distanceFromEnd)
//                            intersection to targetRange
//                        } else {
//                            null
//                        }
//                    }.sortedBy { it.first.first }
// //                        .zipWithNext { a, b ->
// //                            Triple(
// //                                a,
// //                                a.first.last until b.first.first to (a.first.last until b.first.first),
// //                                b,
// //                            )
// //                        }
// //                        .flatMap { listOf(it.first, it.second, it.third) }
// //                        .distinct()
//                        .forEach { recursiveSlivers(seedRange, it.second, step + 1) }
//                }
//            }
//            for (seed in seeds) {
//                recursiveSlivers(seed, seed, 0)
//            }
//        }
//        return result.minBy { it.second.first }.second.first
    }

    fun LongRange.rangeIntersect(other: LongRange) =
        kotlin.math.max(first, other.first)..(kotlin.math.min(last, other.last))
}
