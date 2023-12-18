import D17.Direction.*
import java.io.File
import kotlin.Exception
import kotlin.math.ceil
import kotlin.math.max
import kotlin.streams.toList

data class Block(val row: Int, val col: Int, val heatLoss: Int)
data class CrucibleState(val loc: Block, val dir: Direction, val distance: Int, val heatLoss: Int)
enum class Direction { NORTH, SOUTH, EAST, WEST }
fun Direction.next(): List<Direction> = when(this) {
    NORTH -> listOf(NORTH, EAST, WEST)
    SOUTH -> listOf(SOUTH, EAST, WEST)
    EAST -> listOf(EAST, NORTH, SOUTH)
    WEST -> listOf(WEST, NORTH, SOUTH)
}
fun Direction.opposite(): Direction = when(this) {
    NORTH -> SOUTH
    SOUTH -> NORTH
    EAST -> WEST
    WEST -> EAST
}
fun Block.nextInDirection(dir: Direction) = when(dir) {
    NORTH -> blocks.getOrNull(row - 1)?.getOrNull(col)
    SOUTH -> blocks.getOrNull(row + 1)?.getOrNull(col)
    EAST -> blocks.getOrNull(row)?.getOrNull(col + 1)
    WEST -> blocks.getOrNull(row)?.getOrNull(col - 1)
}
fun CrucibleState.move(dir: Direction): CrucibleState? = loc.nextInDirection(dir)?.let {
    CrucibleState(
        it,
        dir,
        if (dir == this.dir) distance + 1 else 1,
        this.heatLoss + it.heatLoss
    )
}
fun CrucibleState.next() = dir
    .next()
    .mapNotNull { move(it) }
    .filter { it.distance < 4 }
fun CrucibleState.ultraNext() = dir
    .next()
    .mapNotNull { move(it) }
    .filter { it.distance < 11 && (dir == it.dir || distance >= 4) }

val input: String = File("input").readText()
val blocks = input
    .split("\n")
    .filter { it.isNotBlank() }
    .mapIndexed { row, s ->
        s.mapIndexed { col, c -> Block(row, col, c.digitToInt())}
    }

val queue = mutableListOf(CrucibleState(blocks[0][0], EAST, 0, 0), CrucibleState(blocks[0][0], SOUTH, 0, 0))
val visited: MutableMap<Block, MutableList<CrucibleState>> = mutableMapOf(blocks[0][0] to mutableListOf(queue.first(), queue.last()))
val ultra = true // Switch between pt1 (±30 min?) and pt2 (±2.5hr?);
while (queue.isNotEmpty()) {
    val current = queue.removeFirst()
    println("Checking $current")
    val next = if (ultra) current.ultraNext() else current.next()
    next.forEach { new ->
        var otherRoutes = visited.getOrPut(new.loc) { mutableListOf() }
        var comparableRoutes = otherRoutes.filter { new.nextStepsAlsoPossibleWith(it, ultra) }
        if (comparableRoutes.all { new.heatLoss < it.heatLoss }) {
            visited[new.loc]!!.add(new)
            if (new.loc != blocks.last().last()) {
                queue.add(new)
            }
        }
    }
}
println("Answer ${if (ultra) 2 else 1}: ${visited[blocks.last().last()]!!.minOf { it.heatLoss }}")

fun CrucibleState.nextStepsAlsoPossibleWith(other: CrucibleState, ultra: Boolean): Boolean = when {
    (!ultra || other.distance >= 4) && this.dir == other.dir && this.distance >= other.distance -> true
    this.dir.opposite() == other.dir && (ultra || this.distance == 3) && (!ultra || this.distance == 10) -> true
    else -> false
}
