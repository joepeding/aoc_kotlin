import java.io.File
import java.util.Currency
import kotlin.Exception
import kotlin.math.ceil
import kotlin.streams.toList

data class Coord(val row: Int, val col: Int, var status: CoordStatus = CoordStatus.UNKNOWN)
enum class CoordStatus { FREE, ENCLOSED, LOOP, UNKNOWN }
enum class CoordSide { NORTH, SOUTH, EAST, WEST }

val input: String = File("input").readText()
var sequences = input
    .split("\n")
    .map { it.chars().toList().map { c -> c.toChar() } }
val allCoords = sequences
    .mapIndexed { row, chars -> chars.mapIndexed { col, c -> Coord(row, col) } }

fun Coord.outOfBounds(): Boolean = this.row < 0 || this.col < 0 || this.row >= sequences.size || this.col >= sequences.first().size
fun Coord.getChar(): Char = sequences.getOrNull(this.row)?.getOrNull(this.col) ?: throw Exception("Out of bounds")
fun Coord.getNorth(): Coord = allCoords.getOrNull(this.row - 1)?.getOrNull(this.col) ?: throw Exception("Out of bounds")
fun Coord.getSouth(): Coord = allCoords.getOrNull(this.row + 1)?.getOrNull(this.col) ?: throw Exception("Out of bounds")
fun Coord.getWest(): Coord = allCoords.getOrNull(this.row)?.getOrNull(this.col - 1) ?: throw Exception("Out of bounds")
fun Coord.getEast(): Coord = allCoords.getOrNull(this.row)?.getOrNull(this.col + 1) ?: throw Exception("Out of bounds")
fun Coord.getNorthOrNull(): Coord? = try { getNorth() } catch (_: Exception) { null }
fun Coord.getSouthOrNull(): Coord? = try { getSouth() } catch (_: Exception) { null }
fun Coord.getWestOrNull(): Coord? = try { getWest() } catch (_: Exception) { null }
fun Coord.getEastOrNull(): Coord? = try { getEast() } catch (_: Exception) { null }
fun Coord.getSurrounding(): List<Coord> = listOf(getNorthOrNull(), getSouthOrNull(), getWestOrNull(), getEastOrNull()).filterNotNull()
fun Coord.getConnecting(): Pair<Coord, Coord> = when(this.getChar()) {
    '|' -> Pair(this.getNorth(), this.getSouth())
    '-' -> Pair(this.getWest(), this.getEast())
    'L' -> Pair(this.getNorth(), this.getEast())
    'J' -> Pair(this.getNorth(), this.getWest())
    '7' -> Pair(this.getSouth(), this.getWest())
    'F' -> Pair(this.getSouth(), this.getEast())
    '.' -> throw Exception("Ground")
    'S' -> this.getSurrounding().filter { c -> c.getChar() != '.' && c.getConnecting().toList().contains(this) }.let { Pair(it[0], it[1]) }
    else -> throw Exception("Invalid char")
}

// Deep recursive function that walks along the loop to determine its length
// Needs to be deep recursive to prevent stack overflow on real input
val stepsToStart = DeepRecursiveFunction<Pair<Coord, Coord>, Long> { (here, from) ->
    here
        .getConnecting()
        .toList()
        .first { it != from }
        .let {
            when {
                it.outOfBounds() -> Long.MAX_VALUE
                it.getChar() == '.' -> Long.MAX_VALUE
                it.getChar() == 'S' -> 1
                else -> 1L + callRecursive(Pair(it, here))//it.numberOfStepsToStart(this)
            }
        }
}

// Part 1
val start = allCoords
    .flatten()
    .first { it.getChar() == 'S' }
start.getSurrounding()
    .filter { it.getChar() != '.' }
    .filter { it.getConnecting().toList().contains(start) }
    .map { it to stepsToStart(Pair(it, start)) }
    .maxOf { it.second }
    .let { println("Answer 1: ${ceil(it.toDouble() / 2)}") }


