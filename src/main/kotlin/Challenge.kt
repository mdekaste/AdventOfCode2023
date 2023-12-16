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
        repeat(1000) {
            part1()
            part2()
        }
        return measureTimedValue { listOf(name, part1(), part2()) }
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
}
