import java.io.File
import kotlin.math.max
import kotlin.math.min

val input = File("input").readText().split("\n").filter { it.isNotBlank() }

// Part 1
println("Answer 1: ")
var count = 0;
var maxCoord = input.size - 1
input
    .forEachIndexed { y, row ->
        row.forEachIndexed { x, c ->
            when {
                y == 0 || y == maxCoord || x == 0 || x == maxCoord -> count++ // outer edge
                (0..(y - 1)).map { input[it][x] }.all { it < c } -> count++ // To top
                ((y + 1)..maxCoord).map { input[it][x] }.all { it < c } -> count++ // To bottom
                (0..(x - 1)).map { input[y][it] }.all { it < c } -> count++ // To left
                ((x + 1)..maxCoord).map { input[y][it] }.all { it < c } -> count++ // To right
            }
        }
    }
println(count)


//Part 2 (parse input again, because original was modified)
println("Answer 2: ")
input
    .mapIndexed { y, row ->
        row.mapIndexed { x, c ->
            if(x == 0 || y == 0 || x == maxCoord || y == maxCoord) { return@mapIndexed 0 }
            listOf(
                (0..(max(y-1, 0))).map { input[it][x] }.reversed(), // To top
                (min(maxCoord, y+1)..maxCoord).map { input[it][x] }, // To bottom
                (0..(max(x-1, 0))).map { input[y][it] }.reversed(), // To left
                (min(maxCoord, x+1)..maxCoord).map { input[y][it] } // To right
            ).map {
                it.forEachIndexed { index, c ->
                    if (c >= input[y][x]) { return@map index + 1 }
                }
                return@map it.size
            }.let {
                it[0] * it[1] * it[2] * it[3]
            }
        }
    }.flatten().max()