// Part 2
// Determine what char it should have been for later replacement
val startChar = start.let {
    val n = try { it.getNorth() } catch (_: Exception) { null }
    val s = try { it.getSouth() } catch (_: Exception) { null }
    val e = try { it.getEast() } catch (_: Exception) { null }
    val w = try { it.getWest() } catch (_: Exception) { null }
    when {
        start.getConnecting().toList().containsAll(listOf(n,s)) -> '|'
        start.getConnecting().toList().containsAll(listOf(e,w)) -> '-'
        start.getConnecting().toList().containsAll(listOf(e,s)) -> 'F'
        start.getConnecting().toList().containsAll(listOf(w,s)) -> '7'
        start.getConnecting().toList().containsAll(listOf(e,n)) -> 'L'
        start.getConnecting().toList().containsAll(listOf(w,n)) -> 'J'
        else -> throw Exception("Invalid combination")
    }
}

// Define a deep recursive function to move along the loop while keeping track of what the free side is
// in order to collect a list of non-loop cells adjacent to the loop's free side
data class AdjacentFreeFieldState(val start: Coord, val current: Coord, val freeSide: CoordSide, val last: Coord)
val loopAdjacentFreeFields = DeepRecursiveFunction<AdjacentFreeFieldState, List<Coord>> { (start, current, freeSide, last) ->
    val next = current.getConnecting().toList().first { it != last }
    print("Loop: checking $current (${current.getChar()}), freeside $freeSide, moving to (${next.getChar()}) $next")
    val newFreeSide = when {
        current.getChar() == '|' -> freeSide

        current.getChar() == '-' -> freeSide

        current.getChar() == 'L' && next.getChar() == '-' && listOf(CoordSide.WEST, CoordSide.SOUTH).contains(freeSide) -> CoordSide.SOUTH
        current.getChar() == 'L' && next.getChar() == '-' && listOf(CoordSide.EAST, CoordSide.NORTH).contains(freeSide) -> CoordSide.NORTH
        current.getChar() == 'L' && next.getChar() == '|' && listOf(CoordSide.WEST, CoordSide.SOUTH).contains(freeSide) -> CoordSide.WEST
        current.getChar() == 'L' && next.getChar() == '|' && listOf(CoordSide.EAST, CoordSide.NORTH).contains(freeSide) -> CoordSide.EAST
        current.getChar() == 'L' && next.getChar() == 'J' && listOf(CoordSide.WEST, CoordSide.SOUTH).contains(freeSide) -> CoordSide.SOUTH
        current.getChar() == 'L' && next.getChar() == 'J' && listOf(CoordSide.EAST, CoordSide.NORTH).contains(freeSide) -> CoordSide.NORTH
        current.getChar() == 'L' && next.getChar() == '7' -> freeSide
        current.getChar() == 'L' && next.getChar() == 'F' && listOf(CoordSide.WEST, CoordSide.SOUTH).contains(freeSide) -> CoordSide.WEST
        current.getChar() == 'L' && next.getChar() == 'F' && listOf(CoordSide.EAST, CoordSide.NORTH).contains(freeSide) -> CoordSide.EAST

        current.getChar() == 'J' && next.getChar() == '-' && listOf(CoordSide.WEST, CoordSide.NORTH).contains(freeSide) -> CoordSide.NORTH
        current.getChar() == 'J' && next.getChar() == '-' && listOf(CoordSide.EAST, CoordSide.SOUTH).contains(freeSide) -> CoordSide.SOUTH
        current.getChar() == 'J' && next.getChar() == '|' && listOf(CoordSide.WEST, CoordSide.NORTH).contains(freeSide) -> CoordSide.WEST
        current.getChar() == 'J' && next.getChar() == '|' && listOf(CoordSide.EAST, CoordSide.SOUTH).contains(freeSide) -> CoordSide.EAST
        current.getChar() == 'J' && next.getChar() == 'L' && listOf(CoordSide.WEST, CoordSide.NORTH).contains(freeSide) -> CoordSide.NORTH
        current.getChar() == 'J' && next.getChar() == 'L' && listOf(CoordSide.EAST, CoordSide.SOUTH).contains(freeSide) -> CoordSide.SOUTH
        current.getChar() == 'J' && next.getChar() == '7' && listOf(CoordSide.WEST, CoordSide.NORTH).contains(freeSide) -> CoordSide.WEST
        current.getChar() == 'J' && next.getChar() == '7' && listOf(CoordSide.EAST, CoordSide.SOUTH).contains(freeSide) -> CoordSide.EAST
        current.getChar() == 'J' && next.getChar() == 'F' -> freeSide

        current.getChar() == '7' && next.getChar() == '-' && listOf(CoordSide.EAST, CoordSide.NORTH).contains(freeSide) -> CoordSide.NORTH
        current.getChar() == '7' && next.getChar() == '-' && listOf(CoordSide.WEST, CoordSide.SOUTH).contains(freeSide) -> CoordSide.SOUTH
        current.getChar() == '7' && next.getChar() == '|' && listOf(CoordSide.EAST, CoordSide.NORTH).contains(freeSide) -> CoordSide.EAST
        current.getChar() == '7' && next.getChar() == '|' && listOf(CoordSide.WEST, CoordSide.SOUTH).contains(freeSide) -> CoordSide.WEST
        current.getChar() == '7' && next.getChar() == 'L' -> freeSide
        current.getChar() == '7' && next.getChar() == 'F' && listOf(CoordSide.EAST, CoordSide.NORTH).contains(freeSide) -> CoordSide.WEST
        current.getChar() == '7' && next.getChar() == 'F' && listOf(CoordSide.WEST, CoordSide.SOUTH).contains(freeSide) -> CoordSide.EAST
        current.getChar() == '7' && next.getChar() == 'J' && listOf(CoordSide.EAST, CoordSide.NORTH).contains(freeSide) -> CoordSide.EAST
        current.getChar() == '7' && next.getChar() == 'J' && listOf(CoordSide.WEST, CoordSide.SOUTH).contains(freeSide) -> CoordSide.WEST

        current.getChar() == 'F' && next.getChar() == '-' && listOf(CoordSide.EAST, CoordSide.SOUTH).contains(freeSide) -> CoordSide.SOUTH
        current.getChar() == 'F' && next.getChar() == '-' && listOf(CoordSide.WEST, CoordSide.NORTH).contains(freeSide) -> CoordSide.NORTH
        current.getChar() == 'F' && next.getChar() == '|' && listOf(CoordSide.EAST, CoordSide.SOUTH).contains(freeSide) -> CoordSide.EAST
        current.getChar() == 'F' && next.getChar() == '|' && listOf(CoordSide.WEST, CoordSide.NORTH).contains(freeSide) -> CoordSide.WEST
        current.getChar() == 'F' && next.getChar() == 'L' && listOf(CoordSide.EAST, CoordSide.SOUTH).contains(freeSide) -> CoordSide.EAST
        current.getChar() == 'F' && next.getChar() == 'L' && listOf(CoordSide.WEST, CoordSide.NORTH).contains(freeSide) -> CoordSide.WEST
        current.getChar() == 'F' && next.getChar() == '7' && listOf(CoordSide.EAST, CoordSide.SOUTH).contains(freeSide) -> CoordSide.WEST
        current.getChar() == 'F' && next.getChar() == '7' && listOf(CoordSide.WEST, CoordSide.NORTH).contains(freeSide) -> CoordSide.EAST
        current.getChar() == 'F' && next.getChar() == 'J' && listOf(CoordSide.EAST, CoordSide.SOUTH).contains(freeSide) -> CoordSide.SOUTH
        current.getChar() == 'F' && next.getChar() == 'J' && listOf(CoordSide.WEST, CoordSide.NORTH).contains(freeSide) -> CoordSide.NORTH

        else -> throw Exception("Invalid char combination")
    }

    when {
        current.getChar() == '|' && freeSide == CoordSide.EAST -> listOfNotNull(current.getEastOrNull())
        current.getChar() == '|' && freeSide == CoordSide.WEST -> listOfNotNull(current.getWestOrNull())

        current.getChar() == '-' && freeSide == CoordSide.SOUTH -> listOfNotNull(current.getSouthOrNull())
        current.getChar() == '-' && freeSide == CoordSide.NORTH -> listOfNotNull(current.getNorthOrNull())

        current.getChar() == 'L' && (freeSide == CoordSide.NORTH || freeSide == CoordSide.EAST) -> listOfNotNull()
        current.getChar() == 'L' && (freeSide == CoordSide.SOUTH || freeSide == CoordSide.WEST) -> listOfNotNull(current.getSouthOrNull(), current.getWestOrNull())

        current.getChar() == 'J' && (freeSide == CoordSide.NORTH || freeSide == CoordSide.WEST) -> listOfNotNull()
        current.getChar() == 'J' && (freeSide == CoordSide.SOUTH || freeSide == CoordSide.EAST) -> listOfNotNull(current.getSouthOrNull(), current.getEastOrNull())

        current.getChar() == '7' && (freeSide == CoordSide.NORTH || freeSide == CoordSide.EAST) -> listOfNotNull(current.getNorthOrNull(), current.getEastOrNull())
        current.getChar() == '7' && (freeSide == CoordSide.SOUTH || freeSide == CoordSide.WEST) -> listOfNotNull()

        current.getChar() == 'F' && (freeSide == CoordSide.NORTH || freeSide == CoordSide.WEST) -> listOfNotNull(current.getNorthOrNull(), current.getWestOrNull())
        current.getChar() == 'F' && (freeSide == CoordSide.SOUTH || freeSide == CoordSide.EAST) -> listOfNotNull()
        else -> throw Exception("TODO")
    }.filter { it.status != CoordStatus.LOOP }
        .also {println("; Adding $it") }
        .toMutableList()
        .apply {
            if (next != start) { addAll(callRecursive(AdjacentFreeFieldState(start, next, newFreeSide, current))) }
        }
}

