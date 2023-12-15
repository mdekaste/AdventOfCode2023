package day15

import Challenge

fun main() {
    Day15.part1().let(::println)
    Day15.part2().let(::println)
}

object Day15 : Challenge() {
    val parsed = input.split(",")

    fun hash(input: String): Int = input.fold(0) { hash, c -> ((hash + c.code) * 17) % 256 }
    override fun part1() = parsed.map(::hash).sum()

    override fun part2(): Any? {
        return parsed.map {
            when {
                it.contains("-") -> listOf(it.substringBefore("-"), "-", "0")
                else -> listOf(it.substringBefore("="), "=", it.substringAfter("="))
            }
        }.map { (a, b, c) ->
            Triple(a, b, c.toInt())
        }.fold(MutableList(256){ mutableMapOf<String, Int>() }){ acc, (a, b, c) ->
            val hash = hash(a)
            when(b){
                "-" -> acc[hash].remove(a)
                "=" -> acc[hash].put(a, c)
                else -> error("")
            }
            acc
        }.foldIndexed(0){ index: Int, acc: Int, mutableMap: MutableMap<String, Int> ->
            acc + (index + 1) * mutableMap.values.withIndex().sumOf { (index, value) -> (index + 1) * value }
        }
    }
}
