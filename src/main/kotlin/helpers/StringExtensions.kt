package helpers

import helpers.StringExtensions.NUMBER_MATCHER

object StringExtensions {
    val NON_INT_DELIMITER = """(\D)+""".toRegex()
    val NUMBER_MATCHER = """(-?\d+)""".toRegex()
}

fun String.splitOnEmpty() = this.split(System.lineSeparator() + System.lineSeparator())
fun String.extractInts() = NUMBER_MATCHER.findAll(this).map { it.value }.map { it.toInt() }.toList()
fun String.extractLongs() = NUMBER_MATCHER.findAll(this).map { it.value }.map { it.toLong() }.toList()

fun CharSequence.indexOfOrNull(other: String) = indexOf(other).takeIf { it != -1 }
fun CharSequence.lastIndexOfOrNull(other: String) = lastIndexOf(other).takeIf { it != -1 }
