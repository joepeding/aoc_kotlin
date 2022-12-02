var input = "962713854".toList().map {it -> it.toString().toInt()}.toMutableList()

fun findTargetIndex(cups: List<Int>, current: Int): Int {
    var lowerCups = cups.filter { it < current }
    if (lowerCups.size > 0) {
        return cups.indexOf(lowerCups.maxOrNull()!!)
    } else {
        return cups.indexOf(cups.maxOrNull()!!)
    }
}

input.addAll(6..100000)
input.addAll(900000..1000000)

for (m in 1..(input.size * 10)) {
    if(m % 10000 == 0) { println("Move: $m") }

    var current = input[0]
    var held = listOf(input[1]!!, input[2]!!, input[3]!!)
    input.removeAll(held)
    var targetIndex = findTargetIndex(input, current) + 1
    input.addAll(targetIndex, held)
    input.removeAt(0)
    input.add(current)
}

println("10k: [1, 999282, 701, 999988]")
println("10k: [1, 8879, 7516, 3684]")
println("20k: [1, 996986, 19335, 16048]")
println("100k: ")
println("Part 2: ${input.subList(input.indexOf(1), input.indexOf(1) + 4)}")
