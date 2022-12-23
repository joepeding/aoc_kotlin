import java.io.File

val input: String = File("input").readText().trim()
// Part 1
print("Answer 1: ")
solve()

// Part 2
print("Answer 2: ")
solve(true)

data class Coord(var x: Int, var y: Int) {
    override fun toString(): String = "($x, $y)"
    override fun equals(other: Any?): Boolean {
        if (other is Coord) {
            return other.x == this.x && other.y == this.y
        } else {
            return super.equals(other)
        }
    }
}

fun solve(continueUntilStable: Boolean = false) {
    var elves = mutableListOf<Coord>()
    input.split("\n").forEachIndexed { y, s ->
        s.forEachIndexed { x, c -> if (c == '#') { elves.add(Coord(x,y)) } }
    }

    var round = 0
    while (round < 10 || continueUntilStable) {
        var proposedMoves = elves.map { determineMove(it, elves, round) }

        var newElves = mutableListOf<Coord>()
        elves.forEachIndexed { i, e ->
            if (proposedMoves.count { it == proposedMoves[i] } == 1) {
                newElves.add(proposedMoves[i])
            } else {
                newElves.add(e)
            }
        }

        if (elves.all { newElves.contains(it) } && newElves.all { elves.contains(it) }) {
            // No elf moved
            break
        }
        elves = newElves
        round++
    }

    if (continueUntilStable) {
        println("Round ${round + 1}")
    } else {
        // Find rectangle
        var minX = elves.map { it.x }.min()
        var maxX = elves.map { it.x }.max()
        var minY = elves.map { it.y }.min()
        var maxY = elves.map { it.y }.max()
        println("x: ${ maxX - minX + 1}, y: ${ maxY - minY + 1}, e: ${elves.size} -> ${ (maxX - minX + 1) * (maxY - minY + 1) } - ${elves.size} = ${ (maxX - minX + 1) * (maxY - minY + 1) - elves.size}")
    }
}

fun directionsForRound(r: Int): List<Char> {
    val dirs = listOf('N', 'S', 'W', 'E')
    return listOf(dirs[r % 4], dirs[(r + 1) % 4], dirs[(r + 2) % 4], dirs[(r + 3) % 4])
}

fun determineMove(e: Coord, es: List<Coord>, r: Int): Coord {
    var NW = Coord(e.x - 1, e.y - 1)
    var N = Coord(e.x, e.y - 1)
    var NE = Coord(e.x + 1, e.y - 1)
    var W = Coord(e.x - 1, e.y)
    var E = Coord(e.x + 1, e.y)
    var SW = Coord(e.x - 1, e.y + 1)
    var S = Coord(e.x, e.y + 1)
    var SE = Coord(e.x + 1, e.y + 1)
    var around = listOf(NW, N, NE, W, E, SW, S, SE)
    if (around.none { es.contains(it) }) {
        return e
    }
    return directionsForRound(r).map { dir ->
        when(dir) {
            'N' -> if (listOf(NW, N, NE).none { es.contains(it) }) { N } else { null }
            'S' -> if (listOf(SW, S, SE).none { es.contains(it) }) { S } else { null }
            'W' -> if (listOf(NW, W, SW).none { es.contains(it) }) { W } else { null }
            'E' -> if (listOf(NE, E, SE).none { es.contains(it) }) { E } else { null }
            else -> throw Exception("Invalid direction")
        }
    }.filterNotNull().firstOrNull() ?: e
}

fun printElves(elves: List<Coord>, r: Int = -1) {
    var minX: Int = elves.map { it.x }.min()
    var maxX: Int = elves.map { it.x }.max()
    var minY: Int = elves.map { it.y }.min()
    var maxY: Int = elves.map { it.y }.max()

    println("Round $r - Elves: ${elves.size}")
    (minY..maxY).forEach { y ->
        (minX..maxX).forEach { x ->
            if (elves.contains(Coord(x, y))) {
                print("#")
            } else {
                print(".")
            }
        }
        println()
    }
}
