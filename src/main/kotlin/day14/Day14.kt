package day14

import Challenge
import Point
import plus
import java.util.*

fun main() {
    // Day14.part1().let(::println)
    Day14.part2().let(::println)
}

object Day14 : Challenge() {
    override fun part1(): Any? {
        return null
    }

    val state: State
    val yMax: Int

    init {
        val parsed = buildMap {
            input.lines().forEachIndexed { y, s ->
                s.forEachIndexed { x, c ->
                    put(y to x, Node(y to x, c, this))
                }
            }
        }
        state = State(parsed.values.filter { it.currentItem == 'O' })
        yMax = parsed.keys.last().first + 1
    }

    class State(initial: List<Node>) {
        val indexes = mutableListOf(initial)
        val memory = mutableSetOf(initial.map(Node::pos))
        var cycleIndex: Int = 0

        init {
            while (true) {
                var boulderLocations = indexes.last()
                Direction.entries.forEach { direction ->
                    boulderLocations = boulderLocations
                        .sortedWith(direction.sortDirection)
                        .map { it.moveBoulder(direction) }
                }
                if (!memory.add(boulderLocations.map(Node::pos))) {
                    cycleIndex = indexes.indexOf(boulderLocations)
                    break
                }
                indexes.add(boulderLocations)
            }
        }

        fun stateAtIndex(index: Int) = when (index < cycleIndex) {
            true -> indexes[index]
            else -> indexes.subList(cycleIndex, indexes.size).let { it[(index - cycleIndex) % it.size] }
        }
    }

    override fun part2() = state.stateAtIndex(1000000000).sumOf { yMax - it.pos.first }

    enum class Direction(val direction: Point, val sortDirection: Comparator<Node>) {
        NORTH(-1 to 0, compareBy<Node> { it.pos.first }.thenBy { it.pos.second }),
        WEST(0 to -1, compareBy<Node> { it.pos.second }.thenBy { it.pos.first }),
        SOUTH(1 to 0, compareByDescending<Node> { it.pos.first }.thenBy { it.pos.second }),
        EAST(0 to 1, compareByDescending<Node> { it.pos.second }.thenBy { it.pos.first }),
    }

    class Node(val pos: Point, var currentItem: Char, graph: Map<Point, Node>) {
        private val neighbours by lazy {
            Direction.entries.associateByTo(EnumMap(Direction::class.java), { it }, { graph[pos.plus(it.direction)] })
        }

        fun moveBoulder(dir: Direction): Node {
            val node = neighbours[dir]
            return when (node?.currentItem) {
                null, 'O', '#' -> this
                else -> {
                    node.currentItem = 'O'
                    currentItem = '.'
                    node.moveBoulder(dir)
                }
            }
        }
    }
}
