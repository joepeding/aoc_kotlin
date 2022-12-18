import java.io.File

val input: String = File("input").readText().trim()

data class Coord(val x: Long, val y: Long)
abstract class Shape(var bottomLeft: Coord) {
    abstract public fun getCoords(): MutableList<Coord>
}
class Hbar(bottomLeft: Coord): Shape(bottomLeft) {
    override fun getCoords(): MutableList<Coord> = mutableListOf(
        bottomLeft,
        Coord(bottomLeft.x + 1, bottomLeft.y),
        Coord(bottomLeft.x + 2, bottomLeft.y),
        Coord(bottomLeft.x + 3, bottomLeft.y)
    )
}
class Plus(bottomLeft: Coord): Shape(bottomLeft) {
    override fun getCoords(): MutableList<Coord> = mutableListOf(
        bottomLeft,
        Coord(bottomLeft.x, bottomLeft.y + 1),
        Coord(bottomLeft.x - 1, bottomLeft.y + 1),
        Coord(bottomLeft.x + 1, bottomLeft.y + 1),
        Coord(bottomLeft.x, bottomLeft.y + 2)
    )
}
class Hook(bottomLeft: Coord): Shape(bottomLeft) {
    override fun getCoords(): MutableList<Coord> = mutableListOf(
        bottomLeft,
        Coord(bottomLeft.x + 1, bottomLeft.y),
        Coord(bottomLeft.x + 2, bottomLeft.y),
        Coord(bottomLeft.x + 2, bottomLeft.y + 1),
        Coord(bottomLeft.x + 2, bottomLeft.y + 2),
    )
}
class VBar(bottomLeft: Coord): Shape(bottomLeft) {
    override fun getCoords(): MutableList<Coord> = mutableListOf(
        bottomLeft,
        Coord(bottomLeft.x, bottomLeft.y + 1),
        Coord(bottomLeft.x, bottomLeft.y + 2),
        Coord(bottomLeft.x, bottomLeft.y + 3)
    )
}
class Block(bottomLeft: Coord): Shape(bottomLeft) {
    override fun getCoords(): MutableList<Coord> = mutableListOf(
        bottomLeft,
        Coord(bottomLeft.x + 1, bottomLeft.y),
        Coord(bottomLeft.x, bottomLeft.y + 1),
        Coord(bottomLeft.x + 1, bottomLeft.y + 1)
    )
}
var blockTypes: List<Shape> = listOf(
    Hbar(Coord(0L,0L)),
    Plus(Coord(0L,0L)),
    Hook(Coord(0L,0L)),
    VBar(Coord(0L,0L)),
    Block(Coord(0L,0L)),
)

// Part 1
print("Answer 1: ")
println(calcGridHeight(2022L))

// Part 2
print("Answer 2: ")
println(calcGridHeight(1000000000000L))

fun calcGridHeight(
    numBlock: Long,
    startingBlock: Long = 0L,
    startingGrid: MutableMap<Long, MutableMap<Long, String>> = mutableMapOf()
): Long {
    var jetCycleSize = input.length
    var blockCycleSize = blockTypes.size

    var pastCycles: MutableSet<Long> = mutableSetOf()
    var cycleStart: Long? = null
    var cycleStartCoord: Coord? = null
    var cycleStartBlockCount: Long? = null
    var heightAfter: MutableList<Int> = mutableListOf()

    var blockNum = 0L
    var jetNum = 0L
    var grid: MutableMap<Long, MutableMap<Long, String>> = mutableMapOf()

    while (blockNum < numBlock) {
        var currentHeight = grid.keys.maxOrNull() ?: -1

        // Init new block:
        var block = blockTypes[(blockNum % blockTypes.size.toLong()).toInt()]
        block.bottomLeft = Coord(0L, 0L)
        var xOffset = block.getCoords().map { it.x }.min()
        block.bottomLeft = Coord(2 - xOffset, (grid.keys.toList().maxOrNull() ?: -1L) + 4)

        // Move
        while (true) {
            // Move laterally
            var oldBL = block.bottomLeft
            var jet = input[(jetNum % input.length).toInt()]
            block.bottomLeft = when (jet) {
                '>' -> Coord(oldBL.x + 1, oldBL.y)
                '<' -> Coord(oldBL.x - 1, oldBL.y)
                else -> throw Exception("Invalid jet direction")
            }
            if (
                block.getCoords().map { it.x }.any { it < 0 || it > 6 }
                ||
                block.getCoords().any { grid.get(it.y)?.get(it.x) != null }
            ) {
                block.bottomLeft = oldBL
            }
            jetNum++

            // Move vertically
            oldBL = block.bottomLeft
            block.bottomLeft = Coord(oldBL.x, oldBL.y - 1)
            if (
                block.getCoords().map { it.y }.any { it < 0 }
                ||
                block.getCoords().any { grid.get(it.y)?.get(it.x) != null }
            ) {
                // Block comes to rest
                block.bottomLeft = oldBL
                block.getCoords().forEach { c ->
                    if (!grid.containsKey(c.y)) {
                        grid.put(c.y, mutableMapOf())
                    }
                    grid.get(c.y)!!.put(c.x, "#")
                }
                break;
            }
        }

        // Check for repeating pattern
        if ((blockNum % blockCycleSize).toInt() == blockCycleSize - 1) { // Last block in cycle
            if (cycleStart != null && (jetNum % jetCycleSize) == cycleStart) {
                // Completed a cycle
//                println("Completed a cycle!")
                var cycleHeight = block.bottomLeft.y - cycleStartCoord!!.y
                var cycleBlocks = blockNum - cycleStartBlockCount!!
                var blocksToSkip = 1000000000000 - cycleStartBlockCount!!
                var cyclesToSkip = blocksToSkip / cycleBlocks
                var extraBlocks = (blocksToSkip % cycleBlocks).toInt()

                return (cyclesToSkip * cycleHeight) + heightAfter[extraBlocks]
            } else if (cycleStart == null) {
                // No cycle known yet
                var jetPos = (jetNum % jetCycleSize)
                if (!pastCycles.add(jetPos)) { // False if already seen
                    // Found first cycle
                    cycleStart = jetPos
                    cycleStartCoord = block.bottomLeft
                    cycleStartBlockCount = blockNum
//                    println("Found cycle start at $jetNum after $blockNum")
                }
            }
        }

        if (cycleStartCoord != null) {
            heightAfter.add((grid.keys.maxOrNull() ?: 0L).toInt())
        }
        blockNum++
    }
    return (grid.keys.maxOrNull() ?: 0L) + 1 // +1 because zero-indexed
}

fun gridToStrings(grid: MutableMap<Long, MutableMap<Long, String>>): List<String> {
    return (0L..(grid.keys.max())).toList().reversed().map { y ->
        (0L..6L).map { x -> grid.get(y)?.get(x) ?: "." }.joinToString("")
    }
}
