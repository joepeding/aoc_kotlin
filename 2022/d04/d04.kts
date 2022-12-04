import java.io.File

val input: String = File("input").readText()
val pairs: List<List<List<Long>>> = input
    .split("\n")
    .filter { it.isNotEmpty() }
    .map { pair -> pair
        .split(",")
        .map { elf -> elf
            .split("-")
            .map { it.toLong() }
        }
    }

// Part 1
print("Answer 1: ")
pairs
    .filter { pair ->
        // First elf is contained by second:
        (pair[0][0] >= pair[1][0] && pair[0][1] <= pair[1][1])
        ||
        // Second elf is contained by first:
        (pair[0][0] <= pair[1][0] && pair[0][1] >= pair[1][1])
    }
    .let { println(it.count()) }


//Part 2
print("Answer 2: ")
pairs
    .filter { pair ->
        // Any number of elf 1 is contained by elf 2
        (pair[0][0] >= pair[1][0] && pair[0][0] <= pair[1][1] || pair[0][1] >= pair[1][0] && pair[0][1] <= pair[1][1])
        ||
        // Any number of elf 2 is contained by elf 1
        (pair[1][0] >= pair[0][0] && pair[1][0] <= pair[0][1] || pair[1][1] >= pair[0][0] && pair[1][1] <= pair[0][1])
    }
    .let { println(it.count()) }
