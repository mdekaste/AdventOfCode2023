package day23

import Challenge
import EAST
import NORTH
import Point
import SOUTH
import WEST
import cardinals
import io.github.rchowell.dotlin.digraph
import minus
import south

fun main() {
    Day23.part1().let(::println)
    Day23.part2().let(::println)
    Day23.solve().let(::println)
}

object Day23 : Challenge() {
    val parsed = input.lines().flatMapIndexed { y, s ->
        s.mapIndexed { x, c -> y to x to c }
    }.toMap()

    private val directions = mapOf(NORTH to '^', EAST to '>', SOUTH to 'v', WEST to '<')

    val startpoint = 0 to 1
    val endpoint = parsed.maxOf { it.key.first } to parsed.maxOf { it.key.second } - 1

    override fun part1() = solve{ !it.blocked }

    val graph = buildMap<Point, MutableMap<Point, Path>> {
        var paths = setOfNotNull(buildPath(startpoint, startpoint.south()))
        while (paths.isNotEmpty()) {
            val newPaths = mutableSetOf<Path>()
            for (path in paths) {
                getOrPut(path.source) { mutableMapOf() }[path.prev] = path
                if (!containsKey(path.prev)) {
                    path.cur.forEach { p ->
                        buildPath(path.prev, p)?.also {
                            newPaths.add(it)
                        }
                    }
                }
            }
            paths = newPaths
        }
    }

    fun Point.gs() = "y${first}x$second"

    val digraph = digraph {
        graph.forEach { (from, to) ->
            to.values.forEach {  path ->
                from.gs() - path.prev.gs() + { label = path.length.toString()}
            }
        }
    }.let { println(it.dot()) }

//    val graph2 = buildMap<Point, MutableMap<Point, Path>> {
//        fun recursive(path: Path){
//            getOrPut(path.source) { mutableMapOf() }[path.prev] = path
//            path.cur.mapNotNull { buildPath(path.prev, it) }.forEach {
//                if(get(it.source)?.containsKey(it.prev) != true){
//                    recursive(it)
//                }
//            }
//        }
//        recursive(buildPath(startpoint, startpoint.south())!!)
//    }

    data class Path(val source: Point, val length: Int = 0, val blocked: Boolean = false, val prev: Point, val cur: List<Point>)

    private fun buildPath(from: Point, direction: Point) =
        generateSequence(Path(source = from, prev = from, cur = listOf(direction))) { (source, length, blocked, prev, cur) ->
            cur.singleOrNull()?.let { next ->
                Path(
                    source = source,
                    length = length + 1,
                    blocked = blocked || directions[prev - next] == parsed[prev],
                    prev = next,
                    cur = next.cardinals().filter { it != prev && parsed[it] !in setOf('#', null) }
                )
            }
        }.last().takeIf { it.prev == endpoint || it.cur.isNotEmpty() }

    override fun part2() = solve { true }

    private fun solve(edgeFilter: (Path) -> Boolean) = with(mutableSetOf(startpoint)) {
        fun dfs(key: Point): Int? = when (key) {
            endpoint -> 0
            else -> graph.getValue(key).maxOfWithOrNull(nullsFirst()) { (to, length) ->
                if (edgeFilter(length) && add(to)) {
                    dfs(to)?.plus(length.length).also { remove(to) }
                } else {
                    null
                }
            }
        }
        dfs(startpoint)
    }
}