import java.io.File

var lines: MutableList<Pair<Char, Int>> = File("input").readLines().map { it -> Pair(it[0], it.substring(1).toInt()) }.toMutableList()
var headings: List<Pair<Int, Char>> = listOf(Pair(0,'N'), Pair(1,'E'), Pair(2, 'S'), Pair(3, 'W'))
var heading: Char = 'E'
var north: Int = 0
var east: Int = 0

fun rotate(currentHeading: Char, direction: Char, degrees: Int): Char {
    if (degrees % 90 != 0) { println("Warning: non 90 degree rotation") }
    var rotateSteps = ((degrees / 90) % 4) * if (direction == 'L') -1 else 1
    var newHeadingNum = headings.filter { it -> it.second == currentHeading}.first().first + rotateSteps
    if (newHeadingNum > 3) {
        newHeadingNum = newHeadingNum - 4
    } else if (newHeadingNum < 0) {
        newHeadingNum = newHeadingNum + 4
    }
    return headings[newHeadingNum].second
}

fun moveInHeading(h: Char, d: Int) {
    if (h == 'E') {
        east += d
    } else if (h == 'W') {
        east -= d
    } else if (h == 'N') {
        north += d
    } else if (h == 'S') {
        north -= d
    }
}

lines.forEach {println(it)}
lines.forEach {
    if (headings.map {heading -> heading.second}.contains(it.first)) {
        moveInHeading(it.first, it.second)
    } else if (listOf('R','L').contains(it.first)) {
        heading = rotate(heading, it.first, it.second)
    } else if (it.first == 'F') {
        moveInHeading(heading, it.second)
    }
}
println("Part 1: north($north), east($east). Manhattan distance = ${Math.abs(north) + Math.abs(east)}")