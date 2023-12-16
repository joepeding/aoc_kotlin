import java.io.File
import kotlin.Exception
import kotlin.math.ceil
import kotlin.math.max
import kotlin.streams.toList

data class Beam(val row: Int, val col: Int, val dir: Direction )
enum class Direction { NORTH, SOUTH, EAST, WEST }

val input: String = File("input").readText()
val contraption = input
    .split("\n")
    .filter { it.isNotBlank() }
    .map { it.toList() }

val beamSet: MutableSet<Beam> = mutableSetOf()
Beam(0, 0, Direction.EAST).also { beamSet.add(it) }.traceBeam()
val tileSet = beamSet.map { Pair(it.row, it.col) }.toSet()
println("Answer 1: ${tileSet    .size}")

val possibleStarts: MutableList<Beam> = mutableListOf<Beam>()
    .apply {
        addAll(
            contraption.indices.map {
                listOf(
                    Beam(it, 0, Direction.EAST),
                    Beam(it, contraption.first().lastIndex, Direction.WEST),
                )
            }.flatten()
        )
        addAll(
            contraption.first().indices.map {
                listOf(
                    Beam(0, it, Direction.SOUTH),
                    Beam(contraption.lastIndex, it, Direction.NORTH),
                )
            }.flatten()
        )
    }
possibleStarts.maxOf {
    beamSet.clear()
    beamSet.add(it)
    it.traceBeam()
    beamSet.map { Pair(it.row, it.col) }.toSet().size
}.let { println("Answer 2: $it") }

fun Beam.getCharacter() = contraption[row][col]
fun Beam.outOfBounds() = row < 0 || col < 0 || row >= contraption.size || col >= contraption.first().size
fun Beam.north() = Beam(row - 1, col, Direction.NORTH)
fun Beam.south() = Beam(row + 1, col, Direction.SOUTH)
fun Beam.east() = Beam(row, col + 1, Direction.EAST)
fun Beam.west() = Beam(row, col - 1, Direction.WEST)
fun Beam.next(): List<Beam> = when (this.getCharacter()) {
    '.' -> when(this.dir) {
        Direction.NORTH -> listOf(north())
        Direction.SOUTH -> listOf(south())
        Direction.EAST -> listOf(east())
        Direction.WEST -> listOf(west())
    }
    '\\' -> when(this.dir) {
        Direction.NORTH -> listOf(west())
        Direction.SOUTH -> listOf(east())
        Direction.EAST -> listOf(south())
        Direction.WEST -> listOf(north())
    }
    '/' -> when(this.dir) {
        Direction.NORTH -> listOf(east())
        Direction.SOUTH -> listOf(west())
        Direction.EAST -> listOf(north())
        Direction.WEST -> listOf(south())
    }
    '|' -> when(this.dir) {
        Direction.NORTH -> listOf(north())
        Direction.SOUTH -> listOf(south())
        Direction.EAST, Direction.WEST -> listOf(north(), south())
    }
    '-' -> when(this.dir) {
        Direction.NORTH, Direction.SOUTH -> listOf(east(), west())
        Direction.EAST -> listOf(east())
        Direction.WEST -> listOf(west())
    }
    else -> throw Exception("Invalid char $this")
}

fun Beam.traceBeam(): Unit = this
    .next()
    .filter { !beamSet.contains(it) && !it.outOfBounds() }
    .forEach {
        beamSet.add(it)
        it.traceBeam()
    }