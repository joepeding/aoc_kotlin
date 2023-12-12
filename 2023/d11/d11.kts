import java.io.File
import java.util.Comparator
import java.util.Currency
import kotlin.Exception
import kotlin.math.ceil
import kotlin.math.max
import kotlin.streams.toList

data class Galaxy(val row: Int, val col: Int, val num: Int)
data class Coord(val row: Int, val col: Int, val dist: Int)

val input: String = File("input").readText()
var img = input
    .split("\n")
    .filter { it.isNotBlank() }
    .map { it.chars().toList().map { c -> c.toChar() } }

var emptyRows = img.mapIndexed { i, r -> if (r.all { c -> c == '.' }) { i } else { null } }.filterNotNull()
var emptyCols = img.first().mapIndexed { i, _ -> if (img.map { it[i] }.all { c -> c == '.' }) { i } else { null } }.filterNotNull()
var galaxyNum = 1
var galaxies = img
    .mapIndexed { r, s ->
        s.mapIndexed { c, char ->
            if (char == '#') {
                Galaxy(r, c, galaxyNum++)
            } else {
                null
            }
        }
    }.flatten()
    .filterNotNull()

var pairs = galaxies
    .map { a -> galaxies.map { b -> Pair(a, b) } }
    .flatten()
    .filter { it.first != it.second }
    .distinctBy {
        it.toList().sortedBy { galaxy -> galaxy.num }
    }

println("rows: $emptyRows, \n" +
        "cols: $emptyCols, \n" +
        "galaxies: ${galaxies.size}: $galaxies \n" +
        "pairs: ${pairs.size}: $pairs")

pairs.map {
    val minRow = minOf(it.first.row, it.second.row).toLong()
    val maxRow = maxOf(it.first.row, it.second.row).toLong()
    val minCol = minOf(it.first.col, it.second.col).toLong()
    val maxCol = maxOf(it.first.col, it.second.col).toLong()
    val rowDiff = maxRow - minRow
    val colDiff = maxCol - minCol
    val rowExpansion = (emptyRows.filter { it > minRow && it < maxRow }.size).toLong() * (1000000L - 1).toLong()
    val colExpansion = (emptyCols.filter { it > minCol && it < maxCol }.size).toLong() * (1000000L - 1).toLong()
    val dist = rowDiff + colDiff + rowExpansion + colExpansion
    println("${it.first} to ${it.second}: $dist (r: $rowDiff, c: $colDiff, rE: $rowExpansion, cE: $colExpansion)")
    dist
}.sum()
//fun Pair<Galaxy, Galaxy>.shortestDistance(): Int {
//    val start = this.first
//    val end = this.second
//    val nodes = img.mapIndexed { r, s -> s.mapIndexed { c, _ -> if(start.row == r && start.col == c) 0 else Int.MAX_VALUE } }
//
//
//}