package helpers

import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

infix fun Int.to(other: Int) = Point(this, other)

data class Point(val y: Int, val x: Int) : List<Int> by listOf(y, x) {
    companion object {
        val ORIGIN = Point(0, 0)
    }

    operator fun plus(o: Point) = Point(y + o.y, x + o.x)
    operator fun minus(o: Point) = Point(y - o.y, x - o.x)

    operator fun plus(amount: Int) = Point(y + amount, x + amount)

    operator fun minus(amount: Int) = Point(y - amount, x - amount)

    operator fun times(amount: Int) = Point(y * amount, x * amount)
    operator fun div(amount: Int) = Point(y / amount, x / amount)
    fun sign() = Point(y.sign, x.sign)
    fun absoluteValue() = Point(y.absoluteValue, x.absoluteValue)
    fun max(): Int = max(y, x)

    fun min(): Int = min(y, x)
}
