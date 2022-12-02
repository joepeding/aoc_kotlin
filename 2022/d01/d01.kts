import java.io.File
import java.util.SortedSet

val input: String = File("input").readText()
val inventories: List<List<Long>> = input
    .split("\n\n")
    .map { inventory ->
        inventory
            .split("\n")
            .filter { snack -> snack.isNotEmpty() }
            .map { it.toLong() }
    }
val inventorySums = inventories.map { it.sum() }
print("Answer 1: ")
println(inventorySums.max())

print("Answer 2: ")
inventorySums
    .sortedDescending()
    .let { println(it[0] + it[1] + it[2]) }


