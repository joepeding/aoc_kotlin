import java.io.File

fun edgeStrings(img: List<String>): List<String> = listOf(
        img.first(), // Top edge
        img.map { it -> it.last().toString() }.reduce { acc, s -> acc + s }, // Right edge
        img.last(), // Top edge
        img.map { it -> it.first().toString() }.reduce { acc, s -> acc + s }, // Left edge
)

fun edgeStringsReversed(img: List<String>): List<String> = listOf(
        img.first().reversed(), // Top edge, flipped
        img.map { it -> it.last().toString() }.reduce { acc, s -> acc + s }.reversed(), // Right edge, flipped
        img.last().reversed(), // Top edge, flipped
        img.map { it -> it.first().toString() }.reduce { acc, s -> acc + s }.reversed(), // Left edge, flipped
)

fun allPossibleEdges(img: List<String>): List<String> = listOf(edgeStrings(img), edgeStringsReversed(img)).flatten()

fun flipImage(img: List<String>): List<String> = img.map {it -> it.reversed()}

//Part 1
var tiles: Map<Long, List<String>> = File("testinput").readText().split("\\n\\n".toRegex()).map {it -> it.split("\\n".toRegex()) }.map {it -> it[0].substring(5, it[0].lastIndex).toLong() to it.subList(1, it.size)}.toMap()
var tilesToEdges: Map<Long, List<String>> = tiles.mapValues { it -> allPossibleEdges(it.value) }
var corners: Map<Long, List<String>> = tilesToEdges.filter { (tile,edges) -> edges.filter { edge -> tilesToEdges.filter { (otherTile, _) -> otherTile != tile }.none { otherTile -> otherTile.value.any { it == edge } } }.count() == 4 }
println("Part 1 correct: [1117, 1543, 1213, 1291](input) or [951, 1171, 2971, 3079](testinput)")
println("Part 1: ${corners.keys}")
println("Part 1: ${corners.keys.reduce { acc, i -> acc * i }}")

var edges: Map<Long, List<String>> = tilesToEdges.filter { (tile,edges) -> edges.filter { edge -> tilesToEdges.filter { (otherTile, _) -> otherTile != tile }.none { otherTile -> otherTile.value.any { it == edge } } }.count() == 2 }
println("Part 2: ${edges.count()}/40 edges")
var centerPieces: Map<Long, List<String>> = tilesToEdges.filter { (tile,edges) -> edges.filter { edge -> tilesToEdges.filter { (otherTile, _) -> otherTile != tile }.none { otherTile -> otherTile.value.any { it == edge } } }.count() == 0 }
println("Part 2: ${centerPieces.count()}/100 center pieces")



tilesToEdges.forEach { l, list -> println("$l: $list") }

fun rotateImage(img: List<String>, times: Int): List<String> = if (times == 0) img else rotateImage(img.mapIndexed { i, _ -> img.map { it -> it[i].toString() }.reduce { acc, s -> acc + s }}, times - 1)

fun findTilesWith(searchTiles: Map<Long, List<String>>, allTiles: Map<Long, List<String>>, top: String, right: String, bottom: String, left: String): MutableList<Triple<Long, Int, Boolean>> {
    if (top.isEmpty() && right.isEmpty() && bottom.isEmpty() && left.isEmpty()) {
        throw IllegalArgumentException("Provide at least some constraints")
    }

    var returnList: MutableList<Triple<Long, Int, Boolean>> = mutableListOf()

    for (tile in searchTiles) {
        println("-Checking $tile")
        for (r in 0..3) {
            for (flipped in listOf(false, true)) {
                var edges = if(flipped) edgeStringsReversed(rotateImage(tile.value, r)) else edgeStrings(rotateImage(tile.value, r))
//                println("--Orientation R${r}F${if(flipped) 1 else 0}: $edges")
                var otherTileEdges = tilesToEdges.filter { (k,_) -> k != tile.key }
                // Check top
                if (top == "none" && otherTileEdges.any { it.value.any { it == edges[0] } }) { // If top = 'none', top edge should match no other edges
//                    println("Top none")
                    continue
                } else if (top == "any" && !otherTileEdges.any { it.value.any { it == edges[0] } }) { // If top = 'any', top edge should not match no other edges
//                    println("Top any")
                    continue
                } else if (!listOf("none","any").contains(top) && top != edges[0]) { // If is not 'none' or 'any', top edge should match specified value
//                    println("Top check")
                    continue
                }
//                println("---Top+")

                // Check right
                if (right == "none" && otherTileEdges.any { it.value.any { it == edges[1] } }) {
                    continue
                } else if (right == "any" && !otherTileEdges.any { it.value.any { it == edges[1] } }) {
                    continue
                } else if (!listOf("none","any").contains(right) && right != edges[1]) {
                    continue
                }
//                println("---Right+")

                // Check bottom
                if (bottom == "none" && otherTileEdges.any { it.value.any { it == edges[2] } }) {
                    continue
                } else if (bottom == "any" && !otherTileEdges.any { it.value.any { it == edges[2] } }) {
                    continue
                } else if (!listOf("none","any").contains(bottom) && bottom != edges[2]) {
                    continue
                }
//                println("---Bottom+")

                // Check left
                if (left == "none" && otherTileEdges.any { it.value.any { it == edges[3] } }) {
                    continue
                } else if (left == "any" && !otherTileEdges.any { it.value.any { it == edges[3] } }) {
                    continue
                } else if (!listOf("none","any").contains(left) && left != edges[3]) {
                    continue
                }
//                println("---Left+")

                returnList.add(Triple(tile.key, r, flipped))
            }
        }
    }

    return returnList
}

