import java.io.File
import kotlin.Exception
import kotlin.math.ceil
import kotlin.math.max
import kotlin.streams.toList

val input: String = File("input").readText()
val memos: MutableMap<Pair<String, List<Int>>, Long> = mutableMapOf()
var patterns = input
    .split("\n\n")
    .filter { it.isNotBlank() }
    .map { pattern -> pattern
        .split("\n")
        .filter { it.isNotBlank() }
    }

fun List<String>.leftColumnsMirrored(): List<Int> = this
    .first()
    .indices
    .filter { it > 0 }
    .filter { this.isMirroredAfterCol(it) }

fun List<String>.isMirroredAfterCol(col: Int): Boolean = this.all {
    val left = it.substring(0, col)
    val right = it.substring(col, minOf(col + col, it.length))
    if (left.length > right.length) {
        left.reversed().startsWith(right)
    } else {
        right.startsWith(left.reversed())
    }
}

fun List<String>.topRowsMirrored(row: Int): List<Int> = this
    .indices
    .filter { it > 0 }
    .filter { this.isMirroredAfterRow(it) }

fun List<String>.isMirroredAfterRow(row: Int): Boolean = this.first().mapIndexed { col, _ ->
    val top = this.subList(0, row).map { it[col] }.joinToString("")
    val bottom = this.subList(row, minOf(row + row, this.size)).map { it[col] }.joinToString("")
    if (top.length > bottom.length) {
        top.reversed().startsWith(bottom)
    } else {
        bottom.startsWith(top.reversed())
    }
}.all { it }

fun List<String>.result(): Pair<Int,Int> = Pair(
    this.leftColumnsMirrored().minOrNull() ?: 0,
    this.topRowsMirrored(this.size - 1).minOrNull() ?: 0
)

fun List<String>.newResult(old: Pair<Int, Int>): Pair<Int,Int> = Pair(
    this.leftColumnsMirrored().firstOrNull { it != old.first } ?: 0,
    this.topRowsMirrored(this.size - 1).firstOrNull { it != old.second } ?: 0
)

fun List<String>.mutations(): List<List<String>> = this.mapIndexed { row, s ->
    s.mapIndexed { col, c ->
        this.mapIndexed { index, s ->
            if (index != row) {
                s
            } else {
                val start = s.substring(0, col)
                val char = if (s[col] == '.') "#" else "."
                val end = s.substring(col + 1, s.length)
                "$start$char$end"
            }
        }
    }
}.flatten()

val original = patterns.map { it to it.result() }
println("Answer 1: ${original.sumOf { it.second.first + 100 * it.second.second }}")

val new = original.map { (pattern, old) ->
    pattern to pattern
        .mutations()
        .first { it.newResult(old) != Pair(0, 0)}
        .newResult(old)
        .let { Pair(if (it.first == old.first) 0 else it.first, if (it.second == old.second) 0 else it.second) }
}
println("Answer 2: ${new.sumOf { it.second.first + 100 * it.second.second }}")