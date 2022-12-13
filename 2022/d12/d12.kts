import java.io.File
import java.util.LinkedList
import javax.print.attribute.IntegerSyntax
import kotlin.math.floor

val input = File("input").readText()
    .split("\n")
    .filter { it.isNotEmpty() }

val alphabet = "abcdefghijklmnopqrstuvwxyz"
var scoord = Pair(0,0)
var ecoord = Pair(0,0)

class Square(
    var x: Int,
    var y: Int,
    var h: Int,
    var dist: Int = Integer.MAX_VALUE,
    var previous: Square? = null
) {
    override fun toString() = "($x, $y)"
}

fun inputToHeightMap(): List<List<Square>> {
    val heightMap = input
        .mapIndexed { y, s ->
            s.mapIndexed { x, char ->
                char.let {
                    alphabet.forEachIndexed { i, c ->
                        if(it == c) { return@let i }
                    }
                    if (it == 'S') {
                        scoord = Pair(x, y)
                        return@let 0
                    }
                    if (it == 'E') {
                        ecoord = Pair(x, y)
                        return@let 25
                    }
                    return@let 100
                }.let {
                    Square(x, y, it)
                }
            }
        }
    return heightMap
}

fun findShortestPathFromTo(heightMap: List<List<Square>>, start: Square, end: Square): Int {
    val queue: MutableList<Square> = mutableListOf()
    var dest: Square? = null
    queue.add(start)
    while (queue.isNotEmpty()) {
        var square = queue.removeFirst()
        if (square.x == end.x && square.y == end.y) {
            dest = square
            break
        }
        visit(heightMap, queue, square.x - 1, square.y, square);
        visit(heightMap, queue, square.x + 1, square.y, square);
        visit(heightMap, queue, square.x, square.y - 1, square);
        visit(heightMap, queue, square.x, square.y + 1, square);
    }

    if (dest == null) {
        return Integer.MAX_VALUE
    } else {
        return reconstructPath(dest, start).size
    }
}

fun visit(heightMap: List<List<Square>>, queue: MutableList<Square>, x: Int, y: Int, previous: Square) {
    if (x < 0 || x >= heightMap.first().size || y < 0 || y >= heightMap.size) { return }
    var square = heightMap[y][x]
    if (square.h > previous.h + 1) { return }
    var dist = previous.dist + 1
    if (dist < square.dist) {
        square.dist = dist
        square.previous = previous
        queue.add(square)
    }
}

fun reconstructPath(s: Square?, pathStart: Square): MutableList<Square> {
    if (s == null) {
        return mutableListOf()
    } else if (s == pathStart) {
        return mutableListOf()
    } else {
        return mutableListOf(s).also { it.addAll(reconstructPath(s.previous, pathStart)) }
    }
}

// Part 1
print("Answer 1: ")
var pt1HeightMap = inputToHeightMap()
var start = pt1HeightMap.get(scoord.second).get(scoord.first)
var end = pt1HeightMap.get(ecoord.second).get(ecoord.first)
println(findShortestPathFromTo(pt1HeightMap, start, end))

//Part 2
print("Answer 2: ")
inputToHeightMap().flatten().filter { it.h == 0 }.map { aStart ->
    var attemptHeightMap = inputToHeightMap()
    var attemptStart = attemptHeightMap.get(aStart.y).get(aStart.x)
    var attemptEnd = attemptHeightMap.get(ecoord.second).get(ecoord.first)
    return@map findShortestPathFromTo(attemptHeightMap, attemptStart, attemptEnd)
}.sorted().first()
