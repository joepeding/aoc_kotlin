import java.io.File

// Part 1
var numbers: List<Long> = File("input").readLines().map { it -> it.toLong() }.sorted()
var diffs: List<Long> = numbers.mapIndexed { i, l -> l - (if(i == 0) 0 else numbers[i-1]) }
println("Part 1: ${diffs.filter { it == 1L }.size * (diffs.filter { it == 3L }.size + 1)}")

// Part 1 on a single line
File("input").readLines().map { it -> it.toInt() }.sorted().let { it.mapIndexed { i, l -> l - (if(i == 0) 0 else it[i-1]) } }.let { println("Part 1: ${it.count { i -> i == 1 } * (it.count {i -> i == 3} + 1)}")}

// Part 2s
var connectors = mutableListOf<Long>(0)
connectors.addAll(numbers)
var connectorOptions = connectors.sorted().map { c -> c to connectors.filter{ it -> it > c && it <= c + 3L}}

var numberOfRoutesToEndFrom: MutableMap<Long, Long> = mutableMapOf()
numberOfRoutesToEndFrom.put(180L, 1L)
connectorOptions.reversed().forEach {
    if(!numberOfRoutesToEndFrom.containsKey(it.first)) {
        numberOfRoutesToEndFrom.put(it.first, it.second.map { c -> numberOfRoutesToEndFrom[c]!! }.sum())
    }
}
numberOfRoutesToEndFrom.forEach { println(it) }

// Part 2 shorter
var options: List<Pair<Long, List<Long>>> = numbers.toMutableList().also{it.add(0, 0L)}.let { connectors.map { c -> c to connectors.filter { it -> it > c && it <= c + 3L} } }
var numberOfRoutesFrom: MutableMap<Long, Long> = mutableMapOf(Pair(numbers.maxOrNull()!!, 1L))
options.reversed().filter {it.first != numbers.maxOrNull()!!}.forEach {
    numberOfRoutesFrom.put(it.first, it.second.map { c -> numberOfRoutesFrom[c]!! }.sum())
}
println(numberOfRoutesFrom[0])

