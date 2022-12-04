import java.io.File
import java.util.SortedSet

val input: String = File("input").readText()
val pairs = input
    .split("\n", ",", "-")
    .filter { it.isNotEmpty() }
    .map { it.toLong() }
    .zipWithNext { a, b -> a..b }
    .filterIndexed { index, _ -> index % 2 == 0 }
    .zipWithNext { a, b -> Pair(a, b) }
    .filterIndexed { index, _ -> index % 2 == 0 }
Ú
// Part 1
print("Answer 1: ")
pairs
    .count { pair ->
        pair.first.all { pair.second.contains(it) } || pair.second.all { pair.first.contains(it) }
    }
    .let(::println)


//Part 2
print("Answer 2: ")
pairs
    .count { pair ->
        pair.first.any { pair.second.contains(it) } || pair.second.any { pair.first.contains(it) }
    }
    .let(::println)
