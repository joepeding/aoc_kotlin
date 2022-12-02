import java.io.File
import java.util.SortedSet

val input: String = File("input").readText()
val inventories: List<List<Long>> = input.split("\n\n").map { it.split("\n").filter { it.isNotEmpty() }.map { it.toLong() } }
val inventorySums = inventories.map { it.sum() }
println("answer 1:")
println(inventorySums.max())

println("answer 2:")
inventorySums.sortedDescending().also { println(it) }.also { println(it[0] + it[1] + it[2]) }


