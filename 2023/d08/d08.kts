import java.io.File
import kotlin.Exception

val input: String = File("input").readText()
val instructions: String = input.substringBefore("\n\n").trim()
val locations = input
    .substringAfter("\n\n")
    .split("\n")
    .filter { it.isNotBlank() }
    .associate { it.substring(0, 3) to Pair(it.substring(7, 10), it.substring(12, 15)) }


var steps = 0
var currentLoc = "AAA"
while (currentLoc != "ZZZ") {
    currentLoc = if (instructions[steps % instructions.length] == 'L') {
        locations[currentLoc]!!.first
    } else {
        locations[currentLoc]!!.second
    }
    steps++
}
println("Answer 1: $steps")

var runners = locations
    .filter { it.key.endsWith('A') }
    .map { it.key }
var ends = locations
    .filter { it.key.endsWith('Z') }
    .map { it.key }
println("\n\nStarts: $runners\nEnds: $ends")

fun Int.toPrimeFactors() = (1..maxOf(1, this / 2)).filter {
    this % it == 0
}

var steps2 = 0
runners.map {
    var steps = 0
    var currentLoc = it
    while (currentLoc.endsWith('Z').not()) {
        currentLoc = if (instructions[steps % instructions.length] == 'L') {
            locations[currentLoc]!!.first
        } else {
            locations[currentLoc]!!.second
        }
        steps++
    }
    steps
}.flatMap { it.toPrimeFactors() }
.toSet()
.map { it.toLong() }
.fold(1L) { a, b -> a * b }
.let { println("Answer 2: $it") }
