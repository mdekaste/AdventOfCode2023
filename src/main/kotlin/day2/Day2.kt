package day2

import Challenge
import kotlin.math.max

fun main() {
    Day2.part1().let(::println)
    Day2.part2().let(::println)
}

object Day2 : Challenge() {
    private val games: List<Game> = input.lines().map { line ->
        line.split(": ").let { (game, subgames) ->
            Game(
                id = game.substringAfter("Game ").toInt(),
                subgames = subgames.split("; ").map { cubes ->
                    Subgame(
                        cubes.split(", ").associate { cube ->
                            cube.split(" ").let { (amount, color) -> color to amount.toInt() }
                        }.withDefault { 0 },
                    )
                },
            )
        }
    }

    class Game(
        val id: Int,
        private val subgames: List<Subgame>,
    ) {
        fun isValidGame(red: Int, green: Int, blue: Int) =
            subgames.all { subgame -> subgame.red <= red && subgame.green <= green && subgame.blue <= blue }

        val power = subgames.fold(Triple(0, 0, 0)) { (maxRed, maxBlue, maxGreen), subGame ->
            Triple(
                max(maxRed, subGame.red),
                max(maxBlue, subGame.blue),
                max(maxGreen, subGame.green),
            )
        }.let { (red, green, blue) -> red * green * blue }
    }

    class Subgame(map: Map<String, Int>) {
        val red by map
        val green by map
        val blue by map
    }

    override fun part1() = games.filter { it.isValidGame(12, 13, 14) }.sumOf { it.id }
    override fun part2() = games.sumOf(Game::power)
}
