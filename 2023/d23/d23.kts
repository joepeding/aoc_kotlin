import D23.Coord
import D23.Direction.*
import java.io.File
import javax.xml.stream.events.EndDocument
import kotlin.Exception
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.streams.toList

data class Coord(val row: Int, val col: Int, val char: Char)
data class Route(val start: Coord, val startDir: Direction, val end: Coord, val endDir: Direction, val len: Int)
enum class Direction { NORTH, SOUTH, EAST, WEST }
fun Coord.nextInDirection(dir: Direction) = when(dir) {
    NORTH -> coords.getOrNull(row - 1)?.getOrNull(col)
    SOUTH -> coords.getOrNull(row + 1)?.getOrNull(col)
    EAST -> coords.getOrNull(row)?.getOrNull(col + 1)
    WEST -> coords.getOrNull(row)?.getOrNull(col - 1)
}
fun Coord.surrounding() = listOfNotNull(
    nextInDirection(NORTH),
    nextInDirection(SOUTH),
    nextInDirection(WEST),
    nextInDirection(EAST),
).filter { it.char != '#' }
fun Direction.opposite(): Direction = when(this) {
    NORTH -> SOUTH
    SOUTH -> NORTH
    EAST -> WEST
    WEST -> EAST
}

val input: List<String> = File("input").readText().split("\n").filter { it.isNotBlank() }
val coords = input
    .mapIndexed { row, s ->
        s.mapIndexed { col, c -> Coord(row, col, c) }
    }

val start = coords.first().first { it.char == '.' }
val end = coords.last().first { it.char == '.' }
val routes = mutableListOf<Route>()
fun walkAllNewDirections(newStart: Coord, ignoreSlopes: Boolean = false): Unit {
    val queue: MutableList<Coord> = mutableListOf(newStart)
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        current
            .surrounding()
            .filter { routes.none { r -> r.start == it } }
            .mapNotNull {
                when (it) {
                    current.nextInDirection(WEST) -> it.nextCrossingComingFrom(EAST, ignoreSlopes)
                    current.nextInDirection(NORTH) -> it.nextCrossingComingFrom(SOUTH, ignoreSlopes)
                    current.nextInDirection(EAST) -> it.nextCrossingComingFrom(WEST, ignoreSlopes)
                    current.nextInDirection(SOUTH) -> it.nextCrossingComingFrom(NORTH, ignoreSlopes)
                    else -> throw Exception("Invalid dir? $it")
                }
            }.forEach {
                routes.add(it)
                queue.add(it.end)
            }
    }
}
fun Coord.nextCrossingComingFrom(dir: Direction, ignoreSlopes: Boolean = false): Route? = if (this == end)
    Route(this, SOUTH, this, SOUTH, 0)
else if (!ignoreSlopes && ((this.char == 'v' && dir == SOUTH) || (this.char == '>' && dir == EAST)))
    null
else
    surrounding()
    .filter { it != nextInDirection(dir) }
    .let {
        if (it.size == 1) {
            when (it.first()) {
                nextInDirection(WEST) -> it.first().nextCrossingComingFrom(EAST, ignoreSlopes)
                nextInDirection(NORTH) -> it.first().nextCrossingComingFrom(SOUTH, ignoreSlopes)
                nextInDirection(EAST) -> it.first().nextCrossingComingFrom(WEST, ignoreSlopes)
                nextInDirection(SOUTH) -> it.first().nextCrossingComingFrom(NORTH, ignoreSlopes)
                else -> throw Exception("Invalid dir?")
            }?.let { route ->
                Route(this, dir.opposite(), route.end, route.endDir, route.len + 1)
            }
        } else if (it.isEmpty()) {
//            throw Exception("Dead end?")
            return null
        } else {
            Route(this, dir.opposite(), this, dir.opposite(), 0)
        }
    }

routes.add(start.nextCrossingComingFrom(NORTH)!!)
walkAllNewDirections(routes.first().end)

fun maxStepsToEnd(route: Route, visited: List<Coord>): Int? {
    if (route.end == end) { return route.len }
    val routeLengths = route.end.surrounding()
        .mapNotNull { rStart -> routes.firstOrNull { it.start == rStart } }
        .filter { it.end !in visited }
        .filter { it.start != route.end.nextInDirection(route.endDir.opposite()) } // pt2
        .mapNotNull { maxStepsToEnd(it, listOf(visited, listOf(it.end)).flatten()) }
    if (routeLengths.isEmpty()) {
        return null
    } else {
        return route.len + 1 + routeLengths.max()
    }
}

val maxRoute = maxStepsToEnd(routes.first(), listOf(start))
println("Answer 1: $maxRoute")

routes.clear()
routes.add(start.nextCrossingComingFrom(NORTH, true)!!)
walkAllNewDirections(routes.first().end, true)
println("Answer 2: ${maxStepsToEnd(routes.first(), listOf(start))}")