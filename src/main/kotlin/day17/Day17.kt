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
    private val parsed = input.lines().flatMapIndexed { y, s -> s.mapIndexed { x, c -> y to x to c.digitToInt() } }.toMap()
    private val startPoint = 0 to 0
    private val endPoint = (parsed.maxOf { it.key.first }) to (parsed.maxOf { it.key.second })

    data class State(val point: Point, val direction: Direction, val moveMemory: Int, val score: Int)

    override fun part1() = solve(0, 3)
    override fun part2() = solve(4, 10)

    fun solve(minimalForward: Int, maximumForward: Int): Int {
        val visited = mutableSetOf<Triple<Point, Direction, Int>>()
        val queue = PriorityQueue(compareBy(State::score)).apply {
            add(State(startPoint, Direction.E, 1, 0))
            add(State(startPoint, Direction.S, 1, 0))
        }
        while(queue.isNotEmpty()){
            val (point, direction, forwardCount, score) = queue.poll()
            if(!visited.add(Triple(point, direction, forwardCount))){
                continue
            }
            if(point == endPoint){
                return score
            }
            val nextPoint = point + direction.position
            val nextScore = score + (parsed[nextPoint] ?: continue)
            if(forwardCount < maximumForward){
                queue.offer(State(nextPoint, direction, forwardCount + 1, nextScore))
            }
            if(forwardCount >= minimalForward){
                (Direction.entries - direction - direction.opposite()).forEach { nextDirection ->
                    queue.offer(State(nextPoint, nextDirection, 1, nextScore))
                }
            }
        }
        error("could not determine score at end")
    }
}
