package day4

import Challenge
import helpers.extractInts

fun main() {
    Day4.part1().let(::println)
    Day4.part2().let(::println)
}

object Day4 : Challenge() {
    val parsed = input.lines().map {
        it.split(":\\s+".toRegex()).let { (card, items) ->
            val cardNo = card.extractInts().first()
            val items = items.split(" \\|\\s+".toRegex()).let { (before, after) ->
                before.split("\\s+".toRegex()).map { it.toInt() } to after.split("\\s+".toRegex()).map { it.toInt() }
            }
            cardNo to items
        }
    }

    override fun part1(): Any? {
        return parsed.map {
            val (winningNumbers, cardNumbers) = it.second
            cardNumbers.intersect(winningNumbers).fold(0){ acc, _ -> if(acc == 0) 1 else acc * 2 }
        }.sum()
    }

    override fun part2(): Any? {
        val parsed2 = parsed.associateBy({ it.first }, {it.second })
        val array = IntArray(parsed2.size){ 1 }

        parsed.forEach {
            val index = it.first - 1
            val cardCount = array[index]
            val (winningNumbers, cardNumbers) = it.second
            val leftOver = cardNumbers.intersect(winningNumbers)
            for(i in 1..leftOver.size){
                array[index + i] += cardCount
            }
        }

        return array.sum()

    }
}
