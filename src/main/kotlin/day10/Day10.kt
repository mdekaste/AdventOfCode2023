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
        input.lines().forEachIndexed { y, line ->
            line.forEachIndexed { x, _ ->
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
        val enclosedLoop = findEnclosedLoop()
        val allowedTiles = all - enclosedLoop
        val candidates = enclosedLoop.flatMap { it.neighbours() }.distinct().toMutableList()
        val enclosed: MutableSet<Point> = mutableSetOf()
        while (candidates.isNotEmpty()) {
            val (points, isEnclosed) = floodFill(allowedTiles, candidates.removeFirst(), all)
            if (isEnclosed) {
                enclosed += points
            }
            candidates -= points
        }
        printGraph(enclosedLoop.map { it to 1 }.toMap() + enclosed.map { it to 'I' })
        return enclosed.size
    }

    fun findEnclosedLoop(): Set<Point> {
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
        return visited.keys
    }

    fun floodFill(allowedTiles: Set<Point>, startPoint: Point, allTiles: Set<Point>): Pair<Set<Point>, Boolean> {
        if(startPoint !in allowedTiles){
            return setOf(startPoint) to false
        }
        var isEnclosed = true
        val visited = mutableSetOf(startPoint)
        val nextCandidates = startPoint.neighbours().toMutableList()
        while (nextCandidates.isNotEmpty()) {
            when (val candidate = nextCandidates.removeFirst()) {
                !in allTiles -> isEnclosed = false
                in allowedTiles -> if (visited.add(candidate)) {
                    nextCandidates.addAll(candidate.neighbours())
                }
            }
        }
        ///printGraph(allowedTiles.map { it to 'X' }.toMap())
        return visited to isEnclosed
    }

    fun Point.neighbours() = listOf(north, east, south, west).map { it + this }
}
