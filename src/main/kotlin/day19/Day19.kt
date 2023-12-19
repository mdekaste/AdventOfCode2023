package day19

import Challenge
import helpers.intersect
import helpers.splitOnEmpty
import kotlin.math.*

fun main() {
    Day19.part1().let(::println)
    Day19.part2().let(::println)
}

object Day19 : Challenge() {
    val workflows: Map<String, Map<Rule, Transition>>
    val parts: List<Map<String, Int>>

    init {
        input.splitOnEmpty().let { (top, bottom) ->
            workflows = top.lines().map {
                val a = it.substringBefore('{')
                val line = it.substringAfter('{').substringBefore('}')
                a to line.split(',').associate { rule(it) }
            }.toMap()
            parts = bottom.lines().map { it.substringAfter('{').substringBefore('}') }.map {
                it.split(',').map {
                    it.split('=').let { (a, b) -> a to b.toInt() }
                }.toMap()
            }
        }
    }

    sealed interface Transition {
        data class Next(val input: String) : Transition
        data class Accepted(val boolean: Boolean) : Transition
        companion object {
            operator fun invoke(input: String) = when (input) {
                "A" -> Accepted(true)
                "R" -> Accepted(false)
                else -> Next(input)
            }
        }
    }

    sealed interface Rule {
        fun check(machine: Map<String, Int>) = true

        data object Just : Rule
        data class Check(val variable: String, val value: Int, val compare: Compare) : Rule {
            val range = compare.toRange(value)
            override fun check(machine: Map<String, Int>) = machine.getValue(variable) in range
            fun invert() = Check(variable, value, compare.invert())
        }
    }


    enum class Compare(val char: String, val toRange: (Int) -> LongRange) {
        LESS("<", { 1L..<it }),
        MORE(">", { (it + 1)..4000L }),
        MORE_EQUALS(">=", { it..4000L }),
        LESS_EQUALS("<=", { 1L..it });

        fun invert(): Compare = entries[(ordinal + 2) % 4]

        companion object {
            operator fun invoke(char: Char) = entries.first { it.char == char.toString() }
        }
    }

    private fun rule(input: String): Pair<Rule, Transition> =
        input.split(":").let {
            when (it.size) {
                1 -> Rule.Just to Transition(it[0])
                else -> it[0].split('<', '>').let { (variable, value) ->
                    Rule.Check(variable, value.toInt(), Compare(it[0][1])) to Transition(it[1])
                }
            }
        }

    override fun part1() = parts.filter(::accepted).sumOf { it.getValue("x") + it.getValue("m") + it.getValue("a") + it.getValue("s") }

    private fun accepted(part: Map<String, Int>, rule: Map<Rule, Transition> = workflows.getValue("in")): Boolean =
        when (val transition = rule.entries.first { (rule, _) -> rule.check(part) }.value) {
            is Transition.Accepted -> transition.boolean
            is Transition.Next -> accepted(part, workflows.getValue(transition.input))
        }

    private fun LongRange.intersect(other: LongRange) = max(first, other.first)..min(last, other.last)
    private fun LongRange.length() = last - first + 1

    override fun part2(): Any {
        val test = pathsToAcceptance().map { checkRules ->
            checkRules.groupBy({ it.variable }, { it.range })
                .mapValues { (_, value) -> value.reduce { acc, longRange -> acc.intersect(longRange) } }
                .withDefault { 1L..4000L }
        }.sumOf {
            val xCount = it.getValue("x").length()
            val mCount = it.getValue("m").length()
            val aCount = it.getValue("a").length()
            val sCount = it.getValue("s").length()
            xCount * mCount * aCount * sCount
        }
        return test
    }

    private fun pathsToAcceptance(
        thusFar: List<Rule> = emptyList(),
        rules: Map<Rule, Transition> = workflows.getValue("in")
    ): List<List<Rule.Check>> = buildList {
        var list = emptyList<Rule.Check>()
        rules.entries.forEach { (rule, transition) ->
            when (transition) {
                Transition.Accepted(true) -> add((thusFar + list + rule).filterIsInstance<Rule.Check>())
                is Transition.Next -> addAll(pathsToAcceptance(thusFar + list + rule, workflows.getValue(transition.input)))
                else -> Unit
            }
            if (rule is Rule.Check) {
                list = list + rule.invert()
            }
        }
    }
}