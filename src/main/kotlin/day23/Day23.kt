package day23

import Challenge
import Point
import cardinals
import east
import north
import south
import west

fun main(){
    Day23.part1().let(::println)
    Day23.part2().let(::println)
}

object Day23 : Challenge(){
    val parsed = input.lines().flatMapIndexed { y, s ->
        s.mapIndexed { x, c -> y to x to c }
    }.toMap()

    val startpoint = 0 to 1
    val endpoint = parsed.maxOf { it.key.first } to parsed.maxOf { it.key.second } - 1

    override fun part1(): Any? {
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
                    getOrPut(from){ mutableMapOf() }[curPoint] = visited.size
                    getOrPut(curPoint){ mutableMapOf() }[from] = visited.size
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

    override fun part2(): Any? {
        println("calculating max distance...")
//        graph {
//            for ((root, to) in graph){
//                to.map { Triple(root, it.key, it.value) }.forEach { (from, to, weight) ->
//                    "y${from.first}x${from.second}" - "y${to.first}x${to.second}" + { label = weight.toString() }
//                }
//            }
//        }.let { println(it.dot()) }
        return recursiveWalk(startpoint, 0, setOf(startpoint), graph)
    }

    private fun recursiveWalk(key: Point, lengthTo: Int, visited: Set<Point>, graph: Map<Point, Map<Point, Int>>): Int {
        if (key == endpoint) {
            return lengthTo
        }
        val optionsAt = graph.getValue(key)
        return optionsAt.filter { (to, _) -> to !in visited }.map { (to, count) ->
            lengthTo + recursiveWalk(to, count, visited + to, graph)
        }.maxByOrNull { it } ?: 0
    }
}