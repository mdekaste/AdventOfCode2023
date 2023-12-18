package day18

import Challenge
import EAST
import NORTH
import Point
import SOUTH
import WEST
import plus
import times
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import java.awt.image.BufferedImage.TYPE_INT_RGB
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day18.image()
    Day18.part1().let(::println)
    Day18.part2().let(::println)
}

enum class Dir(val point: Point) { R(EAST), D(SOUTH), L(WEST), U(NORTH) }
object Day18 : Challenge() {
    val parsed = input.lines().map { line ->
        line.split(" ").let { (a, b, c) ->
            Triple(Dir.valueOf(a), b.toInt(), c.substring(2..7).toInt(16))
        }
    }

    override fun part1() = parsed.map { (a, b, _) -> a to b }.solve()
    override fun part2() = parsed.map { (_, _, c) -> Dir.entries[c % 16] to c / 16 }.solve()

    fun image() {
        val lines = parsed.runningFold((0 to 0) to 0) { (point, _), (dir, amount, colour) -> (point + dir.point * amount) to colour }
        val xMin = lines.minOf { it.first.second }
        val yMin = lines.minOf { it.first.first }
        val xMax = lines.maxOf { it.first.second }
        val yMax = lines.maxOf { it.first.first }
        val image = BufferedImage(xMax - xMin + 1, yMax - yMin + 1, TYPE_INT_ARGB)
        lines.zipWithNext { (point1, color1), (point2, _) ->
            for (y in min(point1.first, point2.first)..max(point1.first, point2.first)) {
                for (x in min(point1.second, point2.second)..max(point1.second, point2.second)) {
                    image.setRGB(x - xMin, y - yMin, Color(color1).rgb)
                }
            }
        }
        ImageIO.write(image, "png", File("output.png"))
    }

    private fun List<Pair<Dir, Int>>.solve() =
        runningFold(0 to 0) { point, (dir, amount) -> point + dir.point * amount }
            .zipWithNext { (y1, x1), (_, x2) -> (x2 - x1) * y1.toLong() }
            .sum().absoluteValue + sumOf { it.second } / 2 + 1
}