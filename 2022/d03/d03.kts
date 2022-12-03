import java.io.File
import java.util.SortedSet

val input: String = File("input").readText()
val rucksacks: List<String> = input
    .split("\n")
    .filter { it.isNotEmpty() }

// Part 1
print("Answer 1: ")
rucksacks
    .map {
        listOf(it.substring(0, it.length / 2), it.substring(it.length / 2))
    }
    .map { rucksack ->
        rucksack.first().toCharArray().forEach { c ->
            if ( rucksack[1].contains(c, ignoreCase = false)) {
                return@map c
            }
        }
        throw Exception("No item occurs in both compartments") // #Required to keep type List<Char>
    }
    .map {
        if (it.isLowerCase()) {
            return@map it.code - 96
        } else {
            return@map it.code - 38
        }
    }
    .let {
        println(it.sum())
    }

//Part 2
print("Answer 2: ")
val groups: MutableList<MutableList<String>> = mutableListOf()
rucksacks.forEachIndexed { i, r ->
    if (i % 3 == 0) {
        groups.add(mutableListOf<String>())
    }
    groups.last().add(r)
}
groups
    .map { rucks ->
        rucks[0].toCharArray().forEach { c ->
            if (rucks[1].contains(c) && rucks [2].contains(c)) {
                return@map c
            }
        }
        throw Exception("No item occurs in all three rucksacks") // #Required to keep type List<Char>
    }
    .map {
        if (it.isLowerCase()) {
            return@map it.code - 96
        } else {
            return@map it.code - 38
        }
    }
    .let {
        println(it.sum())
    }

