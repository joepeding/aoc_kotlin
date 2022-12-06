import java.io.File

val input = File("input").readText()

// Part 1
println("Answer 1: ")
input
    .windowed(4, 1, false)
    .mapIndexed { i, s -> Pair(i, s.toCharArray().toList().distinct().size == 4) }
    .filter { it.second }
    .first()
    .let { println(it.first + 4) }


//Part 2 (parse input again, because original was modified)
println("Answer 2: ")
input
    .windowed(14, 1, false)
    .mapIndexed { i, s -> Pair(i, s.toCharArray().toList().distinct().size == 14) }
    .filter { it.second }
    .first()
    .let { println(it.first + 14) }
