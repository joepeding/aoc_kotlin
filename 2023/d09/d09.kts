import java.io.File
import kotlin.Exception

val input: String = File("input").readText()
val sequences = input
    .split("\n")
    .filter { it.isNotBlank() }
    .map { it.split(' ').map { s -> s.toLong() } }

println("Answer 1: ${sequences.sumOf { it.nextNumber() }}")
println("Answer 2: ${sequences.sumOf { it.previousNumber() }}")

fun List<Long>.diffs(): List<Long> = this.windowed(2) { (a, b) -> b - a }

fun List<Long>.nextNumber(): Long = this.diffs().let { diffs ->
    if (diffs.distinct().size == 1) {
        this.last() + (this[1] - this[0])
    } else {
        this.last() + diffs.nextNumber()
    }
}

fun List<Long>.previousNumber(): Long = this.diffs().let { diffs ->
    if (diffs.distinct().size == 1) {
        this.first() - (this[1] - this[0])
    } else {
        this.first() - diffs.previousNumber()
    }
}