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

    override fun part2(): Any? {
        val nodes = parsed.values
        var boulderLocations = nodes.filter { it.currentItem == 'O' }.map { it.pos }
        var memory = mutableSetOf(boulderLocations)
        var index = 0
        while (true) {
            Direction.entries.forEach { direction ->
                boulderLocations = boulderLocations
                    .sortedWith(direction.sortDirection)
                    .map(parsed::getValue)
                    .map { it.moveBoulder(direction) }
            }
            if (!memory.add(boulderLocations)) {
                val indexOf = memory.indexOf(boulderLocations)
                val curIndex = index
                val growth = curIndex - indexOf + 1
                val toCheck = (1000000000 - curIndex) % growth
                val result = memory.withIndex().first { it.index == toCheck + indexOf - 1 }.value
                val max = parsed.keys.maxOf { it.first } + 1
                println(memory.map { it.sumOf { max - it.first } })
                return result.sumOf { max - it.first }
            }
            index++
        }
        error("")
    }

    enum class Direction(val direction: Point, val sortDirection: Comparator<Point>) {
        NORTH(-1 to 0, compareBy(Point::first, Point::second)),
        WEST(0 to -1, compareBy(Point::second, Point::first)),
        SOUTH(1 to 0, compareByDescending(Point::first).thenBy(Point::second)),
        EAST(0 to 1, compareByDescending(Point::second).thenBy(Point::first)),
    }

    class Node(
        val pos: Point,
        var currentItem: Char,
        map: Map<Point, Node>,
    ) {
        val neighbours by lazy { Direction.entries.associateBy({ it }, { map[pos + it.direction] }) }

        fun moveBoulder(dir: Direction): Point {
            val node = neighbours[dir]
            return when (node?.currentItem) {
                null, 'O', '#' -> pos
                else -> {
                    node.currentItem = 'O'
                    currentItem = '.'
                    node.moveBoulder(dir)
                }
            }
        }
    }
}
