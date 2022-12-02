import java.io.File
import java.util.SortedSet

val input: String = File("input").readText()
val rounds: List<String> = input.split("\n").filter { it.isNotEmpty() }

// Part 1
rounds
    .map {
        when (it) {
            "A X" -> 1 + 3
            "A Y" -> 2 + 6
            "A Z" -> 3 + 0
            "B X" -> 1 + 0
            "B Y" -> 2 + 3
            "B Z" -> 3 + 6
            "C X" -> 1 + 6
            "C Y" -> 2 + 0
            "C Z" -> 3 + 3
            else -> throw Exception("boom")
        }
    }
    .sum()
    .also { println(it) }

//Part 2
rounds
    .map {
        when (it) {
            "A X" -> 3 + 0
            "A Y" -> 1 + 3
            "A Z" -> 2 + 6
            "B X" -> 1 + 0
            "B Y" -> 2 + 3
            "B Z" -> 3 + 6
            "C X" -> 2 + 0
            "C Y" -> 3 + 3
            "C Z" -> 1 + 6
            else -> throw Exception("boom")
        }
    }
    .sum()
    .also { println(it) }

