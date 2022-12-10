import java.io.File

val input: String = File("input").readText()

// Common
val commands = input
    .split("\n")
    .filter { it.isNotEmpty() }
    .map { it.split(" ") }
    .map { command -> (1..(command[1].toInt())).map { _ -> command[0]} }
    .flatten()

fun moveKnot(n: Int): Unit {
    var head = if (n == 0) { hpos } else { kpos[n-1] }

    var xDist = head.first - kpos[n].first
    var yDist = head.second - kpos[n].second

    when (Pair(xDist, yDist)) {
        Pair(-2,  2) -> kpos[n] = Pair(kpos[n].first - 1 ,kpos[n].second + 1)
        Pair(-2,  1) -> kpos[n] = Pair(kpos[n].first - 1, kpos[n].second + 1)
        Pair(-2,  0) -> kpos[n] = Pair(kpos[n].first - 1, kpos[n].second + 0)
        Pair(-2, -1) -> kpos[n] = Pair(kpos[n].first - 1, kpos[n].second - 1)
        Pair(-2, -2) -> kpos[n] = Pair(kpos[n].first - 1, kpos[n].second - 1)

        Pair(-1, -2) -> kpos[n] = Pair(kpos[n].first - 1 ,kpos[n].second - 1)
        Pair(-1,  2) -> kpos[n] = Pair(kpos[n].first - 1 ,kpos[n].second + 1)

        Pair( 0, -2) -> kpos[n] = Pair(kpos[n].first + 0 ,kpos[n].second - 1)
        Pair( 0,  2) -> kpos[n] = Pair(kpos[n].first + 0 ,kpos[n].second + 1)

        Pair( 1, -2) -> kpos[n] = Pair(kpos[n].first + 1 ,kpos[n].second - 1)
        Pair( 1,  2) -> kpos[n] = Pair(kpos[n].first + 1 ,kpos[n].second + 1)

        Pair( 2,  2) -> kpos[n] = Pair(kpos[n].first + 1, kpos[n].second + 1)
        Pair( 2,  1) -> kpos[n] = Pair(kpos[n].first + 1, kpos[n].second + 1)
        Pair( 2,  0) -> kpos[n] = Pair(kpos[n].first + 1, kpos[n].second + 0)
        Pair( 2, -1) -> kpos[n] = Pair(kpos[n].first + 1, kpos[n].second - 1)
        Pair( 2, -2) -> kpos[n] = Pair(kpos[n].first + 1, kpos[n].second - 1)
    }

    if (n == kpos.size - 1) { visited.add(kpos[n]) }
}

// Part 1
println("Answer 1: ")
var hpos: Pair<Int, Int> = Pair(0,0)
val kpos = mutableListOf(Pair(0,0))
var visited: MutableSet<Pair<Int, Int>> = mutableSetOf(hpos)
commands
    .forEach {
        when (it) {
            "R" -> hpos = Pair(hpos.first + 1, hpos.second)
            "L" -> hpos = Pair(hpos.first - 1, hpos.second)
            "U" -> hpos = Pair(hpos.first, hpos.second + 1)
            "D" -> hpos = Pair(hpos.first, hpos.second - 1)
        }
        moveKnot(0)
    }
println(visited.size)

//Part 2
println("Answer 2: ")
visited.clear()
kpos.clear()
hpos = Pair(0,0)
kpos.addAll((1..9).map { _ -> Pair(0,0) })
visited.add(hpos)

commands
    .forEach {
        when (it) {
            "R" -> hpos = Pair(hpos.first + 1, hpos.second)
            "L" -> hpos = Pair(hpos.first - 1, hpos.second)
            "U" -> hpos = Pair(hpos.first, hpos.second + 1)
            "D" -> hpos = Pair(hpos.first, hpos.second - 1)
        }
        (0..8).forEach { moveKnot(it) }
    }
println(visited.size)