// Deep recursive function to get all loop coordinates
val getLoopCoords = DeepRecursiveFunction<Pair<Coord, Coord>, List<Coord>> { (here, from) ->
    here
        .getConnecting()
        .toList()
        .first { it != from }
        .let { c ->
            when {
                c.getChar() == 'S' -> listOf(here, c)
                else -> mutableListOf(here).also { it.addAll(callRecursive(Pair(c, here))) }
            }
        }
}

// Collect all loop coordinates, label them as LOOP
val loopCoords = start
    .getSurrounding()
    .filter { it.outOfBounds().not() && it.getChar() != '.' }
    .first { it.getConnecting().toList().contains(start) }
    .let { getLoopCoords(Pair(it, start)) }
    .onEach { c -> c.status = CoordStatus.LOOP }

// Start a queue of coordinates with all coordinates adjacent to 0,0 that are not LOOP
// These are guaranteed to be FREE
val queue = allCoords
    .first().first()
    .apply { status = CoordStatus.FREE }
    .getSurrounding()
    .filter { it.outOfBounds().not() && it.status != CoordStatus.LOOP }
    .toMutableList()

// Replace the start character with what it should be, so it doesn't break loop following
sequences = sequences.map {
    it.map {
            c -> if(c == 'S') { startChar } else { c }
    }
}

