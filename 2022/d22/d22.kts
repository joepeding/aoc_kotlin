import java.io.File

val input: String = File("input").readText()
// Part 1
print("Answer 1: ")
solve()

// Part 2
print("Answer 2: ")
solve(true)

fun solve(cubeCoords: Boolean = false) {
    var map = map()
    var ins = parseInstructions()
    var pos = Coord(map.get(0)!!.entries.filter { it.value == "." }.first().key, 0)
    var dir = "E"

    ins.forEach { i ->
        if (i == "R" || i == "L") {
            dir = cd(dir, i)
            return@forEach
        }
        repeat(i.toInt()) {
            var newDir = dir
            var newPos = pos
            if (cubeCoords) {
                var res = forwardCubeCoord(pos, dir, map)
                newDir = res.first
                newPos = res.second
            } else {
                newPos = forwardCoord(pos, dir, map)
            }
            if (map.get(newPos.y)!!.get(newPos.x)!! == "#") {
                return@forEach
            } else {
                pos = newPos
                dir = newDir
            }
        }
    }

    println(1000 * (pos.y + 1) + 4 * (pos.x + 1) + facingVal(dir))
}

data class Coord(var x: Int, var y: Int) {
    override fun toString(): String = "($x, $y)"
}

fun cd(cur: String, turn: String): String = when(cur) {
    "N" -> if( turn == "L" ) { "W" } else { "E" }
    "E" -> if( turn == "L" ) { "N" } else { "S" }
    "S" -> if( turn == "L" ) { "E" } else { "W" }
    "W" -> if( turn == "L" ) { "S" } else { "N" }
    else -> throw Exception("Invalid heading")
}

fun forwardCoord(pos: Coord, dir: String, map: MutableMap<Int, MutableMap<Int, String>>): Coord = when(dir) {
    "N" -> map.get(pos.y - 1)?.get(pos.x)?.let { Coord(pos.x, pos.y - 1 ) } ?: Coord(pos.x, map.entries.filter { it.value.containsKey(pos.x) }.last().key)
    "E" -> map.get(pos.y)!!.get(pos.x + 1)?.let { Coord(pos.x + 1, pos.y)  } ?: Coord(map.get(pos.y)!!.entries.toList().first().key, pos.y)
    "S" -> map.get(pos.y + 1)?.get(pos.x)?.let { Coord(pos.x, pos.y + 1) } ?: Coord(pos.x, map.entries.filter { it.value.containsKey(pos.x) }.first().key)
    "W" -> map.get(pos.y)!!.get(pos.x - 1)?.let { Coord(pos.x - 1, pos.y) } ?: Coord(map.get(pos.y)!!.entries.toList().last().key, pos.y)
    else -> throw Exception("Invalid heading")
}


fun forwardCubeCoord(pos: Coord, dir: String, map: MutableMap<Int, MutableMap<Int, String>>): Pair<String, Coord> {
    var newCoord: Coord? = when (dir) {
        "N" -> map.get(pos.y - 1)?.get(pos.x)?.let { Coord(pos.x, pos.y - 1 ) }
        "E" -> map.get(pos.y)!!.get(pos.x + 1)?.let { Coord(pos.x + 1, pos.y) }
        "S" -> map.get(pos.y + 1)?.get(pos.x)?.let { Coord(pos.x, pos.y + 1) }
        "W" -> map.get(pos.y)!!.get(pos.x - 1)?.let { Coord(pos.x - 1, pos.y) }
        else -> throw Exception("Invalid heading")
    }
    if (newCoord != null) { return Pair(dir, newCoord) }
    var res = traverseCubeEdge(pos, dir, map)
    // println("Moving from $pos, $dir to ${res.second}, facing ${res.first}") // Needed this to debug copy-paste error in traverseCubeEdge
    return res
}

fun traverseCubeEdge(pos: Coord, dir: String, map: MutableMap<Int, MutableMap<Int, String>>): Pair<String, Coord> {
    // Hardcoded for my input :(
    if (dir == "N" && pos.x < 50) { return Pair("E", Coord(50, 50 + pos.x)) }
    if (dir == "N" && (50..99).contains(pos.x)) { return Pair("E", Coord(0, 150 + pos.x - 50)) }
    if (dir == "N" && (100..149).contains(pos.x)) { return Pair("N", Coord(pos.x - 100, 199)) }

    if (dir == "S" && pos.x < 50) { return Pair("S", Coord(pos.x + 100, 0)) }
    if (dir == "S" && (50..99).contains(pos.x)) { return Pair("W", Coord(49, 150 + pos.x - 50)) }
    if (dir == "S" && (100..149).contains(pos.x)) { return Pair("W", Coord(99, 50 + pos.x - 100)) }

    if (dir == "E" && pos.y < 50) { return Pair("W", Coord(99, 149 - pos.y)) }
    if (dir == "E" && (50..99).contains(pos.y)) { return Pair("N", Coord(100 + pos.y - 50, 49)) }
    if (dir == "E" && (100..149).contains(pos.y)) { return Pair("W", Coord(149, 49 - (pos.y - 100))) }
    if (dir == "E" && (150..199).contains(pos.y)) { return Pair("N", Coord(50 + pos.y - 150, 149)) }

    if (dir == "W" && pos.y < 50) { return Pair("E", Coord(0, 149 - pos.y)) }
    if (dir == "W" && (50..99).contains(pos.y)) { return Pair("S", Coord(pos.y - 50, 100)) }
    if (dir == "W" && (100..149).contains(pos.y)) { return Pair("E", Coord(50, 49 - (pos.y - 100))) }
    if (dir == "W" && (150..199).contains(pos.y)) { return Pair("S", Coord(50 + pos.y - 150, 0)) }
    throw Exception("Exhausted edges")
}

fun facingVal(dir: String): Int = when(dir) {
    "N" -> 3
    "E" -> 0
    "S" -> 1
    "W" -> 2
    else -> throw Exception("Invalid heading")
}

fun map(): MutableMap<Int, MutableMap<Int, String>> = input
    .split("\n\n")[0]!!
    .split("\n")
    .filter { it.isNotEmpty() }
    .mapIndexed { y, s ->
        var row = mutableMapOf<Int, String>()
        s.forEachIndexed { x, c ->
            if (listOf('.', '#').contains(c)) {
                row.put(x, "$c")
            }
        }
        return@mapIndexed Pair(y, row)
    }.toMap().toMutableMap()


fun parseInstructions(): MutableList<String> {
    var temp: String = ""
    val out = mutableListOf<String>()
    input
        .split("\n\n")[1]
        .forEach { c ->
            if (c == 'R' || c == 'L') {
                if (temp.isNotEmpty()) {
                    out.add(temp); temp = ""
                }
                out.add("$c")
            } else {
                temp = "$temp$c"
            }
        }
    if (temp.trim().isNotEmpty()) { out.add(temp.trim()) }
    return out
}
