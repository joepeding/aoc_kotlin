import java.io.File
var lastSpoken: MutableMap<Int, Pair<Int,Int>> = File("input").readLines().map { it -> it.split(",") }.flatten().mapIndexed { i, n -> n.toInt() to Pair(0,i+1) }.toMap().toMutableMap()
var turnNum: Int = 7
var previousTurn: Int = lastSpoken.toList().filter { (_, turn) -> turn.second == turnNum - 1 }[0].first

while (turnNum <= 30000000) {
    var lastEncounters: Pair<Int, Int> = lastSpoken[previousTurn] ?: Pair(0,0)
    if (lastEncounters.second == turnNum - 1) {
        previousTurn = if (lastEncounters.first == 0) 0 else turnNum - lastEncounters.first - 1
    } else {
        previousTurn = if (lastEncounters.second == 0) 0 else turnNum - lastEncounters.second - 1
    }
    lastSpoken.put(previousTurn, Pair(lastSpoken[previousTurn]?.second ?: 0, turnNum))
    turnNum++
    if(turnNum % 100000 == 0) {println(turnNum)}
}

println("Part 2: ${lastSpoken.toList().filter { (_, turn) -> turn.second == 30000000 }[0].first}")