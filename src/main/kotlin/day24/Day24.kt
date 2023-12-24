package day24

import Challenge
import java.math.BigDecimal

fun main() {
    Day24.part1().let(::println)
    Day24.part2().let(::println)
}

object Day24 : Challenge() {
    val parsed = input.lines().map {
        it.split(" @ ").let { (a,b) ->
            val (x,y,z) = a.split(", ").map { it.toLong() }
            val (xDiff, yDiff, zDiff) = b.split(", ").map { it.toLong() }
            Input(x,y,z,xDiff,yDiff,zDiff)
        }
    }

    data class Input(
        val x: Long,
        val y: Long,
        val z: Long,
        val xDiff: Long,
        val yDiff: Long,
        val zDiff: Long
    ) {
        data class Equation(
            val m: BigDecimal,
            val b: BigDecimal
        )

        val equation = run {
            val x2 = x + xDiff
            val y2 = y + yDiff
            val m = (y2 - y).toBigDecimal() / (x2 - x).toBigDecimal()
            val b = y.toBigDecimal() - m * x.toBigDecimal()
            Equation(m, b)
        }
    }

    override fun part1(): Any? {
        return null
    }

    /** now run z3 on this lmao **/
    override fun part2(): Any? {
        return buildString {
            appendLine("(declare-const fx Int)")
            appendLine("(declare-const fy Int)")
            appendLine("(declare-const fz Int)")
            appendLine("(declare-const fdx Int)")
            appendLine("(declare-const fdy Int)")
            appendLine("(declare-const fdz Int)")
            parsed.take(3).forEachIndexed{ index, input ->
                appendLine("(declare-const t$index Int)")
                appendLine("(assert (>= t$index 0))")
                appendLine("(assert (= (+ ${input.x} (* ${input.xDiff} t$index)) (+ fx (* fdx t$index))))")
                appendLine("(assert (= (+ ${input.y} (* ${input.yDiff} t$index)) (+ fy (* fdy t$index))))")
                appendLine("(assert (= (+ ${input.z} (* ${input.zDiff} t$index)) (+ fz (* fdz t$index))))")
            }
            appendLine("(check-sat)")
            appendLine("(get-model)")
            appendLine("(eval (+ fx fy fz))")
        }
    }
}