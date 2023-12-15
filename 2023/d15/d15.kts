import java.io.File
import kotlin.Exception
import kotlin.math.ceil
import kotlin.math.max
import kotlin.streams.toList

val input: String = File("input").readText()
input
    .split(",")
    .filter { it.isNotBlank() }
    .sumOf { it.hash() }
    .also { println("Answer 1: $it") }

fun String.hash(current: Long = 0): Int = map { c -> c.code }.fold(0) { acc, int -> ((acc + int) * 17) % 256 }

val boxes: MutableMap<Int, MutableList<Pair<String, Int>>> = mutableMapOf()
val instructions = input
    .split(",")
    .filter { it.isNotBlank() }
    .forEach { it.execute() }
println(boxes.entries.sortedBy { it.key })
boxes.map { box ->
    box.value.mapIndexed { index, lens -> (box.key + 1) * (index + 1) * lens.second }.sum()
}.sum().also { println("Answer 2: $it") }

fun String.execute(): Unit {
    val label = takeWhile { it in "abcdefghijklmnopqrstuvwxyz" }
    val box = boxes.getOrPut(label.hash()) { mutableListOf() }
    if (contains('-')) {
        box.removeIf { it.first == label }
    } else if (contains('=')) {
        val newLens = Pair(label, last().digitToInt())
        val i = box.indexOfFirst { it.first == label }
        if (i == -1) {
            box.add(newLens)
        } else {
            box.replaceAll { if(it.first == label) { newLens } else { it } }
        }
    }
}

