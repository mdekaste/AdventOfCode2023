package day21

import Challenge
import Point
import cardinals
import java.lang.Math.floorMod

fun main(){
    Day21.part1().let(::println)
    Day21.part2().let(::println)
}

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
        val score: Long,
    ) {
        val memoryState by lazy { currentFrontier.map { it.pq() }.toSet() }
    }

    private val states = sequence<State>{
        var state = State(0, emptySet(), setOf(startPoint), 1L)
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
            state = State(state.score, state.currentFrontier, newFrontier, state.previousScore + newFrontier.size)
        }
    }

    fun solveGeneric(depth: Int): Long {
        val memory = mutableMapOf<Set<Point>, MutableList<Long>>()
        for(state in states){
            when(memory[state.memoryState]?.size){
                3 -> break
                else -> memory[state.memoryState] = memory.getOrPut(state.memoryState){ mutableListOf() }.apply { add(state.score) }
            }
        }

        val answers = memory.values.toList()
        val indexOfCycles = answers.indexOfFirst { it.size == 3 }
        val justValues = answers.subList(0, indexOfCycles).map { it.first() }

        return when(depth){
            in 0 until indexOfCycles -> justValues[depth]
            else -> {
                val formulas = answers.subList(indexOfCycles, answers.size).map { (y1, y2, y3) -> toQuadratic(y1, y2, y3) }
                val sizeOfCycle = formulas.size
                val iteration = (depth - indexOfCycles) / sizeOfCycle + 1
                val indexInsideCycle = (depth - indexOfCycles) % sizeOfCycle
                formulas[indexInsideCycle].let { (a, b, c) -> a * iteration * iteration + b * iteration + c }
            }
        }
    }

    fun toQuadratic(y1: Long, y2: Long, y3: Long): Triple<Long, Long, Long>{
        val a = ((y3 - y2) - (y2 - y1)) / 2
        val b = (y2 - y1) - 3 * a
        val c = y1 - (a + b)
        return Triple(a, b, c)
    }

    override fun part2() = solveGeneric(26501365)

}