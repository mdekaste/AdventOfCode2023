package day11

import Challenge
import helpers.cartesianProduct
import kotlin.math.abs

// ktlint-disable no-wildcard-imports

fun main() {
    Day11.part1().let(::println)
    Day11.part2().let(::println)
}

object Day11 : Challenge() {
    val parsed = input.lines().flatMapIndexed { y: Int, s: String ->
        s.mapIndexedNotNull { x, c ->
            when(c){
                '#' -> y to x
                else -> null
            }
        }
    }.toList()
    override fun part1(): Any? {
        print(parsed)
        val sortedByY = parsed.groupBy { it.first }.toSortedMap()
        val insertions = sortedByY.entries.zipWithNext { (y, _), (y2, _) -> y2 - y - 1 }.runningReduce { acc, i -> acc + i }.let { listOf(it.first()) + it + it.last() }
        val parsed2 = sortedByY.values.zip(insertions).flatMap { (nodesByY, increases) -> nodesByY.map { it.first + increases to it.second } }.toList()

        print(parsed2)
        val sortedByX = parsed2.groupBy { it.second }.toSortedMap()
        val insertions2 = sortedByX.entries.zipWithNext { (x, _), (x2, _) -> x2 - x - 1 }.runningReduce { acc, i -> acc + i }.let { listOf(it.first()) + it + it.last() }
        val parsed3 = sortedByX.values.zip(insertions2).flatMap { (nodesByX, increases) -> nodesByX.map { it.first to it.second + increases } }.toList()
        print(parsed3)

        return parsed3.cartesianProduct(parsed3).filter { it.first != it.second }.map { (first, second) -> (abs(first.first - second.first) + abs(first.second - second.second)) }.sum() / 2
    }

    fun print(points: List<Pair<Int, Int>>){
        println("---")
        for(y in 0..points.maxOf { it.first }){
            for(x in 0..points.maxOf { it.second }){
                if(y to x in points){
                    print('#')
                } else {
                    print('.')
                }
            }
            println()
        }
        println("---")
    }

    override fun part2(): Any? {
        val sortedByY = parsed.groupBy { it.first }.toSortedMap()
        val insertions = sortedByY.entries.zipWithNext { (y, _), (y2, _) -> y2 - y - 1 }.map { it * (1000000 - 1) }.runningReduce { acc, i -> acc + i }.let { listOf(it.first()) + it + it.last() }
        val parsed2 = sortedByY.values.zip(insertions).flatMap { (nodesByY, increases) -> nodesByY.map { it.first + increases to it.second } }.toList()

        val sortedByX = parsed2.groupBy { it.second }.toSortedMap()
        val insertions2 = sortedByX.entries.zipWithNext { (x, _), (x2, _) -> x2 - x - 1 }.map { it * (1000000 -1) }.runningReduce { acc, i -> acc + i }.let { listOf(it.first()) + it + it.last() }
        val parsed3 = sortedByX.values.zip(insertions2).flatMap { (nodesByX, increases) -> nodesByX.map { it.first to it.second + increases } }.toList()

        return parsed3.cartesianProduct(parsed3).filter { it.first != it.second }.map { (first, second) -> (abs(first.first - second.first).toLong() + abs(first.second - second.second)).toLong() }.sum() / 2
    }
}
