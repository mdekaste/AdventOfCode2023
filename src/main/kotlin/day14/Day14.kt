package day14

import Challenge
import Point

fun main() {
    // Day14.part1().let(::println)
    Day14.part2().let(::println)
}

object Day14 : Challenge() {
    val parsed get() = buildMap {
        input.lines().forEachIndexed { y, s ->
            s.forEachIndexed { x, c ->
                put(y to x, Node(y to x, c, this))
            }
        }
    }
    override fun part1(): Any? {
        val boulders = parsed.values.filter { it.currentItem == 'O' }
        boulders.forEach { b ->
            b.moveUp()
        }
        val max = parsed.keys.maxOf { it.first } + 1
        print()
        return parsed.values.filter { it.currentItem == 'O' }.map { max - it.pos.first }.sum()
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
        var curBoulders = parsed.toMap()
        val memory = LinkedHashSet<List<Pair<Point, Char>>>().apply { add(curBoulders.toKey()) }
        var index = 0
        while(true){
            curBoulders = curBoulders.toSortedMap(compareBy(Point::first, Point::second))
            curBoulders.values.filter { it.currentItem == 'O' }.forEach { it.moveUp() }
            curBoulders = curBoulders.toSortedMap(compareBy(Point::second).thenBy(Point::first))
            curBoulders.values.filter { it.currentItem == 'O' }.forEach { it.moveLeft() }
            curBoulders = curBoulders.toSortedMap(compareByDescending(Point::first).thenBy(Point::second))
            curBoulders.values.filter { it.currentItem == 'O' }.forEach { it.moveDown() }
            curBoulders = curBoulders.toSortedMap(compareByDescending(Point::second).thenBy(Point::first))
            curBoulders.values.filter { it.currentItem == 'O' }.forEach { it.moveRight() }
            val key = curBoulders.toKey()
            if (!memory.add(key)) {
                val indexOf = memory.indexOf(key)
                val curIndex = index
                val growth = curIndex - indexOf + 1
                val toCheck = (1000000000 - curIndex) % growth
                val result = memory.withIndex().first { it.index == toCheck + indexOf - 1 }.value
                val max = parsed.keys.maxOf { it.first } + 1
                return result.filter { it.second == 'O' }.sumOf { max - it.first.first }
            }
            index++
        }
        error("")
    }

    private fun Map<Point, Node>.toKey() = entries.map { it.key to it.value.currentItem }

    class Node(
        val pos: Point,
        var currentItem: Char,
        map: Map<Point, Node>,
    ) {
        val north by lazy { map[pos + NORTH] }
        val east by lazy { map[pos + EAST] }
        val south by lazy { map[pos + SOUTH] }
        val west by lazy { map[pos + WEST] }
        fun moveUp() {
            when (val item = north?.currentItem) {
                null, 'O', '#' -> return
                else -> {
                    north!!.currentItem = 'O'
                    currentItem = '.'
                    north!!.moveUp()
                }
            }
        }
        fun moveRight() {
            when (val itemNorth = east?.currentItem) {
                null, 'O', '#' -> return
                else -> {
                    east!!.currentItem = 'O'
                    currentItem = '.'
                    east!!.moveRight()
                }
            }
        }
        fun moveDown() {
            when (val itemNorth = south?.currentItem) {
                null, 'O', '#' -> return
                else -> {
                    south!!.currentItem = 'O'
                    currentItem = '.'
                    south!!.moveDown()
                }
            }
        }
        fun moveLeft() {
            when (val itemNorth = west?.currentItem) {
                null, 'O', '#' -> return
                else -> {
                    west!!.currentItem = 'O'
                    currentItem = '.'
                    west!!.moveLeft()
                }
            }
        }
    }
}
