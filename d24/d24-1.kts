import java.io.File

var input: List<List<String>> = File("input").readLines().map { lineToCommands(it) }

fun lineToCommands(line: String): List<String> {
    var tmpLine = line
    val returnValue = mutableListOf<String>()
    while (tmpLine.length > 0) {
        if (tmpLine[0] == 'w' || tmpLine [0] == 'e') {
            returnValue.add(tmpLine[0].toString())
            tmpLine = tmpLine.substring(1)
        } else {
            returnValue.add(tmpLine.substring(0,2))
            tmpLine = tmpLine.substring(2)
        }
    }
    return returnValue
}

var tiles: MutableSet<Pair<Float,Float>> = mutableSetOf()
for (line in input) {
    var x = 0f
    var y = 0f
    for (dir in line) {
        if(dir == "e") {
            x = x + 1
        } else if(dir == "w") {
            x = x - 1
        } else if(dir == "ne") {
            x = x + 0.5f
            y = y - 1
        } else if(dir == "nw") {
            x = x - 0.5f
            y = y - 1
        } else if(dir == "se") {
            x = x + 0.5f
            y = y + 1
        } else if(dir == "sw") {
            x = x - 0.5f
            y = y + 1
        }
    }
    if (tiles.contains(Pair(x,y))) {
        tiles.remove(Pair(x,y))
    } else {
        tiles.add(Pair(x,y))
    }
}

println("Part 1: ${tiles.size}")

fun neighbourCoords(here: Pair<Float,Float>): List<Pair<Float,Float>> {
    val (x,y) = here
    return listOf(
            Pair(x - 1f,    y + 0f),
            Pair(x - 0.5f,  y - 1f),
            Pair(x + 0.5f,  y - 1f),
            Pair(x + 1f,    y + 0f),
            Pair(x - 0.5f,  y + 1f),
            Pair(x + 0.5f,  y + 1f),
    )
}

var oldTiles = tiles.toSet()
for (i in 1..100) {
    var newTiles: MutableSet<Pair<Float,Float>> = mutableSetOf()
    var tilesToCheck = oldTiles.map { neighbourCoords(it) }.flatten().toSet()
    for (tile in tilesToCheck) {
        var neighbouringBlack = oldTiles.filter { neighbourCoords(tile).contains(it) }.size
        if (oldTiles.contains(tile) && listOf(1,2).contains(neighbouringBlack)) {
            // Keep black tile
            newTiles.add(tile)
        } else if (neighbouringBlack == 2) {
            // White tile flips to black
            newTiles.add(tile)
        }
    }
    oldTiles = newTiles
    println("Part 2: Day $i: ${oldTiles.size}")
}

println("Part 2: ${oldTiles.size}")