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

    override fun part1(): Any? {
        val map = mutableMapOf(0 to mutableSetOf(startPoint))
        var key = 0
        while(map.isNotEmpty()){
            val points = map.remove(key)!!
            if(key == 64){
                return points.size
            }
            points.forEach { o ->
                o.cardinals().forEach { s ->
                    if(parsed[s] != '#'){
                        map.getOrPut(key + 1){ mutableSetOf() }.apply { add(s) }
                    }
                }
            }
            key++
        }
        error("")
    }

    override fun part2() = solve(5000)

    fun solve(depth: Long = 26501365): Long {
        var frontier: Frontier = setOf(startPoint) to emptySet<Point>()
        var visited = mutableMapOf(0 to 1L).withDefault { 0L }
        val frontiers = mutableMapOf(frontier to 1L)
        var index = 0
        var indexOfRepetition = 0
        var sizeOfRepetition = 0
        var cycles: List<Pair<Pair<Set<Point>, Set<Point>>, Long>>
        var countsCycle: List<Long>

        while(true){
            val newFrontier = mutableSetOf<Point>()
            val froms = frontier.first
            for(point in froms){
                point.cardinals().forEach { newPoint ->
                    if(parsed[newPoint.pq()] != '#' && newPoint !in frontier.second){
                        newFrontier.add(newPoint)
                    }
                }
            }
            val pqFrontier = newFrontier.map { it.pq() }.toSet()
            val pqFroms = froms.map { it.pq() }.toSet()
            val newCount = visited.getValue(index - 1) + newFrontier.size
            if(frontiers.containsKey(pqFrontier to pqFroms)){
                indexOfRepetition = frontiers.keys.indexOf(pqFrontier to pqFroms)
                println(frontiers[pqFrontier to pqFroms])
                sizeOfRepetition = index.toInt() - indexOfRepetition
                countsCycle = frontiers.values.drop(indexOfRepetition) + newCount
                cycles = frontiers.entries.drop(indexOfRepetition).map { it.key to it.value }
                break
            }
            frontiers[pqFrontier to pqFroms] = newCount
            visited[++index] = newCount
            frontier = newFrontier to froms
        }
        var toCheck = depth - 1
        var additiveCycleSum = countsCycle.last() - countsCycle[0]
        val sums = countsCycle.dropLast(1).map { it - countsCycle[0] }
        val result = ((toCheck - indexOfRepetition) / sizeOfRepetition) * additiveCycleSum + countsCycle[0] + sums[((toCheck - indexOfRepetition) % sizeOfRepetition).toInt()]
        return 0L
    }
}