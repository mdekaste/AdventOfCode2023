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

    val LOWERBOUND = 200000000000000.0.toBigDecimal()
    val UPPERBOUND = 400000000000000.0.toBigDecimal()

//    const val LOWERBOUND = 7.0
//    const val UPPERBOUND = 27.0

    override fun part1(): Any? {
        val toCheck = parsed.flatMapIndexed { index: Int, input: Input -> parsed.drop(index + 1).map { input to it } }
        return toCheck.map { (first, second) -> intersects(first, second) }.count { it }
    }

    fun intersects(input1: Input, input2: Input): Boolean {
        var x: BigDecimal
        var y: BigDecimal
        if(input1.equation == input2.equation){
            val xGrowth = input1.x < input2.x && input1.xDiff > 0 && input2.xDiff < 0
            val xGrowth2 = input1.x > input2.x && input1.xDiff < 0 && input2.xDiff > 0
            val yGrowth = input1.y < input2.y && input1.yDiff > 0 && input2.yDiff < 0
            val yGrowth2 = input1.y > input2.y && input1.yDiff < 0 && input2.yDiff > 0
            if((xGrowth || xGrowth2) && (yGrowth || yGrowth2)){
                x = ((input1.x + input2.x) / 2L).toBigDecimal()
                y = ((input1.y + input2.y) / 2L).toBigDecimal()
            }
            return false
        } else if(input1.equation.m == input2.equation.m) {
            return false
        } else {
            x = (input2.equation.b - input1.equation.b) / (input1.equation.m - input2.equation.m)
            y = (input1.equation.m * x + input1.equation.b)
        }

        if(x < LOWERBOUND || x > UPPERBOUND || y < LOWERBOUND || y > UPPERBOUND){
            return false
        }
        if(input1.xDiff >= 0 && x <= input1.x.toBigDecimal()){
            return false
        }
        if(input2.xDiff >= 0 && x <= input2.x.toBigDecimal()){
            return false
        }
        if(input1.yDiff >= 0 && y <= input1.y.toBigDecimal()){
            return false
        }
        if(input2.yDiff >= 0 && y <= input2.y.toBigDecimal()){
            return false
        }
        return true
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

    override fun part2(): Any? {
        return null
    }
}