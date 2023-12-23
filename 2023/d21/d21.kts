import java.io.File
import javax.xml.stream.events.EndDocument
import kotlin.Exception
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.streams.toList

fun Long.sq() = this * this

val input: List<String> = File("input").readText().split("\n").filter { it.isNotBlank() }
val plots = input.joinToString("")
val w = input.first().length
fun Long.getPlotChar() = (this % plots.length).let { if (it < 0) { it + plots.length } else it }.let { plots[it.toInt()] }
fun Long.surroundingPlots(): List<Long> = listOf(
    this + 1,
    this - 1,
    this + w,
    this - w
)

val s = plots.indexOf('S').toLong()
println(s.getPlotChar())
println(s.surroundingPlots().map { it.getPlotChar() })

val seen: MutableMap<Long, Int> = mutableMapOf(s to 0)
var front: MutableList<Long> = mutableListOf(s)
for (i in 1..64) {
    front = front
        .map { it.surroundingPlots() }
        .flatten()
        .filter { it !in seen.keys }
        .distinct()
        .filter { it.getPlotChar() != '#' }
        .onEach {
            seen[it] = i
        }.toMutableList()
}
println("Answer 1: ${seen.filter { it.value < 65 && it.value % 2 == 0 }.count()}")

// Part 2
data class Coord(val row: Int, val col: Int)
val start: Coord = plots.indexOf('S').let { Coord(it / w, it % w) }
val seen2: MutableMap<Coord, Int> = mutableMapOf(start to 0)
fun Coord.getPlotChar() = input[(row % w).let { if (it < 0) it + w else it }][(col % w).let { if (it < 0) it + w else it }]
fun Coord.surrounding() = listOf(
    Coord(row - 1, col),
    Coord(row + 1, col),
    Coord(row, col - 1),
    Coord(row, col + 1)
)
var front2: MutableList<Coord> = mutableListOf(start)
var previous = front2.toMutableList().toList()

data class Metric(val count: Long = 1L, val step: Long = 0L, val stepInc: Long = 0L)
var runningCount = 0L
val metrics = mutableMapOf<Int, Metric>(0 to Metric())

for (i in 1..1000) {
    val temp = front2
    front2 = front2
        .map { it.surrounding() }
        .flatten()
        .filter { it !in seen2.keys }
        .distinct()
        .filter { it.getPlotChar() != '#' }
        .onEach {
            seen2[it] = i
        }.toMutableList()
    previous = temp

    // Increment count every other step with new cells
    if ( i % 2 == 1) { runningCount += front2.size }

    // Record some metrics every cycle
    if (i % 262 == 65) {
        val lastMetric = metrics[metrics.keys.max()]!!
        val step = runningCount - lastMetric.count
        val stepInc = step - lastMetric.step
        Metric(
            runningCount,
            step,
            stepInc
        ).also { println("$i: $it") }.let { metrics[i] = it }
    }
}

var step = metrics.keys.max()
val lastMetric = metrics[step]!!
var count = lastMetric.count
val acceleration = lastMetric.stepInc
var stepSize = lastMetric.step + acceleration

while (step < 26501365) {
    step += 262
    count += stepSize
    stepSize += acceleration
}

println("Answer 2: $count")
