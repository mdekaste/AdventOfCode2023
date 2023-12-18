
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

    enum class Direction(val position: Point) {
        N(NORTH), E(EAST), S(SOUTH), W(WEST);
        operator fun not() = Direction.entries - this
        operator fun unaryMinus() = Direction.entries[(ordinal + 2) % 4]
        val perpendicular by lazy { listOf(Direction.entries[(ordinal + 1) % 4], Direction.entries[(ordinal + 3) % 4]) }
    }


}

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

operator fun Point.times(amount: Int) = first * amount to second * amount

val CARDINALS = listOf(NORTH, EAST, SOUTH, WEST)
val INTERCARDINALS = listOf(NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST)
val WINDS = listOf(NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST)
fun Point.cardinals() = CARDINALS.map { it + this }
fun Point.intercardinals() = INTERCARDINALS.map { it + this }
fun Point.winds() = WINDS.map { it + this }
fun Point.sum() = first + second

operator fun Point.unaryMinus() = -first to -second
operator fun Point.dec() = rotLeft()
operator fun Point.inc() = rotRight()
fun Point.rotRight() = -second to first
fun Point.rotLeft() = second to -first

fun Point.perpendicular() = sequenceOf(rotLeft(), rotRight())
operator fun Point.not() = sequenceOf(rotLeft(), -this, rotRight())