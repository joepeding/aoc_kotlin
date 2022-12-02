import java.io.File

var lines: MutableList<Pair<Char, Int>> = File("input").readLines().map { it -> Pair(it[0], it.substring(1).toInt()) }.toMutableList()
var headings: List<Pair<Int, Char>> = listOf(Pair(0,'N'), Pair(1,'E'), Pair(2, 'S'), Pair(3, 'W'))
var heading: Char = 'E'
var north: Int = 0
var east: Int = 0
var waypointN: Int = 1
var waypointE: Int = 10

fun rotate(direction: Char, degrees: Int) {
    if (degrees % 90 != 0) { println("Warning: non 90 degree rotation") }
    var rotateSteps = ((degrees / 90) % 4) * if (direction == 'L') -1 else 1
    var stepsRotated = 0
    while (stepsRotated < Math.abs(rotateSteps)) {
        rotate(direction)
        stepsRotated++
    }
}

fun rotate(direction: Char) {
    var oldN = waypointN
    var oldE = waypointE
    if (direction == 'L') {
        waypointN = oldE
        waypointE = 0 - oldN
    } else {
        waypointE = oldN
        waypointN = 0 - oldE
    }
}

fun moveWaypoint(h: Char, d: Int) {
    if (h == 'E') {
        waypointE += d
    } else if (h == 'W') {
        waypointE -= d
    } else if (h == 'N') {
        waypointN += d
    } else if (h == 'S') {
        waypointN -= d
    }
}

lines.forEach {println(it)}
lines.forEach {
    if (headings.map {heading -> heading.second}.contains(it.first)) {
        moveWaypoint(it.first, it.second)
    } else if (listOf('R','L').contains(it.first)) {
        rotate(it.first, it.second)
    } else if (it.first == 'F') {
        north += it.second * waypointN
        east += it.second * waypointE
    }
}
println("Part 2: north($north), east($east). Manhattan distance = ${Math.abs(north) + Math.abs(east)}")