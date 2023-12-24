package day24

import Challenge

fun main(){
    Day24.part1().let(::println)
    Day24.part2().let(::println)
}

object Day24 : Challenge() {
    val parsed = input.lines().map {
        it.split(" @ ").let { (a,b) ->
            a.split(", ").map { it.toLong() } to b.split(", ").map { it.toLong() }
        }
    }

    //const val LOWERBOUND = 200000000000000L
    //const val UPPERBOUND = 400000000000000L

    const val LOWERBOUND = 0.0
    const val UPPERBOUND = Double.MAX_VALUE

    override fun part1(): Any? {
        val equations = parsed.map { it.toEquation() }
        val collisions = equations.zip(parsed).flatMapIndexed { index: Int, pair1 ->
            equations.zip(parsed).mapIndexedNotNull { index2, pair2 ->
                if(index == index2){
                    null
                } else {
                    collides(pair1.first, pair2.first, pair1.second, pair2.second)
                }
            }
        }
        return collisions.count { it }
    }

     fun collides(first: Pair<Long, Long>, second: Pair<Long, Long>, equation: Pair<List<Long>, List<Long>>, equation2: Pair<List<Long>, List<Long>>): Boolean {
         val a1 = -first.first
         val b1 = 1
         val c1 = -first.second

         val a2 = -second.first
         val b2 = 1
         val c2 = -second.second
         try {
             val yIntersect = ((c1 * a2) - (c2 * a1)) / ((a1 * b2) - (a2 * b1).toDouble())
             val xIntersect = ((b1 * c2) - (b2 * c1)) / ((a1 * b2) - (a2 * b1).toDouble())
             val ybound1 = equation.first[1].toDouble()
             val xbound1 = equation.first[0].toDouble()
             val ybound2 = equation2.first[1].toDouble()
             val xbound2 = equation2.first[0].toDouble()
             val rangey1 = if(equation.second[1] < 0) LOWERBOUND..ybound1 else ybound1..UPPERBOUND
             val rangex1 = if(equation.second[0] < 0) LOWERBOUND..xbound1 else xbound1..UPPERBOUND
             val rangey2 = if(equation2.second[1] < 0) LOWERBOUND..ybound2 else ybound2..UPPERBOUND
             val rangex2 = if(equation2.second[0] < 0) LOWERBOUND..xbound2 else xbound2..UPPERBOUND
             return yIntersect in rangey1 && xIntersect in rangex1 && yIntersect in rangey2 && xIntersect in rangex2
         } catch (exception: Exception) {
             return false
         }
     }

    fun Pair<List<Long>, List<Long>>.toEquation(): Pair<Long, Long> {
        val point1 = first[1] to first[0]
        val point2 = (first[1] + second[1]) to (first[0] + second[0])
        val slope = (point2.first - point1.first) / (point2.second - point1.second)
        val b = point1.first - slope * point1.second
        return slope to b
    }

    override fun part2(): Any? {
        return null
    }
}