package day14

import Challenge
import Point

fun main() {
    // Day14.part1().let(::println)
    Day14.part2().let(::println)
}

object Day14 : Challenge() {
    val parsed = buildMap {
        input.lines().forEachIndexed { y, s ->
            s.forEachIndexed { x, c ->
                put(y to x, Node(y to x, c, this))
            }
        }
    }
    val max = parsed.keys.last().first + 1

    override fun part1(): Any? {
        return null
//        val canvas = parsed.toMutableMap()
//
//        val boulders = parsed.values.filter { it.currentItem == 'O' }
//        boulders.forEach { b ->
//            b.moveUp()
//        }
//        val max = parsed.keys.maxOf { it.first } + 1
//        print()
//        return parsed.values.filter { it.currentItem == 'O' }.map { max - it.pos.first }.sum()
    }

    fun print() {
        println("---")
        val yMax = parsed.maxOf { it.key.first }
        val xMax = parsed.maxOf { it.key.second }
        for (y in 0..yMax) {
            for (x in 0..xMax) {
                print(parsed.getValue(y to x).currentItem)
            }
            println()
        }
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

        fun stateAtIndex(index: Int): List<Node> {
            return when(index < cycleIndex){
                true -> indexes[index]
                else -> indexes.subList(cycleIndex, indexes.size).let { it[(index - cycleIndex) % it.size] }
            }
        }
    }

    override fun part2(): Any {
        return State(parsed.values.filter { it.currentItem == 'O' }).stateAtIndex(1000000000).sumOf { max - it.pos.first }
    }

    enum class Direction(val direction: Point, val sortDirection: Comparator<Node>) {
        NORTH(-1 to 0, compareBy<Node> { it.pos.first }.thenBy { it.pos.second }),
        WEST(0 to -1, compareBy<Node> { it.pos.second }.thenBy { it.pos.first }),
        SOUTH(1 to 0, compareByDescending<Node> { it.pos.first }.thenBy { it.pos.second }),
        EAST(0 to 1, compareByDescending<Node> { it.pos.second }.thenBy { it.pos.first }),
    }

    class Node(
        val pos: Point,
        var currentItem: Char,
        graph: Map<Point, Node>,
    ) {
        val neighbours by lazy { Direction.entries.associateBy({ it }, { graph[pos + it.direction] }) }

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