var field: MutableMap<Pair<Int, Int>, List<String>> = mutableMapOf()
fun fitPieces(
        field: Map<Pair<Int, Int>, List<String>>,
        allTiles: Map<Long, List<String>>,
        remainingCorners: Map<Long, List<String>>,
        remainingEdges: Map<Long, List<String>>,
        remainingCenters: Map<Long, List<String>>,
        dim: Int
): Map<Pair<Int, Int>, List<String>>? {
    var maxX = dim - 1
    var maxY = dim - 1
    for (y in 0..maxY) {
        for (x in 0..maxX) {
            if(field.containsKey(Pair(x,y))) {
                continue
            } else {
                // Set contstraints for search
                var topConstraint = "any"
                var rightConstraint = "any"
                var bottomConstraint = "any"
                var leftConstraint = "any"
                var leftNeighbour = Pair(x-1, y)
                var topNeighbour = Pair(x, y-1)
                var searchList: Map<Long, List<String>>
                if (y == 0) {
                    topConstraint = "none"
                } else if (y == maxY) {
                    bottomConstraint = "none"
                }
                if (x == 0) {
                    leftConstraint = "none"
                } else if (x == maxX) {
                    rightConstraint = "none"
                }
                if (field.containsKey(leftNeighbour)) {
                    leftConstraint = edgeStrings(field[leftNeighbour]!!)[1]
                }
                if (field.containsKey(topNeighbour)) {
                    topConstraint = edgeStrings(field[topNeighbour]!!)[2]
                }
                if (listOf(0, maxX).contains(x) && listOf(0, maxY).contains(y)) {
                    searchList = remainingCorners
                } else if (listOf(0, maxX).contains(x) || listOf(0, maxY).contains(y)) {
                    searchList = remainingEdges
                } else {
                    searchList = remainingCenters
                }

                // Search tile
                println("Looking for ($x, $y): [$topConstraint, $rightConstraint, $bottomConstraint, $leftConstraint]")
                var newTiles = findTilesWith(searchList, allTiles, topConstraint, rightConstraint, bottomConstraint, leftConstraint)
                if (newTiles.isEmpty()) {
                    return null
                }
                for (newTile in newTiles) {
                    var newTileAdjusted = if (newTile.third) rotateImage(searchList[newTile.first]!!, newTile.second) else rotateImage(searchList[newTile.first]!!, newTile.second)
                    var newField = field.toMutableMap()
                    newField.put(Pair(x,y), newTileAdjusted)
                    var result = fitPieces(
                            newField,
                            allTiles,
                            remainingCorners.filter { it -> it.key != newTile.first },
                            remainingEdges.filter { it -> it.key != newTile.first },
                            remainingCenters.filter { it -> it.key != newTile.first },
                            dim
                    )
                    if (result == null) {
                        continue
                    } else {
                        return result
                    }
                }
            }
        }
    }

    return field
}

// Position first corner piece

//var topLeft: Pair<Long, List<String>> = Pair(0L, listOf())
//for (c in 0..3) {
//    var firstCorner = tiles.filter { it.key == corners.entries.filterIndexed { index, _ -> index >= c }.first().key }.entries.first()
////    println("${firstCorner.key}: ${firstCorner.value}")
//    for (r in 0..3) {
//        var matchedEdges: List<Int> = edgeStrings(rotateImage(firstCorner.value, r)).map { edge -> tilesToEdges.filter { it -> it.key != firstCorner.key }.values.filter { image -> image.any { otherEdge -> otherEdge == edge } }.count() }
////        println("C${c}R${r}F0: $matchedEdges")
//        if (matchedEdges[0] == 0 && matchedEdges[3] == 0 && matchedEdges[1] > 0 && matchedEdges[2] > 0) {
//            topLeft = Pair(firstCorner.key, rotateImage(firstCorner.value, r))
//            break
//        }
//        var matchedEdgesFlipped: List<Int> = edgeStringsReversed(rotateImage(firstCorner.value, r)).map { edge -> tilesToEdges.filter { it -> it.key != firstCorner.key }.values.filter { image -> image.any { otherEdge -> otherEdge == edge } }.count() }
////        println("C${c}R${r}F1: $matchedEdgesFlipped")
//        if (matchedEdgesFlipped[0] == 0 && matchedEdgesFlipped[3] == 0 && matchedEdgesFlipped[1] > 0 && matchedEdgesFlipped[2] > 0) {
//            topLeft = Pair(firstCorner.key, rotateImage(firstCorner.value, r))
//            break
//        }
//    }
//}
//println(topLeft)
//var topLeft = findTilesWith(tiles.filter { (k,_) -> k == 1291L }, "none", "any", "any", "none")
//var topLeftAdjusted = if (topLeft.first().third) rotateImage(tiles[topLeft.first().first]!!, topLeft.first().second) else rotateImage(tiles[topLeft.first().first]!!, topLeft.first().second)

//println("Looking for: [none, any, any, ##.#...#..]")

//fitPieces(mutableMapOf(Pair(0,0) to topLeftAdjusted), tiles.filter { it -> it.key != topLeft.first().first }, Math.sqrt(tiles.count().toDouble()).toInt())
tiles.forEach { l, list -> println("$l: $list") }
fitPieces(mutableMapOf<Pair<Int,Int>,List<String>>(), tiles, tiles.filter { corners.keys.contains(it.key) }, tiles.filter { edges.keys.contains(it.key) }, tiles.filter { centerPieces.keys.contains(it.key) }, Math.sqrt(tiles.count().toDouble()).toInt())