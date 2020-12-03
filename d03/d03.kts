import java.io.File
import java.util.SortedSet
import java.util.regex.Pattern

val lines: MutableList<String> = File("input").useLines { it.toMutableList() }
val width = lines[0].length


println(lines[0].length)
println(lines.size)

fun treeAtCoord(x: Int, y: Int): Boolean {
    return lines[y][x % width] == '#'
}

fun countTreesForSlope(slopeX: Int, slopeY: Int): Int {
    var x = 0
    var y = 0
    var treesEncountered = 0
    while (y < lines.size) {
        if (treeAtCoord(x, y)) {
            treesEncountered += 1
        }
        x += slopeX
        y += slopeY
    }
    return treesEncountered
}

var c1 = countTreesForSlope(1,1)
var c2 = countTreesForSlope(3,1)
var c3 = countTreesForSlope(5,1)
var c4 = countTreesForSlope(7,1)
var c5 = countTreesForSlope(1,2)
println("Result: ")
println(c1)
println(c2)
println(c3)
println(c4)
println(c5)
println(61L * 265L * 82L * 70L * 34L)