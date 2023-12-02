import java.io.File

data class CubeSet(val r: Int, val g: Int, val b: Int)
data class Game(val id: Int, val sets: List<CubeSet>)

val input: String = File("input").readText()
val games: List<Game> = input
    .split("\n")
    .filter { it.isNotBlank() }
    .map { string ->
        string.split(": ").let { parts ->
            Game(
                parts.first().split(" ").last().toInt(),
                parts.last().split("; ").toCubeSets()
            )
        }
    }

print("Answer 1: ")
println(games
    .filter { game ->
        (game.sets.maxOfOrNull { it.r } ?: 0) <= 12 &&
        (game.sets.maxOfOrNull { it.g } ?: 0) <= 13 &&
        (game.sets.maxOfOrNull { it.b } ?: 0) <= 14
    }.sumOf { it.id }
)

print("Answer 2: ")
println(games.sumOf { it.sets.calculatePower() })

fun List<String>.toCubeSets(): List<D02.CubeSet> =
    this.map {set ->
        set
            .split(", ")
            .associate { group ->
                group
                    .split(" ")
                    .let {
                        it.last() to it.first().toInt()
                    }
            }
            .let { CubeSet(
                it.getOrDefault("red", 0),
                it.getOrDefault("green", 0),
                it.getOrDefault("blue", 0)
            ) }
    }

fun List<CubeSet>.calculatePower(): Int =
    (this.maxOfOrNull { it.r } ?: 0 ) *
    (this.maxOfOrNull { it.g } ?: 0 ) *
    (this.maxOfOrNull { it.b } ?: 0 )

