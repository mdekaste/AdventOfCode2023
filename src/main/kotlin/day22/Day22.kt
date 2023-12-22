package day22

import Challenge
import helpers.extractLongs
import helpers.intersect

fun main() {
    Day22.part1().let(::println)
    Day22.part2().let(::println)
}


private operator fun <E> List<E>.component6() = get(5)

object Day22 : Challenge() {
    private val bricks = input.lines().mapIndexed { index, s ->
        s.extractLongs().let { (x1, y1, z1, x2, y2, z2) -> Brick(z1..z2, y1..y2, x1..x2) }
    }.sortedWith(compareBy { it.zRange.first })

    private val settled = buildMap {
        for (brick in bricks) {
            brick.settleDown()
        }
    }

    override fun part1(): Any? {
        val cannotRemove = settled.mapNotNull { it.value.singleOrNull() }.toSet()
        val canRemove = settled.values.filter { it.size >= 2 }.flatten().toSet()
        val isOnTop = settled.keys.filter { it !in settled.values.flatten().toSet() }
        return (canRemove - cannotRemove + isOnTop).size
    }

    override fun part2() = settled.keys.sumOf { solve(setOf(it), settled.filterValues { it.isNotEmpty() }) }

    fun solve(remove: Set<Brick>, map: Map<Brick, List<Brick>>): Long = when(remove){
        emptySet<Brick>() -> 0
        else -> {
            val next = map.mapValues { it.value - remove }
            val toCheck = next.filter { it.value.isEmpty() }.keys
            toCheck.size + solve(toCheck, next.filterValues { it.isNotEmpty() })
        }
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
        while (true) {
            if (curBottom.zRange.first == 0L) {
                return put(moveDown(zRange.first - prevBottom.zRange.first), emptyList())
            }
            val intersections = keys.filter { it.top.intersects(curBottom) }
            if (intersections.isNotEmpty()) {
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