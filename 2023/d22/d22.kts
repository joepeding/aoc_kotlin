import java.io.File
import javax.xml.stream.events.EndDocument
import kotlin.Exception
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.streams.toList

enum class BlockState { FALLING, RESTING }
data class Coord(val x: Int, val y: Int, val z: Int)
fun Coord.isAbove(other: Coord) = x == other.x && y == other.y && z == other.z + 1

data class Block(var a: Coord, var b: Coord, var state: BlockState = BlockState.FALLING, var name: Int)
fun Block.moveDown() { a = Coord(a.x, a.y, a.z - 1); b = Coord(b.x, b.y, b.z - 1)}
val zComparator = Comparator<Block> { block1, block2 -> minOf(block1.a.z, block1.b.z) - minOf(block2.a.z, block2.b.z) }
fun Block.coords() = (minOf(a.x, b.x)..maxOf(a.x, b.x)).map { x ->
    (minOf(a.y, b.y)..maxOf(a.y, b.y)).map { y ->
        (minOf(a.z, b.z)..maxOf(a.z, b.z)).map { z ->
            Coord(x, y, z)
        }
    }.flatten()
}.flatten()

fun Block.restingOn() = blocks
    .filter { it.state == BlockState.RESTING }
    .filter { it != this }
    .filter { maxOf(it.a.z, it.b.z) == minOf(a.z, b.z) - 1 }
    .filter { other -> this.coords().any { thisC -> other.coords().any { otherC -> thisC.isAbove(otherC) } } }
fun Block.isSupporting() = blocks
    .filter { it.state == BlockState.RESTING }
    .filter { it != this }
    .filter { minOf(it.a.z, it.b.z) == maxOf(a.z, b.z) + 1 }
    .filter { other -> this.coords().any { thisC -> other.coords().any { otherC -> otherC.isAbove(thisC) } } }
val isOnlySupportForMemos: MutableMap<Block, MutableSet<Block>> = mutableMapOf()
fun isOnlySupportFor(block: Block): MutableSet<Block> = isOnlySupportForMemos.getOrPut(block) {
    val supportedBy: MutableSet<Block> = mutableSetOf()
    val new: MutableList<Block> = block
        .isSupporting()
        .filter { it.restingOn().size == 1 }
        .map { b -> isOnlySupportFor(b).also { it.add(b) } }
        .flatten()
        .toSet()
        .toMutableList()
    while (new.isNotEmpty()) {
        supportedBy.addAll(new)
        new.clear()
        new.addAll(blocks
            .filter { it !in supportedBy }
            .filter { b -> b.restingOn().let { it.isNotEmpty() && it.all { it in supportedBy } } }
        )
    }
    supportedBy
}

val input: List<String> = File("input").readText().split("\n").filter { it.isNotBlank() }
var blockNum: Int = 0
val blocks = input
    .map { b ->
        b.split('~')
        .map { c ->
            c.split(',')
            .map { it.toInt() }
            .let { (x, y, z) -> Coord(x, y, z) }
        }
    }.map { (a, b) -> Block(a, b, BlockState.FALLING, blockNum++) }

blocks
    .sortedWith(zComparator)
    .onEach {
        while (it.a.z > 1 && it.b.z > 1 && it.restingOn().isEmpty()) { it.moveDown() }
        it.state = BlockState.RESTING
    }
    .filter {
        it.isSupporting().all { other -> other.restingOn().size > 1 }
    }
    .let { println("Answer 1: ${it.count()}\n") }

blocks
    .sortedWith(zComparator)
    .reversed()
    .map { b -> b to isOnlySupportFor(b).filter { b.name != it.name } }
    .sumOf { it.second.size }


