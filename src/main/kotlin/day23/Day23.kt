package day23

import Challenge
import EAST
import NORTH
import Point
import SOUTH
import WEST
import cardinals
import east
import minus
import north
import south
import west
import kotlin.math.max

fun main(){
    Day23.part1().let(::println)
    Day23.part2().let(::println)
}

object Day23 : Challenge(){
    val parsed = input.lines().flatMapIndexed { y, s ->
        s.mapIndexed { x, c -> y to x to c }
    }.toMap()

    private val directions = mapOf(NORTH to '^', EAST to '>', SOUTH to 'v', WEST to '<')

    val startpoint = 0 to 1
    val endpoint = parsed.maxOf { it.key.first } to parsed.maxOf { it.key.second } - 1

    override fun part1(): Any? {
        val path = buildPath(startpoint, startpoint.south())
        //val initialState = State(0 to 1, emptySet())
        // return recursive(initialState) - 1
        return 0
    }

//    override fun part1(): Any? {
//        val startpoint = 0 to 1
//        val endpoint = 140 to 139
//        var visited = mutableSetOf<Point>()
//        var frontier = mutableSetOf(startpoint)
//        var index = 0
//        while(frontier.isNotEmpty()){
//            val newFrontier = mutableSetOf<Point>()
//            for(point in frontier){
//                val char = parsed[point]
//                val canGoTo: List<Point> = when(char){
//                    null, '#' -> emptyList()
//                    '.' -> point.cardinals()
//                    '>' -> listOf(point.east())
//                    '<' -> listOf(point.west())
//                    'v' -> listOf(point.south())
//                    '^' -> listOf(point.north())
//                    else -> error("ayo artas")
//                }
//                if(canGoTo.isNotEmpty() && visited.add(point)){
//                    newFrontier.addAll(canGoTo)
//                }
//            }
//            index++
//            frontier = newFrontier
//        }
//        visited.print()
//        return index
//    }

    data class State(
        val point: Point,
        val visited: Set<Point>
    )

    val recursive = DeepRecursiveFunction<State, Long>{ (point, visited) ->
        if(point in visited){
            return@DeepRecursiveFunction 0L
        }
        if(point == endpoint){
            return@DeepRecursiveFunction 1L
        }
        val char = parsed[point]
        val canGoTo: List<Point> = when(char){
            null, '#' -> emptyList()
            '.' -> point.cardinals()
            '>' -> listOf(point.east())
            '<' -> listOf(point.west())
            'v' -> listOf(point.south())
            '^' -> listOf(point.north())
            else -> error("ayo artas")
        }
        1 + (canGoTo.maxOfOrNull { callRecursive(State(it, visited + point)) } ?: 0L)
    }

    val recursive2 = DeepRecursiveFunction<State, Long>{ (point, visited) ->
        if(point in visited){
            return@DeepRecursiveFunction 0L
        }
        if(point == endpoint){
            return@DeepRecursiveFunction 1L
        }
        val char = parsed[point]
        val canGoTo: List<Point> = when(char){
            null, '#' -> emptyList()
            '.', '>', '<', 'v', '^' -> point.cardinals()
            else -> error("ayo artas")
        }
        1 + (canGoTo.maxOfOrNull { callRecursive(State(it, visited + point)) } ?: 0L)
    }

    fun Set<Point>.print(){
        for(y in 0 .. parsed.maxOf { it.key.first }){
            for(x in 0 .. parsed.maxOf { it.key.second }){
                if(y to x in this){
                    print('O')
                } else {
                    print(parsed.getValue(y to x))
                }
            }
            println()
        }
    }

    val graph: Map<Point, Map<Point, Int>> = buildMap<Point, MutableMap<Point, Int>> {
        fun walk(point: Point, visited: MutableSet<Point>){
            var curPoint: Point? = point
            while(curPoint != null){
                if(curPoint == endpoint){
                    val size = visited.size
                    val from = visited.first()
                    getOrPut(from){ mutableMapOf() }[curPoint] = visited.size
                    getOrPut(curPoint){ mutableMapOf() }[from] = visited.size
                    break
                }
                if(curPoint in keys){
                    val directions = getValue(curPoint)
                    val from = visited.first()
                    if(directions.containsKey(from)){
                        if(directions.getValue(from) > visited.size){
                            println("omg")
                            break
                        }
                        if(directions.getValue(from) == visited.size){
                            break
                        }
                    }
                }
                val nextPoints = curPoint.cardinals().filter { c ->
                    c !in visited && parsed[c] in setOf('.', '>', '<', '>', 'v')
                }
                if(nextPoints.size >= 2){
                    val size = visited.size
                    val from = visited.first()
                    getOrPut(from){ mutableMapOf() }[curPoint] = max(visited.size, get(from)?.get(curPoint) ?: 0)
                    getOrPut(curPoint){ mutableMapOf() }[from] = max(visited.size, get(curPoint)?.get(from) ?: 0)
                    nextPoints.forEach {
                        walk(it, mutableSetOf(curPoint!!))
                    }
                    break
                }
                visited.add(curPoint)
                curPoint = nextPoints.firstOrNull()
            }
        }
        walk(startpoint.south(), mutableSetOf(startpoint))
    }

    val graph2 = buildMap<Point, MutableMap<Point, Path>> {
        var paths = setOfNotNull(buildPath(startpoint, startpoint.south()))
        while(paths.isNotEmpty()){
            val newPaths = mutableSetOf<Path>()
            for(path in paths){
                getOrPut(path.source){ mutableMapOf() }[path.prev] = path
                path.cur.forEach { p ->
                    buildPath(path.prev, p)?.also {
                        if(!containsKey(it.source)){
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


    override fun part2() = with(mutableSetOf(startpoint)) { dfs(startpoint) }

    private fun MutableSet<Point>.dfs(key: Point): Int? = when (key) {
        endpoint -> 0
        else -> graph2.getValue(key).maxOfWithOrNull(nullsFirst()) { (to, length) ->
            if (add(to)) {
                dfs(to)?.plus(length.length).also { remove(to) }
            } else {
                null
            }
        }
    }
}