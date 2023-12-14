import java.io.File
import kotlin.Exception
import kotlin.math.ceil
import kotlin.math.max
import kotlin.streams.toList

val input: String = File("input").readText()
val states: MutableMap<List<String>, List<String>> = mutableMapOf()
input
    .split("\n")
    .filter { it.isNotBlank() }
    .rollNorthRecursively()
    .northLoad()
    .also { println("Answer 1: $it") }

var start = input
    .split("\n")
    .filter { it.isNotBlank() }

(0 until 1000000000).forEach {
    if ( it % 100000 == 0) { println(it) }
    start = start.memoizedSpin()
}

start
    .northLoad()
    .also { println("Answer 2: $it") }


fun List<String>.transpose(): List<String> = first().indices
    .map { col ->
        this.map { s -> s[col] }.joinToString("")
    }

fun List<String>.rollNorth(): List<String> = mapIndexed { row, s ->
    s.mapIndexed { col, c ->
        if (c == '.' && row + 1 < this.size && this[row + 1][col] == 'O') {
            'O'
        } else if (c == 'O' && row - 1 >= 0 && this[row - 1][col] == '.') {
            '.'
        } else {
            c
        }
    }.joinToString("")
}
fun List<String>.rollNorthRecursively(): List<String> = rollNorth().let {
    if (it == this) this else it.rollNorthRecursively()
}

fun List<String>.rollWestRecursively(): List<String> = map {it.rollWestRecursively() }
fun String.rollWestRecursively(): String = rollWest().let {
    if (it == this) this else it.rollWestRecursively()
}
fun String.rollWest(): String = when {
    length < 2 -> this
    startsWith(".O") -> "O${".${substring(2)}".rollWest()}"
    else -> "${first()}${substring(1).rollWest()}"
}

fun List<String>.rollSouthRecursively(): List<String> = this.reversed().rollNorthRecursively().reversed()
fun List<String>.rollEastRecursively(): List<String> = map { it.reversed() }.rollWestRecursively().map { it.reversed() }

fun List<String>.spin(): List<String> = this
    .rollNorthRecursively()
    .rollWestRecursively()
    .rollSouthRecursively()
    .rollEastRecursively()

fun List<String>.memoizedSpin(): List<String> = states.getOrPut(this) { spin() }

fun List<String>.northLoad() = this.transpose().sumOf { it.reversed().mapIndexed { index, c -> if (c == 'O') index + 1 else 0 }.sum() }
