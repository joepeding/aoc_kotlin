var input = "962713854".toList().map {it -> it.toString().toInt()}.toMutableList()

fun findTargetIndex(cups: List<Int>, current: Int): Int {
    var targetCup = if (current - 1 == 0) cups.maxOrNull()!! else current - 1
    if (cups.contains(targetCup)) {
//        println("-Target: $targetCup")
        return cups.indexOf(targetCup)
    } else {
        return findTargetIndex(cups, targetCup)
    }
}

fun moveCups(cups: List<Int>, num: Int): List<Int> {
//    println("Starting with ${cups.map { it -> it.toString() }.reduce { acc, s -> acc + s }}")
    if (num < 1) {
        return cups
    }
    var current = cups[0]
//    println("-Current: $current")
    var remaining = listOf(listOf(cups[0]) + cups.subList(4, cups.size)).flatten().toMutableList()
//    println("-Remaining: $remaining")
    var held = cups.subList(1,4)
//    println("-Held: $held")
    var targetIndex = findTargetIndex(remaining, current) + 1
//    println("-TargetIndex: $targetIndex")
    remaining.addAll(targetIndex, held)
//    println("-New: $remaining")
    remaining.removeAt(0)
    remaining.add(current)
//    println("-New current: $remaining")
    if(num % 10000 == 0) { println("Remaining moves: $num") }
    return moveCups(remaining, num - 1)
}

println("Part 1: [2, 9, 7, 8, 1, 6, 5, 4, 3] (correct answer)")
println("Part 1: ${moveCups(input, 100)}")

input.addAll(6..1000000)
println("Part 2: ${moveCups(input, 10000000)}")
