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

    enum class PipeType(val char: Char, vararg val directions: Point) {
        NORTH_SOUTH('|', north, south),
        NORTH_WEST('J', north, west),
        NORTH_EAST('L', north, east),
        EAST_WEST('-', east, west),
        EAST_SOUTH('F', east, south),
        SOUTH_WEST('7', south, west),
        ALL('S', north, east, south, west),
        NONE('.'),
    }

    data class Pipe(
        val position: Point,
        val pipeType: PipeType,
    ) {
        lateinit var neighbours: List<Pipe>
    }

    operator fun Point.plus(other: Point) = first + other.first to second + other.second

    val pipes = buildMap {
        input.lines().forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                put(y to x, PipeType.entries.first { it.char == c })
            }
        }
    }.mapValues { (point, pipe) -> Pipe(point, pipe) }
        .apply {
            onEach { (point, pipe) ->
                pipe.neighbours = pipe.pipeType.directions
                    .map { it + point }
                    .mapNotNull(::get)
                    .filter { it.pipeType.directions.map { d -> d + it.position }.any { d -> d == pipe.position } }
            }
        }

    val startPipe = pipes.values.first { it.pipeType == PipeType.ALL }
    override fun part1(): Any? {
        val loop = findLoop(startPipe)
        return loop.size / 2
    }

    fun findLoop(startPipe: Pipe) = buildSet {
        generateSequence(startPipe to startPipe.neighbours.first()) { (p1, p2) ->
            p2 to (p2.neighbours - p1)[0]
        }.forEach {
            if (!add(it.first)) {
                return@buildSet
            }
        }
    }

    fun Point.between(other: Point) = first + (other.first - first) / 2 to second + (other.second - second) / 2
    operator fun Point.times(amount: Int) = first * amount to second * amount

    fun Point.neighbours() = listOf(north, east, south, west).map { it + this }

    override fun part2(): Any? {
        val loop = findLoop(startPipe)
        val extendedLoop = loop.map { it.position }
            .let { it + it.first() }
            .map { it * 2 }
            .zipWithNext { a, b -> listOf(a, a.between(b)) }
            .flatten()
        val workAreaHeight = (-2..input.lines().size * 2)
        val workAreaWidth = (-2..input.lines()[0].length * 2)
        val startPoint = -2 to -2
        val visited = mutableSetOf<Point>()
        val recursiveFunction = DeepRecursiveFunction<Point, Unit> {
            if (it.first in workAreaHeight && it.second in workAreaWidth && it !in extendedLoop && visited.add(it)) {
                it.neighbours().forEach {
                    callRecursive(it)
                }
            }
        }.invoke(startPoint)
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