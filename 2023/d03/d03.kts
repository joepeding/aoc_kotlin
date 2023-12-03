import java.io.File
import kotlin.math.exp
import kotlin.streams.toList

val input: String = File("input").readText()
val rows: List<List<Char>> = input
    .split("\n")
    .filter { it.isNotBlank() }
    .map { string -> string.chars().toList().map { it.toChar() } }

val numbers: MutableSet<Pair<Int, Int>> = mutableSetOf()

print("Answer 1: ")
rows.forEachIndexed { rowNum, chars ->
    chars.forEachIndexed { colNum, c ->
        if (!c.isDigit() && c != '.') {
            numbers.addAll(findAdjacentNumbers(rowNum, colNum))
        }
    }
}
println(numbers.sumOf { expandNumber(it).toInt() })

print("Answer 2: ")
var part2 = 0
rows.forEachIndexed { rowNum, chars ->
    chars.forEachIndexed { colNum, c ->
        if (c == '*') {
            val adjacentNumbers = findAdjacentNumbers(rowNum, colNum)
            if (adjacentNumbers.size == 2) {
                part2 += expandNumber(adjacentNumbers.first()).toInt() * expandNumber(adjacentNumbers.last()).toInt()
            }
        }
    }
}
println(part2)

fun findAdjacentNumbers(rowNum: Int, colNum: Int): Set<Pair<Int, Int>> =
    ((rowNum - 1)..(rowNum + 1)).map { r ->
        ((colNum - 1)..(colNum + 1)).map { c -> Pair(r, c) }
    }
    .flatten()
    .filter { rows.getOrNull(it.first)?.getOrNull(it.second)?.isDigit() ?: false }
    .map { findNumberStart(it) }
    .toSet()

fun findNumberStart(coord: Pair<Int, Int>): Pair<Int, Int> {
    return when {
        rows[coord.first].getOrNull(coord.second - 1)?.isDigit() ?: false -> findNumberStart(Pair(coord.first, coord.second - 1))
        else -> coord
    }
}

fun expandNumber(coord: Pair<Int, Int>): String {
    var number = "${rows[coord.first][coord.second]}"
    if (rows[coord.first].getOrNull(coord.second + 1)?.isDigit() == true) {
        number = "$number${expandNumber(Pair(coord.first, coord.second + 1))}"
    }
    return number
}
