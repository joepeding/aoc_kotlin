import java.io.File
import java.lang.IllegalArgumentException

fun rotateImage(img: List<String>, times: Int): List<String> = if (times % 4 == 0) img else rotateImage(img.mapIndexed { i, _ -> img.map { it[i].toString() }.reduce { acc, s -> acc + s }.reversed() }, times - 1)
fun flipImage(img: List<String>): List<String> = rotateImage(img.map {it.reversed()}, 1)
fun edgeStrings(img: List<String>): List<String> = listOf(
        img.first(), // Top edge
        img.map { it -> it.last().toString() }.reduce { acc, s -> acc + s }, // Right edge
        img.last(), // Top edge
        img.map { it -> it.first().toString() }.reduce { acc, s -> acc + s }, // Left edge
)
fun edgeStringsFlipped(img: List<String>): List<String> = edgeStrings(flipImage(img))
fun allPossibleEdges(img: List<String>): List<String> = listOf(edgeStrings(img), edgeStringsFlipped(img)).flatten()

//Part 1
var tiles: Map<Long, List<String>> = File("input").readText().split("\\n\\n".toRegex()).map {it -> it.split("\\n".toRegex()) }.map {it -> it[0].substring(5, it[0].lastIndex).toLong() to it.subList(1, it.size)}.toMap()
var tilesToEdges: Map<Long, List<String>> = tiles.mapValues { it -> allPossibleEdges(it.value) }
var tileLinks: Map<Long, Pair<MutableMap<Int, Set<Long>>,MutableMap<Int, Set<Long>>>> = tiles.mapValues { (id, tile) -> Pair(
        edgeStrings(tile).mapIndexed { i, e -> i to tilesToEdges.filter { it.key != id && it.value.any { otherEdge -> otherEdge == e }}.keys }.toMap().toMutableMap(),
        edgeStringsFlipped(tile).mapIndexed { i, e -> i to tilesToEdges.filter { it.key != id && it.value.any { otherEdge -> otherEdge == e }}.keys }.toMap().toMutableMap()
)}
var cornerTileIDs = tileLinks.filter { (_, matches) -> matches.first.filter { (_, tiles) -> tiles.isEmpty() }.count() == 2 }.keys
var edgeTileIDs = tileLinks.filter { (_, matches) -> matches.first.filter { (_, tiles) -> tiles.isEmpty() }.count() == 1}.keys
var centerTileIDs = tileLinks.filter { (_, matches) -> matches.first.filter { (_, tiles) -> tiles.isEmpty() }.count() == 0 }.keys
var cornerTiles = tiles.filter { cornerTileIDs.contains(it.key) }
var edgeTiles = tiles.filter { edgeTileIDs.contains(it.key) }
var centerTiles = tiles.filter { centerTileIDs.contains(it.key) }

println("Part 1 correct: [1117, 1543, 1213, 1291](input) or [1951, 1171, 2971, 3079](testinput)")
println("Part 1: ${cornerTiles.keys}")
println("Part 1: ${cornerTiles.keys.reduce { acc, i -> acc * i }}")
println("Part 2: ${edgeTiles.count()}/40 edges")
println("Part 2: ${centerTiles.count()}/100 center pieces")

