package day14

import Challenge
import Point
import java.util.EnumMap

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
        state = State(parsed.values.filter { it.currentItem is Item.Boulder })
        yMax = parsed.keys.last().first + 1
    }

    class State(initial: List<Node>) {
        val boulders = initial.associateBy({ it.currentItem as Item.Boulder }, { Cycle(mutableSetOf(it.pos)) })

        init {
            var boulderLocations = initial
            while (true) {
                Direction.entries.forEach { direction ->
                    boulderLocations = boulderLocations
                        .sortedWith(direction.sortDirection)
                        .map { it.moveBoulder(direction) }
                }
                var allTrue: Boolean = true
                for (boulderNode in boulderLocations) {
                    val boulder = boulderNode.currentItem as Item.Boulder
                    if (!boulders.getValue(boulder).add(boulderNode.pos)) {
                        allTrue = false
                    }
                }
                if (allTrue) {
                    break
                }
            }
        }

        fun stateAtIndex(index: Int): List<Point> = boulders.values.map { it.get(index) }

        data class Cycle(
            val set: MutableSet<Point>,
            var cycleIndex: Int = 0,
        ) {
            fun add(boulder: Point): Boolean {
                if (!set.add(boulder)) {
                    if (cycleIndex != 0) {
                        cycleIndex = set.indexOf(boulder)
                    }
                    return true
                }
                return false
            }
            fun get(index: Int): Point = when (index < cycleIndex) {
                true -> set.toList()[index]
                else -> set.toList().subList(cycleIndex, set.size).let { it[(index - cycleIndex) % it.size] }
            }
        }
    }

    override fun part2() = state.stateAtIndex(1000000000).sumOf { yMax - it.first }

    enum class Direction(val direction: Point, val sortDirection: Comparator<Node>) {
        NORTH(-1 to 0, compareBy<Node> { it.pos.first }.thenBy { it.pos.second }),
        WEST(0 to -1, compareBy<Node> { it.pos.second }.thenBy { it.pos.first }),
        SOUTH(1 to 0, compareByDescending<Node> { it.pos.first }.thenBy { it.pos.second }),
        EAST(0 to 1, compareByDescending<Node> { it.pos.second }.thenBy { it.pos.first }),
    }

    class Node(val pos: Point, currentItem: Char, graph: Map<Point, Node>) {
        var currentItem = when (currentItem) {
            'O' -> Item.Boulder(pos)
            '#' -> Item.Wall
            '.' -> Item.Floor
            else -> null
        }

        private val neighbours by lazy {
            Direction.entries.associateByTo(EnumMap(Direction::class.java), { it }, { graph[pos + it.direction] })
        }

        fun moveBoulder(dir: Direction): Node {
            val node = neighbours[dir]
            return when (node?.currentItem) {
                null, Item.Wall, is Item.Boulder -> this
                else -> {
                    node.currentItem = currentItem
                    currentItem = Item.Floor
                    node.moveBoulder(dir)
                }
            }
        }
    }

    sealed interface Item {
        data object Wall : Item
        data object Floor : Item
        data class Boulder(val originalPos: Point) : Item
    }
}
