import java.io.File

var lines: List<String> = File("input").readLines()
var timestamp: Long = lines[0].toLong()
var busses: List<Pair<Long, Long>> = lines[1].split(',').mapIndexed { i, b -> b to i }.filter { it -> it.first != "x" }.map { it -> Pair(it.first.toLong(), it.second.toLong())}

println(busses)
fun firstOccurrenceForSubset(subset: List<Pair<Long, Long>>): Long {
    println("-Checking for $subset")
    if(subset.size == 2) {
        var occurrences = (0L..(subset[0].first * subset[1].first)).also { timesTested += it.count() }.filter { time -> subset.all { bus -> (time + bus.second) % bus.first == 0L } }
        return occurrences.get(0)
    } else {
        var progStart: Long = firstOccurrenceForSubset(subset.subList(1, subset.lastIndex+1))
        var progEnd: Long = subset.map { it -> it.first }.reduce { acc, bus -> acc * bus }
        var progStep: Long = subset.subList(1, subset.lastIndex).map {it -> it.first}.reduce {acc, bus -> acc * bus}
        println("--$progStart..$progEnd step $progStep: ${(progEnd-progStart) / progStep}")
        var prog = (progStart)..(progEnd) step progStep
        timesTested += prog.count()
        return prog.filter { time -> subset.all { bus -> (time + bus.second) % bus.first == 0L } }.get(0)
    }
}

var timesTested = 0
println("Part2: ${firstOccurrenceForSubset(busses)} - by checking $timesTested times.")
timesTested = 0
println("Part2: ${firstOccurrenceForSubset(busses.sortedBy { it -> it.first })} - by checking $timesTested times.")
timesTested = 0
println("Part2: ${firstOccurrenceForSubset(busses.sortedBy { it -> it.first }.reversed())} - by checking $timesTested times.")
