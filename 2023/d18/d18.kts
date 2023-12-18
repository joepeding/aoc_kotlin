import D18.Direction.*
import java.io.File
import javax.xml.stream.events.EndDocument
import kotlin.Exception
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.streams.toList

data class Coord(val row: Long, val col: Long)
data class Instruction(val dir: Direction, val dist: Long, val color: String)
enum class Direction { NORTH, SOUTH, EAST, WEST }
fun String.toInstruction(): Instruction = Instruction(
    when(last()) {
        '0' -> EAST
        '1' -> SOUTH
        '2' -> WEST
        '3' -> NORTH
        else -> throw Exception("Invalid char")
    },
    take(5).toLong(16),
    ""
)

val input: String = File("input").readText()
val instructions = input
    .split("\n")
    .filter { it.isNotBlank() }
    .map { Instruction(
        when(it.first()) {
            'R' -> EAST
            'D' -> SOUTH
            'L' -> WEST
            'U' -> NORTH
            else -> throw Exception("Invalid direction")
        },
        it.substringAfter(" ").substringBefore(" ").toLong(),
        it.substringAfter("#").substringBefore(")")
    ) }

fun List<Instruction>.points(): List<Coord> = let {
    instructions -> mutableListOf<Coord>().apply {
        add(Coord(0,0))
        instructions.forEach { when(it.dir) {
            EAST -> add(Coord(last().row, last().col + it.dist))
            WEST -> add(Coord(last().row, last().col - it.dist))
            NORTH -> add(Coord(last().row - it.dist, last().col))
            SOUTH -> add(Coord(last().row + it.dist, last().col))
        } }
    }
}

fun List<Coord>.shoelace(ins: List<Instruction>): Long = windowed(2, 1)
    .sumOf { (it.first().col * it.last().row) - (it.last().col * it.first().row) }
    .plus( (this.last().col * this.first().row) - (this.first().col * this.last().row) )
    .plus( ins.sumOf { it.dist } )
    .let { abs(it) }
    .div(2)
    .plus(1)

println("Answer 1: ${instructions.points().shoelace(instructions)}")

val newInstructions = instructions.map { it.color.toInstruction() }
println("Answer 2: ${newInstructions.points().shoelace(newInstructions)}")


