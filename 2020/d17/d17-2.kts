import java.io.File

// Parse input
var coords: MutableMap<List<Int>, Int> = File("input").readText().split("\\s".toRegex()).mapIndexed { y, row -> row.mapIndexed {x, c -> listOf(x, y, 0, 0) to if(c == '.') 0 else 1} }.flatten().toMap().toMutableMap()

fun coordAround(xIn: Int, yIn: Int, zIn: Int, wIn: Int): List<List<Int>> {
    val returnValue: MutableList<List<Int>> = mutableListOf()
    for(x in (xIn-1)..(xIn+1)) {
        for(y in (yIn-1)..(yIn+1)) {
            for(z in (zIn-1)..(zIn+1)) {
                for(w in (wIn-1)..(wIn+1)) {
                    if (!(x == xIn && y == yIn && z == zIn && w == wIn)) {
                        returnValue.add(listOf(x, y, z, w))
                    }
                }
            }
        }
    }
    return returnValue
}

for (iteration in 1..6) {
    var newCoords: MutableMap<List<Int>, Int> = mutableMapOf()
    var xs: List<Int> = coords.keys.map { it -> it[0] }
    var ys: List<Int> = coords.keys.map { it -> it[1] }
    var zs: List<Int> = coords.keys.map { it -> it[2] }
    var ws: List<Int> = coords.keys.map { it -> it[3] }
    var xRange = (xs.minOrNull()!! - 1)..(xs.maxOrNull()!! + 1)
    var yRange = (ys.minOrNull()!! - 1)..(ys.maxOrNull()!! + 1)
    var zRange = (zs.minOrNull()!! - 1)..(zs.maxOrNull()!! + 1)
    var wRange = (ws.minOrNull()!! - 1)..(ws.maxOrNull()!! + 1)

    for (x in xRange) {
        for (y in yRange) {
            for (z in zRange) {
                for (w in wRange) {
                    var coord = listOf(x, y, z, w)
                    if (coords[coord] == 1) {
                        if ((2..3).contains(coords.filter { (k, _) -> coordAround(x, y, z, w).contains(k) }.values.sum())) {
//                            newCoords.put(coord, 1)
                            // DO nothing
                        } else {
                            newCoords.put(coord, 0)
                        }
                    } else {
                        if (coords.filter { (k, _) -> coordAround(x, y, z, w).contains(k) }.values.sum() == 3) {
                            newCoords.put(coord, 1)
                        } else {
                            // Do nothing
//                            newCoords.put(coord, 0)
                        }
                    }
                }
            }
        }
    }

    coords.putAll(newCoords)
    println("Iteration $iteration: ${coords.values.sum()}")
}
println("Part 2: ${coords.values.sum()}")
