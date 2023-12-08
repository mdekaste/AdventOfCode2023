import java.io.File
import kotlin.time.measureTimedValue

abstract class Challenge(
    val name: String? = null,
) {
    val input = File(javaClass.getResource("input").path).readText()

    abstract fun part1(): Any?
    abstract fun part2(): Any?

    fun solve(): Any? {
        repeat(10000) {
            part1()
            part2()
        }
        return measureTimedValue { listOf(name, part1(), part2()) }
    }
}
