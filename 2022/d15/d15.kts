import java.io.File
import kotlin.math.min

val input: String = File("input").readText()

// Part 1
print("Answer 1: ")
val (sensors, beacons) = input
    .split("\n")
    .filter { it.isNotEmpty() }
    .map {
        it
            .split(":")
            .map {
                it
                    .split("at x=", ", y=")
                    .let{ Pair(it[1].toInt(), it[2].toInt())}
            }
    }.let { all -> Pair(all.map {it[0]}, all.map {it[1]}) }

val sensorsWithDistances = sensors
    .mapIndexed { i, a ->
        val b = beacons[i]!!
        val dist: Int = kotlin.math.abs(a.first - b.first) + kotlin.math.abs(a.second - b.second)
        return@mapIndexed Triple(a.first, a.second, dist)
    }

fun isNotABeacon(a: Pair<Int, Int>): Boolean {
    if (beacons.contains(a)) {
        return false
    }
    if (sensors.contains(a)) {
        return true
    }
    sensorsWithDistances.forEach { b ->
        if (kotlin.math.abs(a.first - b.first) + kotlin.math.abs(a.second - b.second) <= b.third) {
            return true
        }
    }
    return false
}

var minX = sensorsWithDistances.map { (x, _, dist) -> x - dist }.min() + 5
var maxX = sensorsWithDistances.map { (x, _, dist) -> x + dist }.max() + 5
var count = 0
(minX..maxX).forEach { x ->
    if (isNotABeacon(Pair(x, 2000000))) {
        count++
    }
}
println(count)



print("Answer 2: ")
var coord = Pair(0,0)
sensorsWithDistances
    .map { (x, y, dist) ->
        pointsAtManhattanDistanceAround(x, y, dist + 1)
    }
    .flatten()
    .forEach {(x, y) ->
        if (isTheBeacon(Pair(x, y))) {
            coord = Pair(x, y)
            return@forEach
        }
    }
println(coord.first.toLong() * 4000000L + coord.second.toLong())

fun isTheBeacon(a: Pair<Int, Int>): Boolean {
    if (beacons.contains(a) || sensors.contains(a) || a.first < 0 || a.first > 4000000 || a.second < 0 || a.second > 4000000) {
        return false
    }
    sensorsWithDistances.forEach { b ->
        if (kotlin.math.abs(a.first - b.first) + kotlin.math.abs(a.second - b.second) <= b.third) {
            return false
        }
    }
    return true
}

fun pointsAtManhattanDistanceAround(x: Int, y: Int, dist: Int): List<Pair<Int, Int>> {
    var out = mutableListOf<Pair<Int, Int>>()
    return listOf(
        (x..(x+dist)).mapIndexed { i, n -> listOf(Pair(n, y + (dist-i)), Pair(n, y - (dist-i))) }.flatten(),
        ((x-dist)..x).mapIndexed { i, n -> listOf(Pair(n, y + (dist-i)), Pair(n, y - (dist-i))) }.flatten()
    ).flatten()
}
