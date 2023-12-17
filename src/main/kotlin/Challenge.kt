
import java.io.File
import kotlin.time.measureTimedValue

typealias Point = Pair<Int, Int>

abstract class Challenge(
    val name: String? = null,
) {
    val input = File(javaClass.getResource("input").path).readText()

    abstract fun part1(): Any?
    abstract fun part2(): Any?

    fun solve(): Any? {
        repeat(100) {
            part1()
            part2()
        }
        val part1 = measureTimedValue { part1() }
        val part2 = measureTimedValue { part2() }
        return "$part1 $part2 ${part1.duration + part2.duration}"
    }

    companion object {
        val NORTH = -1 to 0
        val EAST = 0 to 1
        val SOUTH = 1 to 0
        val WEST = 0 to -1
        val NORTH_EAST = -1 to 1
        val SOUTH_EAST = 1 to 1
        val SOUTH_WEST = 1 to -1
        val NORTH_WEST = -1 to -1
        val ORIGIN = 0 to 0
        operator fun Point.plus(other: Point) = first + other.first to second + other.second
        operator fun Point.minus(other: Point) = first - other.first to second - other.second
        fun Point.neighbours4() = listOf(NORTH, EAST, SOUTH, WEST).map { it + this }
        fun Point.neighbours8() =
            listOf(NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST).map { it + this }

    }

    enum class Direction(val position: Point) {
        N(NORTH), E(EAST), S(SOUTH), W(WEST);
        operator fun not() = Direction.entries - this
        operator fun unaryMinus() = Direction.entries[(ordinal + 2) % 4]
        val perpendicular by lazy { listOf(Direction.entries[(ordinal + 1) % 4], Direction.entries[(ordinal + 3) % 4]) }
    }


}