// Iterate over the queue of coords, any coord in the queue is FREE and will be marked as such
// All adjacent non-LOOP character are added to the queue.
// When a LOOP-character is encountered for the first time, we know what the free side of it is
// and can start to follow along, adding any coords adjacent to the loop's free side to the queue.
// Then, the only remaining thing to do is to process the queue until nothing new is found.
var addedLoopAdjacent = false
while (queue.isNotEmpty()) {
    val c = queue.removeFirst()
    if(c.status == CoordStatus.FREE) { continue }
    println("Processing $c")
    c.status = CoordStatus.FREE
    queue.addAll(
        c.getSurrounding()
            .filter { it.status == CoordStatus.UNKNOWN }
            .also { println("Discovered $it") }
    )

    // Follow loop, but only once!
    if(!addedLoopAdjacent && c.getSurrounding().any { !it.outOfBounds() && it.status == CoordStatus.LOOP }) {
        val loopStart = c.getSurrounding().first { !it.outOfBounds() && it.status == CoordStatus.LOOP }
        println("Investigating loop from $loopStart")
        val next = loopStart.getConnecting().first
        val freeSide = when {
            c.getNorth() == loopStart -> CoordSide.SOUTH
            c.getSouth() == loopStart -> CoordSide.NORTH
            c.getEast() == loopStart -> CoordSide.WEST
            c.getWest() == loopStart -> CoordSide.EAST
            else -> throw Exception("INVALID COORDSIDE")
        }
        val laf = loopAdjacentFreeFields(AdjacentFreeFieldState(loopStart, next, freeSide, loopStart))
        println("LAF: $laf")
        queue.addAll(laf)
        addedLoopAdjacent = true
    }
}

// The answer is calculated by counting the number of coords which we haven't encountered yet.
println("Answer 2: ${allCoords.flatten().count { it.status == CoordStatus.UNKNOWN }}")






