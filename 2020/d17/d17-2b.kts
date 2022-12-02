import java.io.File

// Parse input
var coords: MutableMap<List<Int>, Int> = File("input").readText().split("\\s".toRegex()).mapIndexed { y, row -> row.mapIndexed {x, c -> listOf(x, y, 0, 0) to if(c == '.') 0 else 1} }.flatten().toMap().toMutableMap()

fun coordAround(xIn: Int, yIn: Int, zIn: Int, wIn: Int, includeThis: Boolean = false): List<List<Int>> {
    val returnValue: MutableList<List<Int>> = mutableListOf()
    for(x in (xIn-1)..(xIn+1)) {
        for(y in (yIn-1)..(yIn+1)) {
            for(z in (zIn-1)..(zIn+1)) {
                for(w in (wIn-1)..(wIn+1)) {
                    if (!(x == xIn && y == yIn && z == zIn && w == wIn) || includeThis) {
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
    var coordsToCheck = coords.keys.map { it -> coordAround(it[0], it[1], it[2], it[3], true) }.flatten().toSet()
    for (coord in coordsToCheck) {
        var (x, y, z, w) = coord
        if (coords[coord] == 1) {
            if ((2..3).contains(coords.filter { (k, _) -> coordAround(x, y, z, w).contains(k) }.values.sum())) {
              newCoords.put(coord, 1)
            }
        } else {
            if (coords.filter { (k, _) -> coordAround(x, y, z, w).contains(k) }.values.sum() == 3) {
                newCoords.put(coord, 1)
            }
        }
    }

    coords = newCoords
    println("Iteration $iteration: ${coords.values.sum()}")
}
println("Part 2: ${coords.values.sum()}")
