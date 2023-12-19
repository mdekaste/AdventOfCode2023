package day19

import Challenge
import helpers.intersect
import helpers.splitOnEmpty

fun main() {
    Day19.part1().let(::println)
    Day19.part2().let(::println)
}

object Day19 : Challenge() {
    lateinit var path: Map<String, List<Rule>>
    lateinit var machines: List<Machine>

    val parsed = input.splitOnEmpty().let { (top, bottom) ->
        path = top.lines().map {
            it
            val a = it.substringBefore('{')
            val line = it.substringAfter('{').substringBefore('}')
            a to line.split(',').map { rule(it) }
        }.toMap()
        machines = bottom.lines().map { it.substringAfter('{').substringBefore('}') }.map {
            it.split(',').map {
                it.split('=').let { (a, b) -> a to b.toInt() }
            }.toMap().let(::Machine)
        }
    }

    class Machine(val map: Map<String, Int>) {
        val x by map
        val m by map
        val a by map
        val s by map
    }

    sealed interface Transition
    data class Next(val input: String) : Transition
    data class Accepted(val boolean: Boolean) : Transition

    sealed interface Rule {
        val transition: Transition
        fun check(machine: Machine): Transition?
    }

    data class Just(
        override val transition: Transition
    ) : Rule {
        override fun check(machine: Machine) = transition
    }

    data class Check(
        val variable: String,
        val compareValue: Int,
        val compare: Compare,
        override val transition: Transition,
    ) : Rule {
        val range = when (compare) {
            Compare.LESS -> 1L..<compareValue
            Compare.LESS_EQUALS -> 1L..compareValue
            Compare.MORE -> (compareValue + 1)..4000L
            Compare.MORE_EQUALS -> compareValue..4000L
        }

        override fun check(machine: Machine): Transition? =
            transition.takeIf { machine.map.getValue(variable) in range }

        fun invert(): Check = Check(variable, compareValue, compare.invert(), transition)
    }

    enum class Compare(val char: String) {
        LESS("<"), MORE(">"), MORE_EQUALS(">="), LESS_EQUALS("<=");

        fun invert(): Compare = entries[(ordinal + 2) % 4]
    }

    fun rule(input: String): Rule {
        if (!input.contains(":")) {
            return Just(
                when (input) {
                    "A" -> Accepted(true)
                    "R" -> Accepted(false)
                    else -> Next(input)
                }
            )
        } else {
            input.split(":").let { (check, input) ->
                val transition = when (input) {
                    "A" -> Accepted(true)
                    "R" -> Accepted(false)
                    else -> Next(input)
                }
                when (check.contains("<")) {
                    true -> {
                        val variable = check.substringBefore("<")
                        val value = check.substringAfter("<").toInt()
                        return Check(variable, value, Compare.LESS, transition)
                    }

                    else -> {
                        val variable = check.substringBefore(">")
                        val value = check.substringAfter(">").toInt()
                        return Check(variable, value, Compare.MORE, transition)
                    }
                }
            }
        }
    }

    override fun part1(): Any? {
        return machines.filter(::accepted).sumOf { it.a + it.x + it.m + it.s }
    }

    override fun part2(): Any? {
        val test = accepted2().map {
            it.filterIsInstance<Check>()
                .groupingBy { it.variable }
                .fold(listOf(1L..4000L)) { ranges, range ->
                    ranges.mapNotNull {
                        it.intersect(range.range).takeUnless { it.isEmpty() }
                    }
                }.mapValues { it.value.single() }
                    .withDefault { 1L..4000L }

        }.map {
            val xCount = it.getValue("x").let{ it.endInclusive - it.start + 1L }
            val mCount = it.getValue("m").let{ it.endInclusive - it.start + 1L }
            val aCount = it.getValue("a").let{ it.endInclusive - it.start + 1L }
            val sCount = it.getValue("s").let { it.endInclusive - it.start + 1L }
            xCount * mCount * aCount * sCount
        }.sum()
        return println(test)
    }

    fun accepted2(thusFar: List<Rule> = emptyList(), rules: List<Rule> = path.getValue("in")): List<List<Rule>> {
        return buildList<List<Rule>> {
            var list = emptyList<Rule>()
            rules.forEach {
                when (it) {
                    is Just -> when (val transition = it.transition) {
                        is Accepted -> if (transition.boolean) {
                            add(thusFar + list + it)
                        }

                        is Next -> addAll(accepted2(thusFar + list + it, path.getValue(transition.input)))
                    }

                    is Check -> {
                        when (val transition = it.transition) {
                            is Accepted -> if (transition.boolean) {
                                add(thusFar + list + it)
                            }

                            is Next -> addAll(accepted2(thusFar + list + it, path.getValue(transition.input)))
                        }
                        list = list + it.invert()
                    }
                }
            }
        }
    }

    fun accepted(machine: Machine, rule: List<Rule> = path.getValue("in")): Boolean {
        rule.asSequence().map { it.check(machine) }.firstNotNullOf { it }.let {
            when (it) {
                is Accepted -> return it.boolean
                is Next -> return accepted(machine, path.getValue(it.input))
            }
        }
    }
}