var maxDim = Math.sqrt(tiles.size.toDouble()).toInt() - 1
var field: MutableMap<Pair<Int,Int>, Triple<Long, Int, Boolean>> = mutableMapOf(
    Pair(
        Pair(0,0),
        Triple(
            cornerTiles.entries.first().key,
            if(tileLinks[cornerTiles.entries.first().key]!!.second[0]!!.isEmpty() && tileLinks[cornerTiles.entries.first().key]!!.second[3]!!.isEmpty()) {
                0
            } else if(tileLinks[cornerTiles.entries.first().key]!!.second[2]!!.isEmpty() && tileLinks[cornerTiles.entries.first().key]!!.second[3]!!.isEmpty()) {
                1
            } else if(tileLinks[cornerTiles.entries.first().key]!!.second[1]!!.isEmpty() && tileLinks[cornerTiles.entries.first().key]!!.second[2]!!.isEmpty()) {
                2
            } else {
                3
            },
            true
        )
    )
)
tileLinks.forEach {println (it)}
for (y in 0..(maxDim)) {
    for (x in 0..(maxDim)) {
        if (field.containsKey(Pair(x,y))) continue
        println("Current field: $field")
        println("Looking for ($x,$y)")
        var usedTiles = field.map { it.value.first }

        //Get neighbours
        var leftNeighbour: Triple<Long, Int, Boolean>? = field[Pair(x-1, y)]
        var topNeighbour: Triple<Long, Int, Boolean>? = field[Pair(x, y-1)]

        //Get options
        println("-Left: $leftNeighbour; Top: $topNeighbour")
        var options: MutableList<Long> = mutableListOf()
        if (leftNeighbour != null) {
            if (leftNeighbour.third) {
                options.addAll(tileLinks[leftNeighbour.first]!!.second[(4 + 1 - leftNeighbour.second) % 4]!!)
            } else {
                options.addAll(tileLinks[leftNeighbour.first]!!.first[(4 + 1 - leftNeighbour.second) % 4]!!)
            }
        }
        if (topNeighbour != null) {
            if (topNeighbour.third) {
                options.addAll(tileLinks[topNeighbour.first]!!.second[(4 + 2 - topNeighbour.second) % 4]!!)
            } else {
                options.addAll(tileLinks[topNeighbour.first]!!.first[(4 + 2 - topNeighbour.second) % 4]!!)
            }
        }
        println("--Options: ${options.filter { !usedTiles.contains(it) }}")
        //Add new tile
        var newTile = options.filter { !usedTiles.contains(it) }.firstOrNull()
        if (newTile == null) throw IllegalArgumentException("Can't find new tile for ($x, $y)")
        for (f in listOf(true,false)) {
            for (r in 0..3) {
                var ownBorders = edgeStrings(rotateImage(if(f) flipImage(tiles[newTile]!!) else tiles[newTile]!!, r))
//                println("---OwnBorders (R${r}F$f): ${ownBorders}")
                //If left neighbour present, check equality of common border
                if (leftNeighbour != null) {
                    var leftNeighbourBorders = edgeStrings(rotateImage(if(leftNeighbour.third) flipImage(tiles[leftNeighbour.first]!!) else tiles[leftNeighbour.first]!!, leftNeighbour.second))
//                    println("---LeftBorders: ${leftNeighbourBorders}")
                    if (leftNeighbourBorders[1] != ownBorders[3]) {
//                        println("----Failed")
                        continue
                    }
                }
                if (topNeighbour != null) {
                    var topNeighbourBorders = edgeStrings(rotateImage(if(topNeighbour.third) flipImage(tiles[topNeighbour.first]!!) else tiles[topNeighbour.first]!!, topNeighbour.second))
//                    println("---TopBorders: ${topNeighbourBorders}")
                    if (topNeighbourBorders[2] != ownBorders[0]) {
//                        println("----Failed")
                        continue
                    }
                }

                field.put(Pair(x, y), Triple(newTile, r, f))
                break // TODO: Doesn't actually break both loops...
            }
        }
        if (!field.containsKey(Pair(x,y))) {
            throw IllegalArgumentException("Can't place new tile for ($x, $y): $newTile")
        }
    }
}

//// Print field to check edges align
//field.forEach { pair, triple -> println("$pair: $triple") }
//for (y in 0..(maxDim)) {
//    for (i in 0..9) {
//        for (x in 0..(maxDim)) {
//            var pos = field[Pair(x,y)]!!
//            var img = rotateImage(if(pos.third) flipImage(tiles[pos.first]!!) else tiles[pos.first]!!, pos.second)
//            print("${img[i]} ")
//        }
//        println()
//    }
//    println()
//}
//println()
//println()

val lines: MutableList<String> = mutableListOf()
for (y in 0..(maxDim)) {
    for (i in 0..9) {
        if (i == 0 || i == 9) continue // Skip top and bottom edges
        var lineNum = y * 8 + i - 1
        lines.add("")
        for (x in 0..(maxDim)) {
            var pos = field[Pair(x,y)]!!
            var img = rotateImage(if(pos.third) flipImage(tiles[pos.first]!!) else tiles[pos.first]!!, pos.second)
            lines[lineNum] += img[i].substring(1, 9)
        }
    }
}
var sea = lines.toList()


// Pattern width: 20
val seaMonsterL1 = "..................#.".toRegex()
val seaMonsterL2 = "#....##....##....###".toRegex()
val seaMonsterL3 = ".#..#..#..#..#..#...".toRegex()
val seaMonsterSpace = 15
var monsterCount = 0
for (r in 0..3) {
    for (f in listOf(true, false)) {
//        println("Flipped: $f, Rotated: $r")
        var newSea = rotateImage(if(f) flipImage(sea) else sea, r)
        println()
        monsterCount = 0
        for (i in newSea.indices) {
            seaMonsterL2.findAll(newSea[i]).iterator().forEach {
                if (i != 0 && i != newSea.lastIndex) {
                    // Find index
                    var index = newSea[i].indexOf(it.value)
                    if (seaMonsterL1.matches(newSea[i-1].substring(index,index+20)) && seaMonsterL3.matches(newSea[i+1].substring(index,index+20))) {
                        monsterCount++
                        println("-Line $i/$index: ${it.value}")
                    }
                }
            }
        }
        if (monsterCount > 0) {
            println("Monstercount: $monsterCount")
        }
    }
}

println("Part 2: Number of # = ${sea.map { it -> it.count { c -> c == '#' } }.sum()}")