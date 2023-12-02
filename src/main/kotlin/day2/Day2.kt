package day2

import Challenge
import kotlin.math.max

fun main() {
    Day2.part1().let(::println)
    Day2.part2().let(::println)
}

object Day2 : Challenge() {
    private val parsed: List<Game> = input.lines().map { line ->
        line.split(": ").let { (game, subgame) ->
            val gameId = game.substringAfter("Game ").toInt()
            val subgames = subgame.split("; ").map { cubes ->
                SubGame(
                    cubes.split(", ").map { cube ->
                        cube.split(" ").let { (amount, color) ->
                            amount.toInt() to color
                        }
                    }.associateBy({ it.second }, { it.first }).withDefault { 0 },
                )
            }
            Game(gameId, subgames)
        }
    }

    class Game(
        val id: Int,
        private val subGames: List<SubGame>,
    ) {
        fun isValidGame(red: Int, green: Int, blue: Int) =
            subGames.all { subgame -> subgame.red <= red && subgame.green <= green && subgame.blue <= blue }

        val power = subGames.fold(Triple(0, 0, 0)) { (maxRed, maxBlue, maxGreen), subGame ->
            Triple(
                max(maxRed, subGame.red),
                max(maxBlue, subGame.blue),
                max(maxGreen, subGame.green),
            )
        }.let { (red, green, blue) -> red * green * blue }
    }

    class SubGame(map: Map<String, Int>) {
        val red by map
        val green by map
        val blue by map
    }

    override fun part1() = parsed.filter { it.isValidGame(12, 13, 14) }.sumOf { it.id }
    override fun part2() = parsed.sumOf(Game::power)
}
