package day17

import Challenge
import Point
import day16.Direction
import java.util.*

fun main() {
    Day17.part1().let(::println)
    Day17.part2().let(::println)
}

typealias Vector = Pair<Point, Direction>
object Day17 : Challenge() {
    val parsed = input.lines().flatMapIndexed { y, s -> s.mapIndexed { x, c -> y to x to c.digitToInt() }  }.toMap()
    val startPoint = 0 to 0
    val endPoint = (parsed.maxOf { it.key.first }) to (parsed.maxOf { it.key.second })
    override fun part1(): Any? {
        val curVisited = mutableSetOf<Triple<Point, Direction, Int>>()
        val queue = PriorityQueue<State>(
            compareBy<State> { it.score }
        ).apply {
            add(State(startPoint, Direction.E, 1, 0))
            add(State(startPoint, Direction.S, 1, 0))
        }

        while(queue.isNotEmpty()){
            val (point, direction, moveMemory, score) = queue.poll()
            val mem = Triple(point, direction, moveMemory)
            if(!curVisited.add(mem)){
                continue
            }
            if(point == endPoint){
                return score
            }
            val nextPoint = (point + direction.position).takeIf {
                parsed.containsKey(it) } ?: continue
            val nextScore = score + parsed.getValue(nextPoint)
            if(moveMemory <= 2){
                queue.offer(State(nextPoint, direction, moveMemory + 1, nextScore))
            }
            (Direction.entries - direction - direction.opposite()).forEach { nextDirection ->
                queue.offer(State(nextPoint, nextDirection, 1, nextScore))
            }
        }
        error("should not happen")
    }

    fun print(visited: Map<Vector, Int>, graph: Map<Point, Int>){
        val best = visited.entries.groupBy( { it.key.first }, { it.value }).mapValues { it.value.min().toString() }
        val widthOfScore = visited.maxOf { it.value.toString().length } + 1
        for(y in 0..endPoint.first){
            for(x in 0..endPoint.second){
                print(best[y to x]?.padStart(widthOfScore, ' '))
            }
            println()
        }
    }

    data class State(
        val point: Point,
        val direction: Direction,
        val moveMemory: Int,
        val score: Int
    )

    override fun part2(): Any? {
        val curVisited = mutableSetOf<Triple<Point, Direction, Int>>()
        val queue = PriorityQueue<State>(
            compareBy<State> { it.score }
        ).apply {
            add(State(startPoint, Direction.E, 1, 0))
            add(State(startPoint, Direction.S, 1, 0))
        }

        while(queue.isNotEmpty()){
            val (point, direction, moveMemory, score) = queue.poll()
            val mem = Triple(point, direction, moveMemory)
            if(!curVisited.add(mem)){
                continue
            }
            if(point == endPoint){
                return score
            }
            val nextPoint = (point + direction.position).takeIf {
                parsed.containsKey(it) } ?: continue
            val nextScore = score + parsed.getValue(nextPoint)
            if(moveMemory < 4){
                queue.offer(State(nextPoint, direction, moveMemory + 1, nextScore))
            } else {
                if(moveMemory < 10){
                    queue.offer(State(nextPoint, direction, moveMemory + 1, nextScore))
                }
                (Direction.entries - direction - direction.opposite()).forEach { nextDirection ->
                    queue.offer(State(nextPoint, nextDirection, 1, nextScore))
                }
            }
        }
        error("should not happen")
    }

}
