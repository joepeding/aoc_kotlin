import java.io.File

val input: String = File("input").readText().trim()

data class Coord(val x: Int, val y: Int, val z: Int) {
    override fun toString(): String = "($x, $y, $z)"
}
class Block(public val coord: Coord, public val type: String = "LAVA") {
    public var above: Block? = null
    public var below: Block? = null
    public var left: Block? = null
    public var right: Block? = null
    public var front: Block? = null
    public var back: Block? = null

    public fun leftCoord(): Coord = coord.let { (x,y,z) -> Coord(x - 1, y, z) }
    public fun rightCoord(): Coord = coord.let { (x,y,z) -> Coord(x + 1, y, z) }
    public fun aboveCoord(): Coord = coord.let { (x,y,z) -> Coord(x, y + 1, z) }
    public fun belowCoord(): Coord = coord.let { (x,y,z) -> Coord(x, y - 1, z) }
    public fun frontCoord(): Coord = coord.let { (x,y,z) -> Coord(x, y , z + 1) }
    public fun backCoord(): Coord = coord.let { (x,y,z) -> Coord(x, y , z - 1) }

    public fun freeFaces(): Int = listOf(
        above,
        below,
        left,
        right,
        front,
        back
    ).count { it == null }

    public fun typeFaces(type: String): Int = listOf(
        above,
        below,
        left,
        right,
        front,
        back
    ).count { it?.type == type }

    override fun toString(): String = "$coord: ${freeFaces()}"
}

// Part 1
print("Answer 1: ")
val blocks: List<Block> = input
    .split("\n")
    .map {
        it.split(",").map { it.toInt() }.let { (x,y,z) -> Coord(x, y, z) }
    }.map {
        Block(it)
    }

blocks.forEach { b: Block ->
    if ( b.back == null ) { blocks.firstOrNull { it.coord == b.backCoord() }?.let { b.back = it; it.front = b } }
    if ( b.front == null ) { blocks.firstOrNull { it.coord == b.frontCoord() }?.let { b.front = it; it.back = b } }
    if ( b.left == null ) { blocks.firstOrNull { it.coord == b.leftCoord() }?.let { b.left = it; it.right = b } }
    if ( b.right == null ) { blocks.firstOrNull { it.coord == b.rightCoord() }?.let { b.right = it; it.left = b } }
    if ( b.above == null ) { blocks.firstOrNull { it.coord == b.aboveCoord() }?.let { b.above = it; it.below = b } }
    if ( b.below == null ) { blocks.firstOrNull { it.coord == b.belowCoord() }?.let { b.below = it; it.above = b } }
}

println(blocks.sumBy { it.freeFaces() })

// Part 2
print("Answer 2: ")
var minX = blocks.map { it.coord }.map{ it.x }.min() - 1
var maxX = blocks.map { it.coord }.map{ it.x }.max() + 1
var minY = blocks.map { it.coord }.map{ it.y }.min() - 1
var maxY = blocks.map { it.coord }.map{ it.y }.max() + 1
var minZ = blocks.map { it.coord }.map{ it.z }.min() - 1
var maxZ = blocks.map { it.coord }.map{ it.z }.max() + 1

fun outOfBounds(c: Coord): Boolean = c.let { (x, y, z) ->
    (x < minX || x > maxX || y < minY || y > maxY || z < minZ || z > maxZ)
}

val airStart = Block(Coord(minX, minY, minZ), "AIR")
val airBlocks = mutableListOf(airStart)
val queue: MutableList<Block> = mutableListOf(airStart)
while (!queue.isEmpty()) {
    var b: Block = queue.removeFirst()

    if ( b.back == null ) { blocks.firstOrNull { it.coord == b.backCoord() }?.let { b.back = it } }
    if ( b.back == null ) { airBlocks.firstOrNull { it.coord == b.backCoord() }?.let { b.back = it; it.front = b} }
    if ( outOfBounds(b.backCoord()) ) { b.back = Block(b.backCoord(), "BOUNDARY") }
    if ( b.back == null) { Block(b.backCoord(), "AIR").also { b.back = it; it.front = b; airBlocks.add(it); queue.add(it); } }

    if ( b.front == null ) { blocks.firstOrNull { it.coord == b.frontCoord() }?.let { b.front = it } }
    if ( b.front == null ) { airBlocks.firstOrNull { it.coord == b.frontCoord() }?.let { b.front = it; it.back = b } }
    if ( outOfBounds(b.frontCoord()) ) { b.front = Block(b.frontCoord(), "BOUNDARY") }
    if ( b.front == null) { Block(b.frontCoord(), "AIR").also { b.front = it; it.back = b; airBlocks.add(it); queue.add(it); } }

    if ( b.left == null ) { blocks.firstOrNull { it.coord == b.leftCoord() }?.let { b.left = it } }
    if ( b.left == null ) { airBlocks.firstOrNull { it.coord == b.leftCoord() }?.let { b.left = it; it.right = b } }
    if ( outOfBounds(b.leftCoord()) ) { b.left = Block(b.leftCoord(), "BOUNDARY") }
    if ( b.left == null) { Block(b.leftCoord(), "AIR").also { b.left = it; it.right = b; airBlocks.add(it); queue.add(it); } }

    if ( b.right == null ) { blocks.firstOrNull { it.coord == b.rightCoord() }?.let { b.right = it } }
    if ( b.right == null ) { airBlocks.firstOrNull { it.coord == b.rightCoord() }?.let { b.right = it; it.left = b } }
    if ( outOfBounds(b.rightCoord()) ) { b.right = Block(b.rightCoord(), "BOUNDARY") }
    if ( b.right == null) { Block(b.rightCoord(), "AIR").also { b.right = it; it.left = b; airBlocks.add(it); queue.add(it); } }

    if ( b.above == null ) { blocks.firstOrNull { it.coord == b.aboveCoord() }?.let { b.above = it } }
    if ( b.above == null ) { airBlocks.firstOrNull { it.coord == b.aboveCoord() }?.let { b.above = it; it.below = b } }
    if ( outOfBounds(b.aboveCoord()) ) { b.above = Block(b.aboveCoord(), "BOUNDARY") }
    if ( b.above == null) { Block(b.aboveCoord(), "AIR").also { b.above = it; it.below = b; airBlocks.add(it); queue.add(it); } }

    if ( b.below == null ) { blocks.firstOrNull { it.coord == b.belowCoord() }?.let { b.below = it } }
    if ( b.below == null ) { airBlocks.firstOrNull { it.coord == b.belowCoord() }?.let { b.below = it; it.above = b } }
    if ( outOfBounds(b.belowCoord()) ) { b.below = Block(b.belowCoord(), "BOUNDARY") }
    if ( b.below == null) { Block(b.belowCoord(), "AIR").also { b.below = it; it.above = b; airBlocks.add(it); queue.add(it); } }
}

println(airBlocks.sumBy { it.typeFaces("LAVA") } )
