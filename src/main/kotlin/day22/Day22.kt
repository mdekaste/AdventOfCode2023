package day22

import Challenge
import helpers.extractLongs
import helpers.intersect

fun main() {
    Day22.part1().let(::println)
    Day22.part2().let(::println)
}

typealias XYZCoord = List<Int>

private operator fun <E> List<E>.component6() = get(5)

object Day22 : Challenge() {
    val bricks = input.lines().mapIndexed { index, s ->
        s.extractLongs().let { (x1, y1, z1, x2, y2, z2) -> Brick(z1..z2, y1..y2, x1..x2) }
    }.sortedWith(compareBy { it.zRange.first })

    val settled = buildMap {
        for (brick in bricks) {
            brick.settleDown()
        }
    }

    override fun part1(): Any? {
        val fallenBricks = settled.toMutableMap()
        val cannotRemove = fallenBricks.mapNotNull { it.value.singleOrNull() }.toSet()
        val canRemove = fallenBricks.values.filter { it.size >= 2 }.flatten().toSet()
        val isOnTop = fallenBricks.keys.filter { it !in fallenBricks.values.flatten().toSet() }
        return (canRemove - cannotRemove + isOnTop).size
    }

    override fun part2(): Any? {
        val fallenBricks = settled.toMutableMap()
        val mapToCheck = fallenBricks - fallenBricks.filter { it.value.isEmpty() }.keys
        return fallenBricks.keys
            .map { remove(setOf(it), mapToCheck.mapValues { (_, value) -> value.toMutableSet() }.toMutableMap()) }
            .sumOf { it.size }
    }

    private fun remove(bricks: Set<Brick>, map: MutableMap<Brick, MutableSet<Brick>>): Set<Brick> {
        if (bricks.isEmpty())
            return emptySet()
        val mapIterator = map.iterator()
        val nextFall = mutableSetOf<Brick>()
        while (mapIterator.hasNext()) {
            mapIterator.next().let {
                it.value -= bricks
                if (it.value.isEmpty()) {
                    mapIterator.remove()
                    nextFall.add(it.key)
                }
            }
        }
        return nextFall + remove(nextFall, map)
    }
}

data class Brick(
    val zRange: LongRange,
    val yRange: LongRange,
    val xRange: LongRange
) {
    private val top by lazy { copy(zRange = zRange.last..zRange.last) }
    private val bottom by lazy { copy(zRange = zRange.first..zRange.first) }

    context(MutableMap<Brick, List<Brick>>)
    fun settleDown(): Any? {
        var prevBottom = bottom
        var curBottom = bottom.moveDown()
        while(true){
            if(curBottom.zRange.first == 0L){
                return put(moveDown(zRange.first - prevBottom.zRange.first), emptyList())
            }
            val intersections = keys.filter { it.top.intersects(curBottom) }
            if(intersections.isNotEmpty()){
                return put(moveDown(zRange.first - prevBottom.zRange.first), intersections)
            }
            prevBottom = curBottom
            curBottom = curBottom.moveDown()
        }
    }
    private fun moveDown(amount: Long = 1): Brick = copy(zRange = (zRange.first - amount)..(zRange.last - amount))

    private fun intersects(other: Brick) = !zRange.intersect(other.zRange).isEmpty() &&
            !xRange.intersect(other.xRange).isEmpty() &&
            !yRange.intersect(other.yRange).isEmpty()
}