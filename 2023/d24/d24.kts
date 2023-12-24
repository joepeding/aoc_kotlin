import java.io.File
import javax.xml.stream.events.EndDocument
import kotlin.Exception
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.streams.toList

data class HailStone(
    val x: Long,
    val y: Long,
    val z: Long,
    val vx: Long,
    val vy: Long,
    val vz: Long
)
fun HailStone.toLineFormula(): Pair<Double, Double> {
    val a = vy.toDouble() / vx.toDouble()
    val yIntersect = y - a * x
    return Pair(a, yIntersect)
}
fun HailStone.intersect(other: HailStone): Pair<Double, Double>? {
    val thisLine = toLineFormula()
    val otherLine = other.toLineFormula()
    if (thisLine.first == otherLine.first) {
        if (thisLine.second == otherLine.second) { throw Exception("Overlapping lines") }
        return null // No intersect
    }
    val a = thisLine.first - otherLine.first
    val b = otherLine.second - thisLine.second
    val x = b.toDouble() / a.toDouble()
    val y = thisLine.first * x + thisLine.second
    return Pair(x, y)
}

val test = false
val src: Triple<String, Long, Long> = if (test) Triple("example", 7L, 27L) else Triple("input", 200000000000000L, 400000000000000L)
val input: List<String> = File(src.first).readText().split("\n").filter { it.isNotBlank() }
val stones = input
    .map { it.split(" @ ").map { it.split(", ").map { it.trim().toLong() } } }
    .map { (cs, vs) -> HailStone(cs[0], cs[1], cs[2], vs[0], vs[1], vs[2]) }

// Part 1
val futureCrossingsInTargetArea = stones.mapIndexed{ i, a ->
    stones.subList(i + 1, stones.size).map { b ->
        val intersect = a.intersect(b)
        intersect to Pair(a, b)
    }.filter { it.first != null }
}.flatten()
.filter { c ->
    c.first!!.toList().all { it >= src.second && it <= src.third }
    // Only future collisions for A
    && c.second.first.let { if(it.vx < 0) c.first!!.first < it.x else c.first!!.first > it.x }
    // Only future collisions for B
    && c.second.second.let { if(it.vx < 0) c.first!!.first < it.x else c.first!!.first > it.x }
}
println("Answer 1: ${futureCrossingsInTargetArea.size}")


// Part 2:
// The input contains a pair of HailStones with the same coordinate and speed in a single dimension
// This solution exploits that, but it won't work on the example input because the trick isn't present there.
val start = stones.mapIndexed { i, a ->
    stones.subList(i + 1, stones.size).firstOrNull() {
        it != a && (
            (it.x == a.x && it.vx == a.vx) ||
            (it.y == a.y && it.vy == a.vy) ||
            (it.z == a.z && it.vz == a.vz)
        )
    }?.let { a to it }
}.filterNotNull().first()
println(start)  // gives me equal z-coords and equal z-velocity for 2 hailstones; rest of code written for
                // a match on the Z-coordinates

// Choose starting coord and speed the same as the matching two hailstones
val zStart = start.first.z
val zVelocity = start.first.vz

// Pick two other stones (first 2 in the list) to calculate at what times to collide with them
val time1 = (zStart - stones[0].z) / (stones[0].vz - zVelocity)
val time2 = (zStart - stones[1].z) / (stones[1].vz - zVelocity)

// Calculate their x coordinates at collision time; from there calculate required stone speed and then start coord
val xTime1 = stones[0].x + stones[0].vx * time1
val xTime2 = stones[1].x + stones[1].vx * time2
val xVelocity = (xTime1 - xTime2) / (time1 - time2)
val xStart = xTime1 - (time1 * xVelocity)

// Calculate their y coordinates at collision time; from there calculate required stone speed and then start coord
val yTime1 = stones[0].y + stones[0].vy * time1
val yTime2 = stones[1].y + stones[1].vy * time2
val yVelocity = (yTime1 - yTime2) / (time1 - time2)
val yStart = yTime1 - (time1 * yVelocity)

// Sum and print answer
println("Answer 2: $xStart + $yStart + $zStart = ${xStart + yStart + zStart}")