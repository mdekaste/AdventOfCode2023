package year2021.day24

import Challenge

fun main() {
    Day24.part1().let(::println)
}

private const val i1 = 0

object Day24 : Challenge() {
    val parsed = input.lines()
    override fun part1(): Any {
        val program = Program(parsed)
        return generateSequence(List(14) { 9 }) { list -> list.nextDown() }.first {
            program.run(it)
        }.also { println(it) }.let { it.fold(0L) { acc, i -> acc * 10 + i } }

        TODO("Not yet implemented")
    }

    fun List<Int>.nextDown(): List<Int> = when (val last = last()) {
        0 -> dropLast(1).nextDown() + 9
        else -> dropLast(1) + (last - 1)
    }

    class Program(input: List<String>) {
        val memory = Memory()
        val instructionSet = input.map { Instruction(it, this) }
        var inputIndex = 0
        var input: List<Int> = emptyList()

        data class State(
            val insIndex: Int,
            val instructions: List<Int>,
            val w: Long,
            val x: Long,
            val y: Long,
            val z: Long,
        )

        val memoized = mutableMapOf<State, Boolean>()

        fun run(input: List<Int>): Boolean {
            memory.clear()
            this.input = input
            this.inputIndex = 0

            fun recursive(insIndex: Int): Boolean {
                return if (insIndex >= this.instructionSet.size) {
                    memory.get(Variable("z")).amount == 0L
                } else {
                    val instruction = instructionSet[insIndex]
                    if (!instruction.execute()) {
                        false
                    } else {
                        recursive(insIndex + 1)
                    }
                }
            }

            return recursive(0)
        }

//            fun recursive(insIndex: Int): Boolean = memoized.getOrPut(
//                State(
//                    insIndex = insIndex,
//                    instructions = this.input.subList(inputIndex, this.input.size),
//                    w = memory.variables.getValue(Variable("w")).amount,
//                    x = memory.variables.getValue(Variable("x")).amount,
//                    y = memory.variables.getValue(Variable("y")).amount,
//                    z = memory.variables.getValue(Variable("z")).amount,
//                ),
//            ) {
//                if (insIndex >= this.instructionSet.size) {
//                    memory.get(Variable("z")).amount == 0L
//                } else {
//                    val instruction = instructionSet[insIndex]
//                    if (!instruction.execute()) {
//                        false
//                    } else {
//                        recursive(insIndex + 1)
//                    }
//                }
//            }
//            return recursive(0)
//        }

        fun getInput() = input[inputIndex++]
    }

    sealed class Instruction(val program: Program) {
        abstract fun execute(): Boolean

        companion object {
            operator fun invoke(line: String, program: Program): Instruction {
                val split = line.split(' ')
                return when (split[0]) {
                    "inp" -> Input(Variable(split[1]), program)
                    "add" -> Add(Variable(split[1]), Value(split[2]), program)
                    "mul" -> Mul(Variable(split[1]), Value(split[2]), program)
                    "div" -> Div(Variable(split[1]), Value(split[2]), program)
                    "mod" -> Mod(Variable(split[1]), Value(split[2]), program)
                    "eql" -> Eql(Variable(split[1]), Value(split[2]), program)
                    else -> error("invalid instruction: $line")
                }
            }
        }
    }

    class Input(val variable: Variable, program: Program) : Instruction(program) {
        override fun execute(): Boolean {
            program.memory[variable] = Exact(program.getInput().toLong())
            return true
        }
    }

    class Add(val variable: Variable, val value: Value, program: Program) : Instruction(program) {
        override fun execute(): Boolean {
            program.memory[variable] = program.memory[variable] + program.memory[value]
            return true
        }
    }

    class Mul(val variable: Variable, val value: Value, program: Program) : Instruction(program) {
        override fun execute(): Boolean {
            program.memory[variable] = program.memory[variable] * program.memory[value]
            return true
        }
    }

    class Div(val variable: Variable, val value: Value, program: Program) : Instruction(program) {
        override fun execute(): Boolean {
            program.memory[variable] = program.memory[variable] / (program.memory[value].takeIf { it.amount != 0L } ?: return false)
            return true
        }
    }

    class Mod(val variable: Variable, val value: Value, program: Program) : Instruction(program) {
        override fun execute(): Boolean {
            program.memory[variable] = (program.memory[variable].takeIf { it.amount >= 0 } ?: return false) %
                (program.memory[value].takeIf { it.amount > 0 } ?: return false)
            return true
        }
    }

    class Eql(val variable: Variable, val value: Value, program: Program) : Instruction(program) {
        override fun execute(): Boolean {
            program.memory[variable] = if (program.memory[variable] == program.memory[value]) Exact(1) else Exact(0)
            return true
        }
    }

    sealed interface Value {
        companion object {
            operator fun invoke(name: String): Value {
                return name.toIntOrNull()?.let { Exact(it.toLong()) } ?: Variable(name)
            }
        }
    }

    data class Variable(val name: String) : Value
    data class Exact(val amount: Long) : Value {
        operator fun plus(other: Exact) = Exact(amount + other.amount)
        operator fun minus(other: Exact) = Exact(amount - other.amount)
        operator fun times(other: Exact) = Exact(amount * other.amount)
        operator fun div(other: Exact) = Exact(amount / other.amount)
        operator fun rem(other: Exact) = Exact(amount % other.amount)
    }

    class Memory {
        var variables: MutableMap<Variable, Exact> = mutableMapOf(Variable("w") to Exact(0), Variable("x") to Exact(0), Variable("y") to Exact(0), Variable("z") to Exact(0))
        var inputIndex = 0

        operator fun set(key: Value, value: Value) = when (key) {
            is Exact -> error("cannot set memory on key: ${key.amount}")
            is Variable -> variables[key] = get(value)
        }

        operator fun get(value: Value): Exact = when (value) {
            is Exact -> value
            is Variable -> variables.getValue(value)
        }

        fun clear() {
            variables = mutableMapOf(Variable("w") to Exact(0), Variable("x") to Exact(0), Variable("y") to Exact(0), Variable("z") to Exact(0))
        }
    }

    override fun part2(): Any? {
        TODO("Not yet implemented")
    }
}
