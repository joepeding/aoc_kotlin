import java.io.File

val input: String = File("input").readText()
val pairs = input
    .split("\n", ",", "-")
    .filter { it.isNotEmpty() }
    .map { it.toLong() }
    .windowed(4, 4, false) { it -> Pair(it[0]..it[1], it[2]..it[3]) }

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
