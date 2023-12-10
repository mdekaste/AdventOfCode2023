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

    var startPoint: Point = 0 to 0
    operator fun Point.plus(other: Point) = first + other.first to second + other.second

    val graph: Map<Point, List<Point>>

    init {
        graph = buildMap {
            input.lines().forEachIndexed { y, line ->
                line.forEachIndexed { x, c ->
                    put(y to x, c)
                }
            }
        }.let {
            buildMap<Point, List<Point>> {
                it.entries.forEach { (point, c) ->
                    when (c) {
                        '|' -> put(point, listOf(north + point, south + point))
                        '-' -> put(point, listOf(west + point, east + point))
                        '7' -> put(point, listOf(south + point, west + point))
                        'J' -> put(point, listOf(west + point, north + point))
                        'L' -> put(point, listOf(north + point, east + point))
                        'F' -> put(point, listOf(south + point, east + point))
                        'S' -> {
                            startPoint = point
                            put(point, listOf(north + point, east + point, south + point, west + point))
                        }
                        '.' -> put(point, listOf(point))
                    }
                }
                println(get(startPoint))
            }
        }.let { map ->
            map.mapValues { (key, value) -> value.filter { it in map.keys }.filter { point -> map.getValue(point).any { it == key } } }
        }
    }

    val parsed = input.lines()
    override fun part1(): Any? {
        val visited = mutableMapOf(startPoint to 0)
        val curCandidates = graph.getValue(startPoint).map { it to 1 }.toMutableList()
        while (curCandidates.isNotEmpty()) {
            val (curCandidate, distance) = curCandidates.removeFirst()
            if (graph.containsKey(curCandidate)) {
                if (visited.containsKey(curCandidate)) {
                    continue
                }
                visited[curCandidate] = distance
                val nextCandidates = graph.getValue(curCandidate).filter { next -> graph[next]?.any { it == curCandidate } ?: false }
                curCandidates.addAll(nextCandidates.map { it to distance + 1 })
            }
        }
        return visited.entries.maxBy { it.value }
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
                        null -> "|....|"
                        else -> "|" + value.toString().padStart(4, '.') + "|"
                    },
                )
            }
            println()
        }
    }

    fun findRoute(point: Point): Set<Point> {
        val visited = mutableSetOf(startPoint)
        buildMap {
            fun findRoute(point: Point, soFar: List<Point> = emptyList()): Set<Point>? = getOrPut(point to soFar) {
                println(soFar)
                if (point in soFar) {
                    return@getOrPut soFar.toSet()
                }
                val nextPoints = graph[point]
                if (nextPoints == null) {
                    return@getOrPut null
                }
                val previousPoint = soFar.lastOrNull()
                return@getOrPut (nextPoints - previousPoint).firstNotNullOfOrNull {
                    findRoute(
                        it as Point,
                        soFar + point,
                    )
                }
            }
            return findRoute(point, listOf(startPoint)) ?: emptySet()
        }
    }

    val all = input.lines().mapIndexed { y, s ->
        s.mapIndexed { x, _ -> y to x }
    }.flatten().toSet()

    override fun part2(): Any? {
        val loop = findLoop()
        val extendedLoop = (loop.toList() + loop.first()).map { it * 2 }.zipWithNext { a, b -> listOf(a, a.between(b)) }.flatten().distinct()
        val yMin = -2
        val yMax = input.lines().size * 2
        val xMin = -2
        val xMax = input.lines()[0].length * 2
        val fullGrid = (yMin..yMax).flatMap { y ->
            (xMin..xMax).map { x ->
                y to x
            }
        }.toSet()
        val visited = mutableSetOf(-1 to -1)
        var candidates = (-1 to -1).neighbours().toMutableList()
        while (candidates.isNotEmpty()) {
            val candidate = candidates.removeFirst()
            if (candidate !in fullGrid) {
                continue
            }
            if (candidate in extendedLoop) {
                continue
            }
            if (visited.add(candidate)) {
                candidates.addAll(candidate.neighbours())
            }
        }
        var sum = 0
        for (y in 0 until input.lines().size) {
            for (x in 0 until input.lines()[0].length) {
                val point = y * 2 to x * 2
                if (point !in visited && point !in extendedLoop) {
                    sum++
                }
            }
        }
        return sum
    }

    operator fun Point.times(amount: Int) = first * amount to second * amount

    fun findLoop(): Set<Point> {
        val direction = graph.getValue(startPoint).first()
        return generateSequence(startPoint to direction) { (p1, p2) -> p2 to (graph.getValue(p2) - p1).first() }
            .fold(setOf<Point>()) { s, (p1, p2) ->
                when (p1 in s) {
                    true -> return s
                    else -> s + p1
                }
            }
    }
    fun Point.between(other: Point) = first + (other.first - first) / 2 to second + (other.second - second) / 2

    fun Point.neighbours() = listOf(north, east, south, west).map { it + this }
}
