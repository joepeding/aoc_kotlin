import java.io.File
import java.util.regex.Pattern
import kotlin.math.min

val input: String = File("input").readText()

data class Valve(var label: String, val flow: Int, val tunnels: MutableList<Valve>) {
    override fun toString(): String = "Valve($label, $flow, [${tunnels.map { it.label }.joinToString()}])"
    override fun hashCode(): Int { return toString().hashCode() }
    override fun equals(other: Any?): Boolean {
        if (other is Valve) { return this.label == other.label } else {
            return super.equals(other)
        }
    }
}

fun shortestPath(a: Valve, b: Valve?): List<Valve> {
    val records: MutableMap<Valve, Valve?> = valves.map { it to null }.toMap().toMutableMap()
    val queue: MutableList<Valve> = mutableListOf()
    var dest: Valve? = null
    queue.add(a)
    while (queue.size > 0) {
        var v = queue.removeFirst()
        if (v == b) {
            dest = b
            break
        }
        v.tunnels.forEach {
            if (records.get(it) == null && it != a) {
                records.put(it, v)
                queue.add(it)
            }
        }
    }

    if (dest == null) { return listOf(valves, valves, valves).flatten() }
    var path = mutableListOf<Valve>()
    if (a == dest) { return path }
    path.add(dest)
    var previous = records[dest]
    while (previous != a && previous != null) {
        path.add(previous)
        previous = records[previous]
    }
    return path.reversed()
}

fun allPathsFrom(s: Valve, t: Int, l: List<Valve>): List<Pair<Int, List<Valve>>> = l
    .filter { it.flow > 0 }
    .map { v ->
        var path = shortestPath(s, v)
        if (path.size > valves.size) { return@map listOf() }
        var newTime = t - (path.size + 1)
        if (newTime < 1) { return@map listOf() }
        var worth = newTime * v.flow
        var paths =  allPathsFrom(v, newTime, l.filter { it != v })
            .map {
                Pair(
                    it.first + worth,
                    it.second.toMutableList()
                        .also { it.add(v) }
                )
            }.toMutableList()
        if (paths.isEmpty()) {
            paths.add(Pair(worth, mutableListOf(v)))
        }
        return@map paths
    }.flatten()

// Part 1
print("Answer 1: ")
val valves: List<Valve> = input
    .split("\n")
    .filter { it.isNotEmpty() }
    .map { line: String ->
        Pair(
            Valve(
                line.subSequence(6, 8).toString(),
                line.split("=", ";")[1].toInt(),
                mutableListOf()
            ),
            line.split(Regex("valves? "))[1].split(", ")
        )
    }.let { list: List<Pair<Valve, List<String> > > ->
        var valves = list.map { it.first }
        list.forEach { (valve, tunnels) ->
            var linkedValves = tunnels.map { t -> valves.filter { it.label == t }.first() }
            valve.tunnels.addAll(linkedValves)
        }
        return@let valves
    }

val aa = valves.filter { it.label == "AA" }.first()
var allPaths = allPathsFrom(aa, 30, valves).sortedByDescending { it.first }
println(allPaths.first().first)

// Part 2
print("Answer 2: ")
allPaths = allPathsFrom(aa, 26, valves).sortedByDescending { it.first }
var first = allPaths.first()
allPaths
    .filter { e -> e != allPaths.first() }
    .filter { e -> !first.second.any { v -> e.second.contains(v) }}
    .first()
    .let {
        println(first.first + it.first)
    }
