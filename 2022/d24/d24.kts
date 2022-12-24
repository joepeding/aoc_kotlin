import java.io.File

val input: String = File("input").readText().trim()
val valleyTimeCache: MutableMap<Int, MutableMap<Int, MutableMap<Int, MutableList<Char>>>> = mutableMapOf()

// Part 1
println("Answer 1: ")
solve()

// Part 2
print("Answer 2: ")
solve(thereAndBackAgain = true)

data class Position(var x: Int, var y: Int, var t: Int) {
    override fun toString(): String = "($x, $y) at $t"
    override fun equals(other: Any?): Boolean {
        if (other is Position) {
            return other.x == this.x && other.y == this.y && this.t == other.t
        } else {
            return super.equals(other)
        }
    }
}

fun solve(thereAndBackAgain: Boolean = false) {
    // Initial state
    var valley = mutableMapOf<Int, MutableMap<Int, MutableList<Char>>>()
    input
        .split("\n")
        .forEachIndexed { y, l ->
            valley.put(y, mutableMapOf<Int, MutableList<Char>>())
            l.forEachIndexed { x, c -> valley.get(y)!!.put(x, mutableListOf(c)) }
        }
    valleyTimeCache.put(0, valley)
    var start = Position(1,0, 0)
    var end = Position(valley[0]!!.keys.max() - 1, valley.keys.max(), Int.MAX_VALUE)

    // Get path
    if (!thereAndBackAgain) {
        println(findShortestPathFromTo(start, end))
    } else {
        var there = findShortestPathFromTo(start, end)
        var back = findShortestPathFromTo(
            Position(end.x, end.y, there),
            Position(start.x, start.y, Int.MAX_VALUE)
        )
        var again = findShortestPathFromTo(
            Position(start.x, start.y, back),
            Position(end.x, end.y, Int.MAX_VALUE)
        )
        println("$there -> $back -> $again")
    }
}

fun findShortestPathFromTo(start: Position, end: Position): Int {
    val queue: MutableList<Position> = mutableListOf()
    var dest: Position? = null
    queue.add(start)
    while (queue.isNotEmpty()) {
        var pos = queue.removeFirst()
        if (pos.x == end.x && pos.y == end.y) {
            dest = pos
            break
        }
        visit(queue, pos.x - 1, pos.y, pos.t + 1, pos);
        visit(queue, pos.x + 1, pos.y, pos.t + 1, pos);
        visit(queue, pos.x, pos.y - 1, pos.t + 1, pos);
        visit(queue, pos.x, pos.y + 1, pos.t + 1, pos);
        visit(queue, pos.x, pos.y, pos.t + 1, pos);
    }

    if (dest == null) {
        return Integer.MAX_VALUE
    } else {
        return dest.t
    }
}

fun visit(queue: MutableList<Position>, x: Int, y: Int, t: Int, previous: Position) {
    if (x < 0 || x > valleyAtTime(t).get(0)!!.keys.max() || y < 0 || y > valleyAtTime(t).keys.max()) { return }
    var posAtTime = valleyAtTime(t).get(y)!!.get(x)!!
    if (posAtTime.size > 0) { return }
    if (queue.contains(Position(x,y,t))) { return }
    queue.add(Position(x, y, t))
}

fun valleyAtTime(time: Int): MutableMap<Int, MutableMap<Int, MutableList<Char>>> {
    if (valleyTimeCache.containsKey(time)) { return valleyTimeCache[time]!! }
    var newValley = mutableMapOf<Int, MutableMap<Int, MutableList<Char>>>()
    // Initiate valley
    valleyAtTime(time - 1).forEach { y, row ->
        newValley.put(y, mutableMapOf<Int, MutableList<Char>>())
        row.forEach { x, pos ->
            newValley[y]!!.put(x, mutableListOf())
        }
    }

    // Fill walls first
    valleyAtTime(time - 1).forEach { y, row ->
        row.forEach { x, pos ->
            pos.forEach {
                if (it == '#') { newValley.get(y)!!.get(x)!!.add('#') }
            }
        }
    }

    // Fill blizzards
    var maxX = newValley[0]!!.keys.max()
    var maxY = newValley.keys.max()
    valleyAtTime(time - 1).forEach { y, row ->
        row.forEach { x, pos ->
            pos.forEach {
                when(it) {
                    '>' -> if (newValley.get(y)!!.get(x + 1)!!.contains('#')) { newValley.get(y)!!.get(1)!!.add('>') } else { newValley.get(y)!!.get(x + 1)!!.add('>') }
                    '<' -> if (newValley.get(y)!!.get(x - 1)!!.contains('#')) { newValley.get(y)!!.get(maxX - 1)!!.add('<') } else { newValley.get(y)!!.get(x - 1)!!.add('<') }
                    'v' -> if (newValley.get(y + 1)!!.get(x)!!.contains('#')) { newValley.get(1)!!.get(x)!!.add('v') } else { newValley.get(y + 1)!!.get(x)!!.add('v') }
                    '^' -> if (newValley.get(y - 1)!!.get(x)!!.contains('#')) { newValley.get(maxY - 1)!!.get(x)!!.add('^') } else { newValley.get(y - 1)!!.get(x)!!.add('^') }
                }
            }
        }
    }

    valleyTimeCache.put(time, newValley) // Cache valley at time, so it isn't calculated over and over again.
    return newValley
}

fun printValley(valley: MutableMap<Int, MutableMap<Int, MutableList<Char>>>) {
    valley.forEach {
        it.value
            .map { s ->
                when (s.value.size) {
                    0 -> "."
                    1 -> s.value.first().toString()
                    else -> s.value.size.toString()
                }
            }.joinToString("")
            .also(::println)
    }
}
