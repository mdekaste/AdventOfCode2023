package day15

import Challenge
import kotlin.time.measureTimedValue

fun main() {
    repeat(10000) {
        val day = Day15()
        day.part1()
        day.part2()
    }
    val (day, time) = measureTimedValue { Day15() }
    println("Reading input takes: $time")
    val (part1, timePart1) = measureTimedValue { day.part1() }
    println("Part 1 is $part1 and takes $timePart1")
    val (part2, timePart2) = measureTimedValue { day.part2() }
    println("Part 2 is $part2 and takes $timePart2")
}

class Day15 : Challenge() {
    val parsed = input.split(",")

    private fun hash(input: String): Int = input.fold(0) { hash, c -> ((hash + c.code) * 17) % 256 }
    override fun part1() = parsed.map(::hash).sum()

    override fun part2() = parsed.fold(MutableList(256) { mutableMapOf<String, Int>() }) { acc, line ->
        val (value, focalLength) = line.split("=", "-")
        if ("-" in line) acc[hash(value)] -= value else acc[hash(value)][value] = focalLength.toInt()
        acc
    }.withIndex().sumOf { (i, map) -> (i + 1) * map.values.withIndex().sumOf { (j, value) -> (j + 1) * value } }
}
