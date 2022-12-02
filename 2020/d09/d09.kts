import java.io.File

var numbers: List<Long> = File("input").readLines().map { it -> it.toLong() }
var preamble = 25

fun validNumber(preamble: List<Long>, n: Long): Boolean {
    preamble.forEach { first ->
        if (preamble.filter { second -> second != first }.any { second -> first + second == n }) {
            return true
        }
    }
    return false
}

var faults: List<Long> = numbers.filterIndexed { i, n -> i >= preamble && !validNumber(numbers.subList(i-preamble, i), n) }
var fault = faults[0]
println("Fault: $fault")

fun contiguousListUntil(list: MutableList<Long>, source: List<Long>, nextIndex: Int, target: Long): Pair<List<Long>, Boolean> {
    if (list.sum() == target) {
        return Pair(list, true)
    } else if (list.sum() > target) {
        return Pair(list, false)
    } else {
        list.add(source[nextIndex])
        return contiguousListUntil(list, source, nextIndex + 1, target)
    }
}

println( numbers.mapIndexed { i, _ -> contiguousListUntil(mutableListOf<Long>(), numbers, i, fault) }.filter { it -> it.second && it.first.size > 1}.map { it -> it.first.minOrNull()!! + it.first.maxOrNull()!! } )