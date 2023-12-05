import java.io.File
import kotlin.math.exp
import kotlin.math.pow
import kotlin.streams.toList
import kotlin.time.Duration.Companion.seconds

val input: String = File("input").readText()
val part1 = input
    .split("\n")
    .filter { it.isNotBlank() }
    .map { it.substringAfter(": ").trim() }
    .map { it.substringBefore(" | ").split("\\s+".toRegex()) to
            it.substringAfter(" | ").split("\\s+".toRegex())
    }.map { it.second.filter { n -> it.first.contains(n) } }
//    .also { it.forEachIndexed { index, strings -> println("$index: $strings") } }
    .filter { it.isNotEmpty() }
    .sumOf { 2.0.pow(it.toSet().size.toDouble().minus(1)) }

println("Answer 1: ${part1.toInt()}")


val cards: Map<Int, Pair<List<String>, List<String>>> = input
    .split("\n")
    .filter { it.isNotBlank() }
    .associate {
        it.substringBefore(":").substringAfter(' ').trim().toInt() to
        it.substringAfter(": ").trim().let { card ->
            card.substringBefore(" | ").split("\\s+".toRegex()) to
            card.substringAfter(" | ").split("\\s+".toRegex())
        }
    }

val cardValues = cards
    .map { it.key to it.value.second.filter { n -> it.value.first.contains(n) }.size }
    .toMap()

println(cardValues)
println("Answer 2: ${cardValues.map { it.trueValue() }.sum()}")

fun Map.Entry<Int, Int>.trueValue(): Int {
    println("Analysing k:$key, v:$value")
    return 1 + if (value > 0) {
        val newCards = (key + 1)..(key + value)
        println(newCards)
        newCards.sumOf { k ->
            cardValues.entries.firstOrNull { it.key == k }?.trueValue() ?: 0
        }
    } else {
        0
    }
}
