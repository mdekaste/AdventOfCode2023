package day19

import Challenge
import day19.Day19.Rule
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
    class Next(val input: String) : Transition
    class Accepted(val boolean: Boolean) : Transition

    fun interface Rule {
        abstract fun rule(machine: Machine): Transition?
    }

//    class Rule(
//        val variable: String,
//        val compareValue: Int,
//        val compare: Compare,
//        val transition: Transition,
//    ){
//        val range = when(compare){
//            Compare.LESS -> 0 until compareValue
//        }
//    }

    enum class Compare(val char: String){
        LESS("<"), MORE(">"), MORE_EQUALS(">="), LESS_EQUALS("<=");
        fun invert(): Compare = entries[(ordinal + 2) % 4]
    }

    fun rule(input: String): Rule {
        if (!input.contains(":")) {
            return Rule { _ ->
                when (input) {
                    "A" -> Accepted(true)
                    "R" -> Accepted(false)
                    else -> Next(input)
                }
            }
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
                        return Rule { map -> transition.takeIf { map.map.getValue(variable) < value } }
                    }

                    else -> {
                        val variable = check.substringBefore(">")
                        val value = check.substringAfter(">").toInt()
                        return Rule { map -> transition.takeIf { map.map.getValue(variable) > value } }
                    }
                }
            }
        }
    }

    override fun part1(): Any? {
        return machines.filter(::accepted).sumOf { it.a + it.x + it.m + it.s }
    }

    fun accepted(machine: Machine, rule: List<Rule> = path.getValue("in")): Boolean {
        rule.asSequence().map { it.rule(machine) }.firstNotNullOf { it }.let {
            when (it) {
                is Accepted -> return it.boolean
                is Next -> return accepted(machine, path.getValue(it.input))
            }
        }
    }

    override fun part2(): Any? {
        path.forEach { key, transitions ->
            transitions.filter { it.rule() }
        }
    }
}