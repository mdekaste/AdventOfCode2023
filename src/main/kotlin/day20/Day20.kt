package day20

import Challenge

fun main() {
    Day20.part1().let(::println)
    Day20.part2().let(::println)
}

object Day20 : Challenge() {
    val parsed = input.lines().map {
        it.split(" -> ").let { (a, b) ->
            val type = if(a.contains('%') || a.contains('&')) a[0] else null
            val rest = if(a.contains('%') || a.contains('&')) a.substring(1..<a.length) else a
            when(type){
                null -> Start(rest, b.split(", "))
                '%' -> FlipFlop(rest, b.split(", "))
                '&' -> Conjuction(rest, b.split(", "))
                else -> error("")
            }
        }
    }.associateBy { it.key }.also {  map ->
        map.forEach { key, value ->
            for(o in value.output){
                val target = map[o] ?: continue
                if(target is Conjuction){
                    target.input[key] = false
                }
            }
        }
    }

    sealed interface Module {
        val lastSendSignal: Boolean
        val key: String
        val output: List<String>
        fun receive(from: String, signal: Boolean): Pair<List<String>, Boolean>
    }

    data class Start(
        override val key: String,
        override val output: List<String>
    ) : Module {
        override val lastSendSignal: Boolean = false
        override fun receive(from: String, signal: Boolean) = output to signal
    }

    data class FlipFlop(
        override val key: String,
        override val output: List<String>
    ) : Module {
        var onOrOff: Boolean = false
        override val lastSendSignal: Boolean = onOrOff
        override fun receive(from: String, signal: Boolean): Pair<List<String>, Boolean> {
            return when(signal){
                true -> emptyList<String>() to false
                false -> {
                    onOrOff = !onOrOff
                    output to onOrOff
                }
            }
        }
    }

    data class Conjuction(
        override val key: String,
        override val output: List<String>
    ) : Module {
        val input: MutableMap<String, Boolean> = mutableMapOf()
        override val lastSendSignal: Boolean = !input.values.all { it }
        override fun receive(from: String, signal: Boolean): Pair<List<String>, Boolean> {
            input[from] = signal
            return output to !input.values.all { it }
        }
    }

    fun simulate(): Pair<Int, Int>{
        val nodes = parsed.getValue("broadcaster").output.map { Triple("",it, false) }.toMutableList()
        var trueCount = 0
        var falseCount = nodes.size + 1
        while(nodes.isNotEmpty()){
            val (from, to, signal) = nodes.removeFirst()
            val (output, sent) = parsed[to]?.receive(from, signal) ?: continue
            for(o in output){
                if(sent){
                    trueCount++
                } else {
                    falseCount++
                }
                nodes.add(Triple(to, o, sent))
            }
        }
        return trueCount to falseCount
    }

    override fun part1(): Any? {
        return null
        //return generateSequence { simulate() }.take(1000).fold(0L to 0L){ (a,b), (c,d) -> a + c to b + d}.let { it.first * it.second }
    }

    override fun part2(): Any? {
        val initial = parsed.values.filterIsInstance<FlipFlop>().map { it.onOrOff }
        val names = parsed.values.filterIsInstance<FlipFlop>().map { it.key }
        //29 43 44 46
        //rb xk vj nc
        var previous = List(initial.size){ true }
        val hasFlipped = MutableList(initial.size){ false }
        val flippedOn = MutableList<Int?>(initial.size){ null as Int? }
        val flippedOff = MutableList<Int?>(initial.size) { null }
        var totalIndex = 0
        while(true){
            simulate2()
            parsed.values.filterIsInstance<FlipFlop>().map { it.onOrOff }.forEachIndexed { index, b ->
                if(b && flippedOn[index] == null){
                    flippedOn[index] = totalIndex + 1
                } else if(!b && flippedOn[index] != null && flippedOff[index] == null) {
                    flippedOff[index] = totalIndex + 1
                }
            }
            if(flippedOff.none { it == null }){
                val x = flippedOn.zip(flippedOff).filter { it.second!!.countOneBits() != 1 }
                return x
            }
            totalIndex++
        }
        error("")
    }

    fun simulate2(): Boolean {
        val nodes = parsed.getValue("broadcaster").output.map { Triple("",it, false) }.toMutableList()
        while(nodes.isNotEmpty()){
            val (from, to, signal) = nodes.removeFirst()
            val (output, sent) = parsed[to]?.receive(from, signal) ?: if(signal == false){
                return true
            } else{
                continue
            }
            for(o in output){
                nodes.add(Triple(to, o, sent))
            }
        }
        return false
    }
}