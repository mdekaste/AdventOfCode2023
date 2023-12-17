package day17

import Challenge
import EAST
import Point
import SOUTH
import perpendicular
import plus
import java.util.*

fun main() {
    Day17.part1().let(::println)
    Day17.part2().let(::println)
    Day17.solve().let(::println)
}

object Day17 : Challenge() {
    private val parsed = input.lines()
        .flatMapIndexed { y, s -> s.mapIndexed { x, c -> y to x to c.digitToInt() } }
        .toMap()
    private val startPoint = 0 to 0
    private val endPoint = (parsed.maxOf { it.key.first }) to (parsed.maxOf { it.key.second })

    data class State(
        val point: Point = startPoint,
        val direction: Point,
        val moveMemory: Int = 1,
        val score: Int = 0
    )

    override fun part1() = solve(0, 3)
    override fun part2() = solve(4, 10)

    fun solve(minimalForward: Int, maximumForward: Int): Int {
        val visited = mutableSetOf(
            Triple(startPoint, EAST, 1),
            Triple(startPoint, SOUTH, 1)
        )
        val queue = PriorityQueue(compareBy(State::score, State::moveMemory)).apply {
            add(State(startPoint, EAST, 1, 0))
            add(State(startPoint, SOUTH, 1, 0))
        }
        fun add(state: State) {
            if (visited.add(Triple(state.point, state.direction, state.moveMemory))) {
                queue.offer(state)
            }
        }
        while (queue.isNotEmpty()) {
            val (point, direction, forwardCount, score) = queue.poll()
            val nextPoint = point + direction
            val nextScore = score + (parsed[nextPoint] ?: continue)
            if (nextPoint == endPoint) {
                return nextScore
            }
            if (forwardCount < maximumForward) {
                add(State(nextPoint, direction, forwardCount + 1, nextScore))
            }
            if (forwardCount >= minimalForward) {
                direction.perpendicular().forEach {
                    add(State(nextPoint, it, 1, nextScore))
                }
            }
        }
        error("could not determine score at end")
    }
}
