import java.io.File

// Parse input
var coords: MutableMap<Triple<Int, Int, Int>, Int> = File("testinput").readText().split("\\s".toRegex()).mapIndexed { y, row -> row.mapIndexed {x, c -> Triple(x, y, 0) to if(c == '.') 0 else 1} }.flatten().toMap().toMutableMap()

fun coordAround(xIn: Int, yIn: Int, zIn: Int): List<Triple<Int,Int,Int>> {
    val returnValue: MutableList<Triple<Int,Int,Int>> = mutableListOf()
    for(x in (xIn-1)..(xIn+1)) {
        for(y in (yIn-1)..(yIn+1)) {
            for(z in (zIn-1)..(zIn+1)) {
                if(!(x == xIn && y == yIn && z == zIn)) {
                    returnValue.add(Triple(x, y, z))
                }
            }
        }
    }
    return returnValue
}

fun printCoords(coords: MutableMap<Triple<Int, Int, Int>, Int>) {
    var xs: List<Int> = coords.keys.map { it -> it.first }
    var ys: List<Int> = coords.keys.map { it -> it.second }
    var zs: List<Int> = coords.keys.map { it -> it.third }
    var xRange = (xs.minOrNull()!!)..(xs.maxOrNull()!!)
    var yRange = (ys.minOrNull()!!)..(ys.maxOrNull()!!)
    var zRange = (zs.minOrNull()!!)..(zs.maxOrNull()!!)

    for (z in zRange) {
        println("Z: $z")
        for (y in yRange) {
            for (x in xRange) {
                print(coords[Triple(x, y, z)])
            }
            println("")
        }
    }
}

println(coordAround(1,2,1).map { it -> it to coords[it] }.filter { it -> it.second != null})

for (iteration in 1..6) {
    var newCoords: MutableMap<Triple<Int, Int, Int>, Int> = mutableMapOf()
    var xs: List<Int> = coords.keys.map { it -> it.first }
    var ys: List<Int> = coords.keys.map { it -> it.second }
    var zs: List<Int> = coords.keys.map { it -> it.third }
//    var xRange = if (iteration == 1) (xs.minOrNull()!!)..(xs.maxOrNull()!!) else (xs.minOrNull()!! - 1)..(xs.maxOrNull()!! + 1)
//    var yRange = if (iteration == 1) (ys.minOrNull()!!)..(ys.maxOrNull()!!) else (ys.minOrNull()!! - 1)..(ys.maxOrNull()!! + 1)
//    var zRange = if (iteration == 1) (-1)..1 else (zs.minOrNull()!! - 1)..(zs.maxOrNull()!! + 1)
    var xRange = (xs.minOrNull()!! - 1)..(xs.maxOrNull()!! + 1)
    var yRange = (ys.minOrNull()!! - 1)..(ys.maxOrNull()!! + 1)
    var zRange = (zs.minOrNull()!! - 1)..(zs.maxOrNull()!! + 1)

    for (x in xRange) {
        for (y in yRange) {
            for (z in zRange) {
                if(coords[Triple(x,y,z)] == 1) {
                    if ((2..3).contains(coords.filter { (k,_) -> coordAround(x,y,z).contains(k) }.values.sum())) {
                        newCoords.put(Triple(x, y, z), 1)
                    } else {
                        newCoords.put(Triple(x, y, z), 0)
                    }
                } else {
                    if (coords.filter { (k,_) -> coordAround(x,y,z).contains(k) }.values.sum() == 3) {
                        newCoords.put(Triple(x, y, z), 1)
                    } else {
                        newCoords.put(Triple(x, y, z), 0)
                    }
                }
            }
        }
    }

    coords = newCoords
    println("Iteration $iteration: ${coords.values.sum()}")
    printCoords(coords)
}
println("Part 2: ${coords.values.sum()}")
