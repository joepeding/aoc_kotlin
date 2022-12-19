import java.io.File
import kotlin.math.min

val input: String = File("input").readText()

// Part 2
print("Answer 2: ")
var points: List<List<Pair<Int,Int>>> = input
    .split("\n")
    .filter { it.isNotEmpty() }
    .map {
        it
            .split(" -> ")
            .filter { it.isNotEmpty() }
            .map { it.split(",").let { (x,y) -> Pair(x.toInt(), y.toInt()) } }
    }

var minY = 0
var maxY = points.flatten().map { it.second }.max() + 1
var minX = points.flatten().map { it.first }.min() - (maxY + 5)
var maxX = points.flatten().map { it.first }.max() + (maxY + 5)

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

println("($minX, $minY) ($maxX, $maxY)")
println(points)

var sandCount = 0
outer@ while (true) {
    var sandCoord = Pair(500,0)
    sandCount++
    var sandStable = false
    while (!sandStable) {
        when {
            sandCoord.second == maxY -> {
                grid.get(sandCoord.second)!!.put(sandCoord.first, "o")
                sandStable = true
            }
            grid.get(sandCoord.second + 1)!!.get(sandCoord.first) == "." -> sandCoord = Pair(sandCoord.first, sandCoord.second + 1)
            listOf("o", "#").contains(grid.get(sandCoord.second + 1)!!.get(sandCoord.first)) -> {
                when {
                    grid.get(sandCoord.second + 1 )!!.get(sandCoord.first - 1) == "." -> sandCoord = Pair(sandCoord.first - 1, sandCoord.second + 1)
                    grid.get(sandCoord.second + 1 )!!.get(sandCoord.first + 1) == "." -> sandCoord = Pair(sandCoord.first + 1, sandCoord.second + 1)
                    else -> {
                        grid.get(sandCoord.second)!!.put(sandCoord.first, "o")
                        if (sandCoord.first == 500 && sandCoord.second == 0) { break@outer }
                        sandStable = true
                        println("Sand moved to $sandCoord")
                    }
                }
            }
        }
    }
    if (sandCount % 500 == 0 ) {
        println()
        println("Sandcount: $sandCount")
        grid.values.forEach {
            println(it.values.joinToString(""))
        }
    }
}

println()
grid.values.forEach {
    println(it.values.joinToString(""))
}

println(sandCount)


