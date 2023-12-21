package day21

import Challenge
import Point
import cardinals
import java.lang.Math.floorMod

fun main(){
    Day21.part1().let(::println)
    Day21.part2().let(::println)
}

typealias Frontier = Pair<Set<Point>, Set<Point>>
typealias PointCount = Map<Point, Long>
object Day21 : Challenge() {
    val parsed = input.lines().flatMapIndexed { y, c ->
        c.mapIndexed { x, c -> y to x to c }
    }.toMap()
    val startPoint = parsed.entries.first { it.value == 'S' }.key
    val height = parsed.maxOf { it.key.first } + 1
    val width = parsed.maxOf{ it.key.second } + 1

    fun Point.pq() = floorMod(first, height) to floorMod(second, width)

    override fun part1(): Int {
        val map = mutableMapOf(0 to mutableSetOf(startPoint))
        var key = 0
        while(map.isNotEmpty()){
            val points = map.remove(key)!!
            points.forEach { o ->
                o.cardinals().forEach { s ->
                    if(parsed[s.pq()] != '#'){
                        map.getOrPut(key + 1){ mutableSetOf() }.apply { add(s.pq()) }
                    }
                }
            }
            key++
        }
    }

    override fun part2() = solve(26501365)

    fun solve(depth: Long = 26501365): Long {
        var frontier: Frontier = setOf(startPoint) to emptySet<Point>()
        var visited = mutableMapOf(0 to 1L).withDefault { 0L }
        val allPoints = mutableSetOf(startPoint)
        val counterOfPqPoints = mutableMapOf(startPoint to 1L).withDefault { 0L }
        val frontiers = mutableMapOf(frontier to (0L to counterOfPqPoints.toMap()))
        var index = 0
        var x0 = 0L
        var x1 = 1L
        var x2 = 2L
        var indexOfRepetition = 0
        var sizeOfRepetition = 0
        var cycles:
                List<Pair<Frontier /* = Pair<Set<Point /* = Pair<Int, Int> */>, Set<Point /* = Pair<Int, Int> */>> */, Pair<Long, Map<Pair<Int, Int>, Long>>>>
        var countsCycle: List<Pair<Long, Map<Point, Long>>>

        while(true){
            val newFrontier = mutableSetOf<Point>()
            val froms = frontier.first
            for(point in froms){
                point.cardinals().forEach { newPoint ->
                    if(parsed[newPoint.pq()] != '#' && newPoint !in frontier.second){
                        newFrontier.add(newPoint)
                        if(allPoints.add(newPoint)){
                            counterOfPqPoints[newPoint.pq()] = counterOfPqPoints.getValue(newPoint.pq()) + 1
                        }
                    }
                }
            }
            val pqFrontier = newFrontier.map { it.pq() }.toSet()
            val pqFroms = froms.map { it.pq() }.toSet()
            val newCount: Long = visited.getValue(index - 1) + newFrontier.size
            index++
            if(index == 65){
                x0 = newCount
            } else if(index == 65 + 131){
                x1 = newCount
            } else if(index == 65 + 131 + 131){
                x2 = newCount
                break;
            }

            frontiers[pqFrontier to pqFroms] = newCount to counterOfPqPoints.toMap()
            visited[index] = newCount
            frontier = newFrontier to froms
        }
//        var toCheck = depth - 1
//        var additiveCycleSum = countsCycle.last().first - countsCycle[0].first
//        val sums = countsCycle.dropLast(1).map { it.first - countsCycle[0].first }
//        val result = ((toCheck - indexOfRepetition) / sizeOfRepetition) * additiveCycleSum + countsCycle[0].first + sums[((toCheck - indexOfRepetition) % sizeOfRepetition).toInt()]
        return 0L
    }
//
//    class State(
//        val currentFrontier: Set<Point>,
//        val previousFrontier: Set<Point>,
//    ){
//        val visitationState = currentFrontier.toPq() to previousFrontier.toPq()
//        lateinit var sumOfPuPoints: Map<Point, Int>
//    }
//
//    data class Work(
//        val state: State,
//        val work: List<List<Set<Point>>>
//    )
//
//    fun solve2(){
//        val map = parsed.mapNotNull { (key, value) -> key.takeIf { value != '#' } }.associateWith { mutableSetOf<Point>() }
//        map.getValue(startPoint).add(startPoint)
//
//        var previousFrontier = emptySet<Point>()
//        var currentFrontier = setOf(startPoint)
//        val frontiers = mutableMapOf(currentFrontier)
//        while(currentFrontier.isNotEmpty()){
//            val newFrontier = mutableSetOf<Point>()
//            for(point in currentFrontier){
//                for(neighbour in point.cardinals()){
//                    if(neighbour !in previousFrontier && parsed[neighbour.pq()] != '#'){
//                        val (oPoint, dimension) = PqPoint(neighbour)
//                        map[oPoint.first][oPoint.second].add(dimension)
//                        newFrontier.add(neighbour)
//                    }
//                }
//            }
//            if(!frontiers.add(newFrontier.toPq())){
//                val maxWidth = map.maxOf { it.maxOf { it.size.toString().length } }
//                for(y in 0 until height){
//                    for(x in 0 until width){
//                        print(map[y][x].size.toString().padStart(maxWidth + 1, ' '))
//                    }
//                    println()
//                }
//                break
//            }
//            previousFrontier = currentFrontier
//            currentFrontier = newFrontier
//        }
//    }
//
//    fun Set<Point>.toPq() = mapTo(mutableSetOf()){ it.pq() }
//
//    data class PqPoint(
//        val point: Point,
//        val dimension: Point
//    ){
//        constructor(point: Point) : this(point.pq(), point.first / height to point.second / width)
//    }
}