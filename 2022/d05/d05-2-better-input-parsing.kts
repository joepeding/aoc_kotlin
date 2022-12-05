import java.io.File

val input: List<String> = File("input").readText().split("\n\n").filter { it.isNotEmpty() }
val commands: List<Triple<Int, Int, Int>> = input[1]
    .split("\n")
    .filter { it.isNotEmpty() }
    .map { it.drop(5) }
    .map { it.split(" ") }
    .map { Triple(it[0].toInt(), it[2].toInt() - 1, it[4].toInt() - 1) }

// Part 1
print("Answer 1: ")
var stacks = parseStacksFromInput(input[0])
commands.forEach { (num, source, dest) ->
    stacks[dest].addAll(stacks[source].takeLast(num).reversed())
    (1..(num)).forEach { _ -> stacks[source].removeLast() }
}
stacks.map { it.lastOrNull() }.let { println(it) }

//Part 2 (parse input again, because original was modified)
print("Answer 2: ")
stacks = parseStacksFromInput(input[0])
commands.forEach { (num, source, dest) ->
    stacks[dest].addAll(stacks[source].takeLast(num))
    (1..(num)).forEach { _ -> stacks[source].removeLast() }
}
stacks.map { it.lastOrNull() }.let { println(it) }

fun parseStacksFromInput(input: String) = input
    .split("\n")
    .map { it.drop(1) }
    .map { it.windowed(1, 4, partialWindows = false) }
    .dropLast(1)
    .reversed()
    .let { lines ->
        lines.first().mapIndexed { i, _ ->
            lines.map { it[i] }.filter { it.isNotBlank() }.toMutableList()
        }
    }
