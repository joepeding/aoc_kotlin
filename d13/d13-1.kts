import java.io.File

var lines: List<String> = File("input").readLines()
var timestamp: Int = lines[0].toInt()
var busses: List<Int> = lines[1].split(',').filter {it -> it != "x"}.map {it -> it.toInt()}
var busWaitTimes: List<Pair<Int,Int>> = busses.map { it -> it to (it - (timestamp % it)) }

busWaitTimes.sortedBy { it -> it.second }.get(0).let {println("Part 1: ${it.first * it.second}")}