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
            if(key == 64){
                return points.size
            }
            points.forEach { o ->
                o.cardinals().forEach { s ->
                    if(parsed[s.pq()] != '#'){
                        map.getOrPut(key + 1){ mutableSetOf() }.apply { add(s.pq()) }
                    }
                }
            }
            key++
        }
        error("")
    }

    data class State(
        val previousScore: Long,
        val previousFrontier: Set<Point>,
        val currentFrontier: Set<Point>,
        val score: Pair<Long, Long>,
    ) {
        fun memoryState() = currentFrontier.map { it.pq() }.toSet()
    }

    fun solveGeneric(depth: Long): Long {
        val stateSequence = sequence<State>{
            var state = State(0, emptySet(), setOf(startPoint), 0L to 1L)
            while(true){
                yield(state)
                val newFrontier = mutableSetOf<Point>()
                for(point in state.currentFrontier){
                    for(neighbour in point.cardinals()){
                        val puPoint = neighbour.pq()
                        if(parsed[puPoint] != '#' && neighbour !in state.previousFrontier){
                            newFrontier.add(neighbour)
                        }
                    }
                }
                val newScore = state.previousScore to newFrontier.size.toLong()
                state = State(state.score.first + state.score.second, state.currentFrontier, newFrontier, newScore)
            }
        }
        val memory = mutableMapOf<Set<Point>, MutableList<Pair<Long, Long>>>()
        for(state in stateSequence){
            val stateMemory = state.memoryState()
            if(memory[stateMemory]?.size == 3){
                memory.map { it.value }.forEach { println(it) }
                // do some magic wolfram alpha kekw maybe later ill fix
                break
            } else {
                memory[stateMemory] = memory.getOrPut(stateMemory){ mutableListOf<Pair<Long, Long>>() }.apply { add(state.score) }
            }
        }
        return 0L
    }

    override fun part2() = solveGeneric(100000L)

}