import java.io.File

val input: String = File("input").readText().trim()

// Part 1
println("All inputs unique: ${input.split("\n").map{ it.toInt() }.let { it.size == it.distinct().size }}")

print("Answer 1: ")
println(mix())

// Part 2
print("Answer 2: ")
println(mix(811589153, 10))

fun mix(key: Int = 1, times: Int = 1): Long {
    // Parse
    val numbers: MutableList<Pair<Long, Long>> = input
        .split("\n")
        .mapIndexed { i, s -> Pair(i.toLong(), s.toLong() * key) }
        .toMutableList()

    // Mix
    var originalSize = numbers.size
    repeat(times) {
        for (i in 0L until originalSize.toLong()) {
            // Find element by original index, and remove it from current position
            var n = numbers.first { it.first == i }
            var currentIndex = numbers.indexOf(n)
            numbers.remove(n)

            // Create new index, and add it back at that position
            var newIndex = ((currentIndex + n.second) % (originalSize - 1))
            if (newIndex < 0) { newIndex = originalSize - 1 + newIndex }
            numbers.add(newIndex.toInt(), n)
        }
    }

    // Outcome:
    var zeroIndex = numbers.indexOf(numbers.first { it.second == 0L } )
    return listOf(1000, 2000, 3000).map { it + zeroIndex }.map { numbers[it % numbers.size]!!.second }.also { println(it) }.sum()
}
