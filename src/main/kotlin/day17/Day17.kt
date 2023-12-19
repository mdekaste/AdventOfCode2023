package day17

import Challenge
import EAST
import Point
import SOUTH
import perpendicular
import plus
import times
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

    data class State(val point: Point = startPoint, val direction: Point, val forwardCount: Int = 1) {
        var score: Int = 0
        constructor(point: Point, dir: Point, forwardCount: Int, score: Int) : this(point, dir, forwardCount) {
            this.score = score
        }
    }

    override fun part1() = solve(0, 3)
    override fun part2() = solve(4, 10)

    fun solve(minimalForward: Int, maximumForward: Int): Int {
        val stateOne = State(direction = EAST)
        val stateTwo = State(direction = SOUTH)
        val visited = mutableSetOf(stateOne, stateTwo)
        val queue = PriorityQueue(compareBy(State::score, State::forwardCount)).apply {
            add(stateOne)
            add(stateTwo)
        }

        fun add(state: State) {
            if (parsed.containsKey(state.point + state.direction) && visited.add(state)) {
                queue.offer(state)
            }
        }
        while (true) {
            val state = queue.poll()
            val nextPoint = state.point + state.direction
            val nextScore = state.score + (parsed[nextPoint] ?: continue)
            if (nextPoint == endPoint && state.forwardCount >= minimalForward) {
                return nextScore
            }
            if (state.forwardCount < maximumForward) {
                add(State(nextPoint, state.direction, state.forwardCount + 1, nextScore))
            }
            if (state.forwardCount >= minimalForward) {
                state.direction.perpendicular().forEach {
                    if(parsed.containsKey(state.point + it * minimalForward)) {
                        add(State(nextPoint, it, 1, nextScore))
                    }
                }
            }
        }
    }
}
