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

    override fun part2() = solve(50)

    fun solve(depth: Long = 26501365): Long {
        var frontier: Frontier = setOf(startPoint) to emptySet<Point>()
        var visited = mutableSetOf(startPoint)
        val frontiers: MutableMap<Frontier, Set<Point>> = mutableMapOf(frontier to visited.toSet())
        var index = 0L
        var indexOfRepetition = 0
        var sizeOfRepetition = 0
        var sumAtRepetitionIndex = 0L
        var sumAtEnd = 0L
        while(true){
            val newFrontier = mutableSetOf<Point>()
            val froms = frontier.first
            for(point in frontier.first){
                point.cardinals().forEach { newPoint ->
                    if(parsed[newPoint.pq()] != '#' && newPoint !in frontier.second){
                        newFrontier.add(newPoint)
                    }
                }
            }
            visited += newFrontier
            val pqFrontier = newFrontier.map { it.pq() }.toSet()
            val pqFroms = froms.map { it.pq() }.toSet()
            if(frontiers.containsKey(pqFrontier to pqFroms)){
                indexOfRepetition = frontiers.keys.indexOf(pqFrontier to pqFroms)
                sizeOfRepetition = frontiers.size + 1 - indexOfRepetition
                sumAtRepetitionIndex = frontiers.getValue(pqFrontier to pqFroms).size.toLong()
                sumAtEnd = visited.size.toLong()
                break
            } else {
                frontiers[pqFrontier to pqFroms] = visited.toSet()
            }
            index++
            frontier = newFrontier to froms
        }
        return 0L
    }
}