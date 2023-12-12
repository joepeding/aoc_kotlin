import java.io.File
import kotlin.Exception
import kotlin.math.ceil
import kotlin.math.max
import kotlin.streams.toList

val input: String = File("input").readText()
val memos: MutableMap<Pair<String, List<Int>>, Long> = mutableMapOf()
var rows = input
    .split("\n")
    .filter { it.isNotBlank() }
    .map { it.split(' ').let { (conditions, groups) -> conditions to groups.split(',').map { it.toInt() } } }

rows.forEach {
    println("$it -> ${it.possibleArrangements()}")
}
println("Answer 1: ${rows.sumOf { it.possibleArrangements() }}")

val rows2 = rows.map { "${it.first}?${it.first}?${it.first}?${it.first}?${it.first}" to listOf(it.second, it.second, it.second, it.second, it.second).flatten() }

rows2.forEach {
    println("$it -> ${it.possibleArrangements()}")
}
println("Answer 2: ${rows2.sumOf { it.possibleArrangements() }}")

fun Pair<String, List<Int>>.possibleArrangements(): Long = memos.getOrPut(this) {
    when {
        first.isEmpty() && second.isEmpty() -> 1 // println("Accepted: $this").let { 1 }
        first.contains('#') && second.isEmpty() -> -1 // println("Rejected: $this").let { -1 }
        first.length < second.sum() -> -1 // println("Rejected: $this").let { -1 }
        first.startsWith('.') -> Pair(first.substring(1), second).possibleArrangements()
        first.startsWith('?') -> {
            val broken = Pair("#${first.substring(1)}", second).possibleArrangements()
            val whole = Pair(".${first.substring(1)}", second).possibleArrangements()
            if (broken < 0) { 0 } else { broken } + if (whole < 0) { 0 } else { whole }
        }
        first.startsWith('#') -> {
            val brokenCount = first.takeWhile { it == '#' }.length
            if (brokenCount > second.first()) {
                -1
            } else if (brokenCount == second.first() && !first.substring(brokenCount).startsWith('#')) {
                val rest = first.substring(brokenCount)
                if (rest.isEmpty()) {
                    Pair(first.substring(brokenCount), second.subList(1, second.size)).possibleArrangements()
                } else {
                    // Skip one more, because the next can't be a broken spring
                    Pair(first.substring(brokenCount + 1), second.subList(1, second.size)).possibleArrangements()
                }
            } else if (brokenCount < second.first() && first[brokenCount] == '?') {
                Pair("${first.substring(0, brokenCount)}#${first.substring(brokenCount + 1)}", second).possibleArrangements()
            } else {
                -1
            }
        }
        else -> throw Exception("Invalid start char ($first)")
    }
}
