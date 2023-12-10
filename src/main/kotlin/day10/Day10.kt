package day10

import Challenge

fun main() {
    Day10.part1().let(::println)
    Day10.part2().let(::println)
}

typealias Point = Pair<Int, Int>

object Day10 : Challenge() {
    val north = -1 to 0
    val south = 1 to 0
    val west = 0 to -1
    val east = 0 to 1

    val pipeTypes = mapOf(
        '|' to listOf(north, south),
        'J' to listOf(north, west),
        'L' to listOf(north, east),
        '-' to listOf(east, west),
        'F' to listOf(east, south),
        '7' to listOf(south, west),
        'S' to listOf(north, east, south, west),
        '.' to emptyList(),
    )

    operator fun Point.plus(other: Point) = first + other.first to second + other.second
    lateinit var startPipe: Point

    val pipes = buildMap<Point, List<Point>> {
        input.lines().forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                put(y to x, pipeTypes.getValue(c).map { it + (y to x) })
                if (c == 'S') {
                    startPipe = y to x
                }
            }
        }
        mapValuesTo(this) { (point, neighbours) -> neighbours.filter { p -> get(p).orEmpty().any { it == point } } }
    }

    override fun part1(): Any? {
        val loop = findLoop(startPipe)
        return loop.size / 2
    }

    fun findLoop(startPipe: Point) = buildSet {
        generateSequence(startPipe to pipes.getValue(startPipe).first()) { (p1, p2) ->
            p2 to (pipes.getValue(p2) - p1)[0]
        }.forEach {
            if (!add(it.first)) {
                return@buildSet
            }
        }
    }

    private fun Point.between(other: Point) = first + (other.first - first) / 2 to second + (other.second - second) / 2
    operator fun Point.times(amount: Int) = first * amount to second * amount

    private fun Point.neighbours() = listOf(north, east, south, west).map { it + this }

    override fun part2(): Any? {
        val loop = findLoop(startPipe)
        val extendedLoop = loop
            .let { it.toList() + it.first() }
            .map { it * 2 }
            .zipWithNext { a, b -> listOf(a, a.between(b)) }
            .flatten()
        val workAreaHeight = (-2..input.lines().size * 2)
        val workAreaWidth = (-2..input.lines()[0].length * 2)
        val startPoint = -2 to -2
        val visited = mutableSetOf<Point>(startPoint)
        val recursiveFunction = DeepRecursiveFunction<Point, Unit> {
            it.neighbours().forEach {
                if (it.first in workAreaHeight && it.second in workAreaWidth && it !in extendedLoop && visited.add(it)) {
                    callRecursive(it)
                }
            }
        }.invoke(startPoint)
        visited.associate { it to "O" }.let(::printGraph)
        var sum = 0
        for (y in 0 until input.lines().size) {
            for (x in 0 until input.lines()[0].length) {
                val point = y * 2 to x * 2
                if (point !in extendedLoop && point !in visited) {
                    sum++
                }
            }
        }
        return sum
    }
    fun printGraph(map: Map<Point, Any?>) {
        val yMin = map.minOf { it.key.first }
        val yMax = map.maxOf { it.key.first }
        val xMin = map.minOf { it.key.second }
        val xMax = map.maxOf { it.key.second }
        (yMin..yMax).forEach { y ->
            (xMin..xMax).forEach { x ->
                print(
                    when (val value = map[y to x]) {
                        null -> "I"
                        else -> " "
                    },
                )
            }
            println()
        }
    }
}

