import java.io.File
import kotlin.math.min

val input: String = File("input").readText()

// Part 1
print("Answer 1: ")
var points: List<List<Pair<Int,Int>>> = input
    .split("\n")
    .filter { it.isNotEmpty() }
    .map {
        it
            .split(" -> ")
            .filter { it.isNotEmpty() }
            .map { it.split(",").let { (x,y) -> Pair(x.toInt(), y.toInt()) } }
    }

var minX = points.flatten().map { it.first }.min()
var maxX = points.flatten().map { it.first }.max()
var minY = 0
var maxY = points.flatten().map { it.second }.max()

val grid: MutableMap<Int, MutableMap<Int, String>> = mutableMapOf()
(minY..maxY).forEach { y ->
    grid.put(y, mutableMapOf())
    (minX..maxX).forEach { x ->
        grid.get(y)!!.put(x, ".")
    }
}

points.forEach {rock ->
    rock
        .windowed(2, 1, false)
        .forEach { (first, second) ->
            coordinatesBetween(first, second).forEach {
                grid.get(it.second)!!.put(it.first, "#")
            }
        }
}

fun coordinatesBetween(a: Pair<Int, Int>, b: Pair<Int, Int>): List<Pair<Int,Int>> {
    if (a.first == b.first) {
        var yCoords = listOf(a.second, b.second)
        return ((yCoords.min())..(yCoords.max())).map { Pair(a.first, it) }
    } else {
        var xCoords = listOf(a.first, b.first)
        return ((xCoords.min())..(xCoords.max())).map { Pair(it, a.second) }
    }
}

var sandCount = 0
outer@ while (true) {
    var sandCoord = Pair(500,0)
    sandCount++
    var sandStable = false
    while (!sandStable) {
        when {
            sandCoord.first < minX || sandCoord.second + 1 > maxY -> break@outer
            grid.get(sandCoord.second + 1)!!.get(sandCoord.first) == "." -> sandCoord = Pair(sandCoord.first, sandCoord.second + 1)
            listOf("o", "#").contains(grid.get(sandCoord.second + 1)!!.get(sandCoord.first)) -> {
                when {
                    sandCoord.first - 1 < minX || sandCoord.second + 1 > maxY -> break@outer
                    grid.get(sandCoord.second + 1 )!!.get(sandCoord.first - 1) == "." -> sandCoord = Pair(sandCoord.first - 1, sandCoord.second + 1)
                    sandCoord.first + 1 > maxX || sandCoord.second + 1 > maxY -> break@outer
                    grid.get(sandCoord.second + 1 )!!.get(sandCoord.first + 1) == "." -> sandCoord = Pair(sandCoord.first + 1, sandCoord.second + 1)
                    else -> {
                        grid.get(sandCoord.second)!!.put(sandCoord.first, "o")
                        sandStable = true
                    }
                }
            }
        }
    }
}

println()
grid.values.forEach {
    println(it.values.joinToString(""))
}

println(sandCount - 1)


