package day20

import Challenge
import helpers.lcm

fun main() {
    Day20.part1().let(::println)
    Day20.part2().let(::println)
}

object Day20 : Challenge() {
    val parsed = input.lines().map {
        it.split(" -> ").let { (a, b) ->
            val type = if (a.contains('%') || a.contains('&')) a[0] else null
            val rest = if (a.contains('%') || a.contains('&')) a.substring(1..<a.length) else a
            when (type) {
                null -> Start(rest, b.split(", "))
                '%' -> FlipFlop(rest, b.split(", "))
                '&' -> Conjuction(rest, b.split(", "))
                else -> error("")
            }
        }
    }.associateBy { it.key }.also { map ->
        map.forEach { (key, value) ->
            for (o in value.output) {
                when (val target = map[o]) {
                    is Conjuction -> target.input[key] = false
                    else -> Unit
                }
            }
        }
    }

    sealed interface Module {
        val lastSendSignal: Boolean
        val key: String
        val output: List<String>
        fun receive(from: String, signal: Boolean): Pair<List<String>, Boolean>
        fun reset(){ }
    }

    data class Start(override val key: String, override val output: List<String>) : Module {
        override val lastSendSignal: Boolean get() = false
        override fun receive(from: String, signal: Boolean) = output to signal
    }

    data class FlipFlop(override val key: String, override val output: List<String>) : Module {
        override var lastSendSignal: Boolean = false
        override fun receive(from: String, signal: Boolean): Pair<List<String>, Boolean> {
            return when (signal) {
                true -> emptyList()
                false -> output.also { lastSendSignal = !lastSendSignal }
            } to lastSendSignal
        }

        override fun reset() {
            lastSendSignal = false
        }
    }

    data class Conjuction(override val key: String, override val output: List<String>) : Module {
        val input: MutableMap<String, Boolean> = mutableMapOf()
        override val lastSendSignal: Boolean get() = !input.values.all { it }
        override fun receive(from: String, signal: Boolean): Pair<List<String>, Boolean> {
            input[from] = signal
            return output to lastSendSignal
        }

        override fun reset() {
            input.mapValues { false }
        }
    }

    private fun simulate() = sequence{
        val nodes = mutableListOf(Triple("input", "broadcaster", false))
        while (nodes.isNotEmpty()) {
            val (from, to, signal) = nodes.removeFirst()
            yield(signal)
            val (output, sent) = parsed[to]?.receive(from, signal) ?: continue
            for (o in output) {
                nodes.add(Triple(to, o, sent))
            }
        }
    }

    override fun part1() = generateSequence { simulate() }
        .take(1000)
        .flatten()
        .fold(0 to 0){ (t, f), b -> if(b) t + 1 to f else t to f + 1 }
        .let { (t, f) -> t * f }

    override fun part2(): Any? {
        parsed.values.forEach(Module::reset)
        val flipflops = parsed.values.filterIsInstance<FlipFlop>()
        val states = BooleanArray(flipflops.size){ false }
        val flips = List(flipflops.size){ mutableListOf<Int>() }
        var totalIndex = 0
        while(flips.any { it.size < 2 }){
            simulate().last()
            flipflops.forEachIndexed { index, t ->
                if (t.lastSendSignal != states[index]) {
                    states[index] = t.lastSendSignal
                    flips[index] += totalIndex + 1
                }
            }
            totalIndex++
        }
        return flips.map { it[1].toLong() }.filter { it.countOneBits() != 1 }.reduce(Long::lcm)
    }
}