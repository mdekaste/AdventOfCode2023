package day23

import Challenge
import EAST
import NORTH
import Point
import SOUTH
import WEST
import cardinals
import minus
import south

fun main() {
    Day23.part1().let(::println)
    Day23.part2().let(::println)
}

object Day23 : Challenge() {
    val parsed = input.lines().flatMapIndexed { y, s ->
        s.mapIndexed { x, c -> y to x to c }
    }.toMap()

    private val directions = mapOf(NORTH to '^', EAST to '>', SOUTH to 'v', WEST to '<')

    val startpoint = 0 to 1
    val endpoint = parsed.maxOf { it.key.first } to parsed.maxOf { it.key.second } - 1

    override fun part1() = solve(true)

    val graph = buildMap<Point, MutableMap<Point, Path>> {
        var paths = setOfNotNull(buildPath(startpoint, startpoint.south()))
        while (paths.isNotEmpty()) {
            val newPaths = mutableSetOf<Path>()
            for (path in paths) {
                getOrPut(path.source) { mutableMapOf() }[path.prev] = path
                path.cur.forEach { p ->
                    buildPath(path.prev, p)?.also {
                        if (!containsKey(it.source)) {
                            newPaths.add(it)
                        }
                    }
                }
            }
            paths = newPaths
        }
    }

    data class Path(val source: Point, val length: Int = 0, val blocked: Boolean = false, val prev: Point, val cur: List<Point>)

    private fun buildPath(from: Point, direction: Point) =
        generateSequence(Path(source = from, prev = from, cur = listOf(direction))) { (source, length, blocked, prev, cur) ->
            cur.singleOrNull()?.let { next ->
                Path(
                    source = source,
                    length = length + 1,
                    blocked = blocked || blocked(prev - next, prev),
                    prev = next,
                    cur = next.cardinals().filter { it != prev }.filter { parsed[it] !in setOf('#', null) }
                )
            }
        }.last().takeIf { it.prev == endpoint || it.cur.isNotEmpty() }


    private fun blocked(direction: Point, point: Point) = directions[direction] == parsed[point]


    override fun part2() = solve(false)

    private fun solve(directed: Boolean = false) = with(mutableSetOf(startpoint)) {
        dfs(startpoint,
            if (directed) {
                { !it.blocked }
            } else {
                { true }
            }
        )
    }

    private fun MutableSet<Point>.dfs(key: Point, filter: (Path) -> Boolean): Int? = when (key) {
        endpoint -> 0
        else -> graph.getValue(key).maxOfWithOrNull (nullsFirst()) { (to, length) ->
            if (filter(length) && add(to)) {
                dfs(to, filter)?.plus(length.length).also { remove(to) }
            } else {
                null
            }
        }
    }
}