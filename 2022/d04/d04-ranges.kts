import java.io.File
import java.util.SortedSet

val input: String = File("input").readText()
val pairs: List<List<LongRange>> = input
    .split("\n")
    .filter { it.isNotEmpty() }
    .map { pair -> pair
        .split(",")
        .map { elf -> elf
            .split("-")
            .map { it.toLong() }
        }
        .map { it[0]..it[1] }
    }

// Part 1
print("Answer 1: ")
pairs
    .filter { pair ->
        pair[0].all { pair[1].contains(it) } || pair[1].all { pair[0].contains(it) }
    }
    .let { println(it.count()) }


//Part 2
print("Answer 2: ")
pairs
    .filter { pair ->
        pair[0].any { pair[1].contains(it) } || pair[1].any { pair[0].contains(it) }
    }
    .let { println(it.count()) }
