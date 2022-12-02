import java.io.File

var tiles: Map<Long, List<String>> = File("input").readText().split("\\n\\n".toRegex()).map {it -> it.split("\\n".toRegex()) }.map {it -> it[0].substring(5, it[0].lastIndex).toLong() to it.subList(1, it.size)}.toMap()
var edgeLength: Long = tiles[tiles.keys.first()]!!.size.toLong()

fun edgeStrings(img: List<String>): List<String> = listOf(
        img.first(), // Top edge
        img.first().reversed(), // Top edge, flipped
        img.map { it -> it.last().toString() }.reduce { acc, s -> acc + s }, // Right edge
        img.map { it -> it.last().toString() }.reduce { acc, s -> acc + s }.reversed(), // Right edge, flipped
        img.last(), // Top edge
        img.last().reversed(), // Top edge, flipped
        img.map { it -> it.first().toString() }.reduce { acc, s -> acc + s }, // Left edge
        img.map { it -> it.first().toString() }.reduce { acc, s -> acc + s }.reversed(), // Left edge, flipped
)

var imageEdges: Map<Long, List<String>> = tiles.mapValues { it -> edgeStrings(it.value) }
var corners: Map<Long, List<String>> = imageEdges.filter { (img,edges) -> edges.filter { edge -> imageEdges.filter { (otherImg, _) -> otherImg != img }.values.flatten().all { otherEdge -> otherEdge != edge } }.count() == 4 }
println("Part 1: ${corners.keys}")
println("Part 1: ${corners.keys.reduce { acc, i -> acc * i }}")
