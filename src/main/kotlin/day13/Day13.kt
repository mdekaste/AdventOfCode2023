package day13

import Challenge
import helpers.splitOnEmpty
import java.util.*

fun main() {
    Day13.part1().let(::println)
    Day13.part2().let(::println)
}

typealias Grid = List<String>

fun Grid.findReflectionIndex(not: Int = -1): Int? = indexOfReflection().map(100::times).plus(transpose().indexOfReflection()).firstOrNull { it != not }
fun Grid.transpose() = first().indices.map { x -> indices.joinToString("") { y -> get(y).get(x).toString() } }
fun Grid.indexOfReflection() = indices.drop(1).filter(::splitsAlong)
fun Grid.splitsAlong(index: Int): Boolean = subList(0, index).matches(subList(index, size))
fun List<String>.matches(other: List<String>) = asReversed().zip(other).all { (a, b) -> a == b }

fun Grid.unSmudge(): List<Grid> = flatMapIndexed { y, s -> s.indices.map { x -> unSmudge(y, x) } }
fun Grid.unSmudge(y: Int, x: Int): Grid = take(y) + get(y).unSmudge(x) + drop(y + 1)
fun String.unSmudge(x: Int) = take(x) + get(x).unSmudge() + drop(x + 1)
fun Char.unSmudge() = if (this == '#') '.' else '#'

object Day13 : Challenge() {
    val parsed: List<Grid> = input.splitOnEmpty().map(String::lines)
    val reflectionIndexes = parsed.mapNotNull(Grid::findReflectionIndex)
    override fun part1() = reflectionIndexes.sum()
    override fun part2() = reflectionIndexes.zip(parsed.map(Grid::unSmudge)) { from, candidates ->
        candidates.firstNotNullOf { it.findReflectionIndex(from) }
    }.sum()
}
