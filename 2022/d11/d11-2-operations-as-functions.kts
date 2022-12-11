import java.io.File
import kotlin.math.floor

val input: String = File("input").readText()

data class Monkey(
    var items: MutableList<Long>,
    val operation: (Long) -> Long,
    val test: Triple<Long, Int, Int>,
)

fun parseMonkeys(): MutableList<Monkey> = input
    .split("\n\n").filter { it.isNotEmpty() }
    .map { m ->
        m.split("\n").filter { it.isNotEmpty() }.let {
            Monkey(
                it[1].split(": ")[1].split(", ").map { s -> s.toLong() }.toMutableList(),
                parseOperation(it[2].split(": new = ")[1]),
                Triple(
                    it[3].split("by ")[1].toLong(),
                    it[4].split("monkey ")[1].toInt(),
                    it[5].split("monkey ")[1].toInt()
                )
            )
        }
    }
    .toMutableList()

fun parseOperation(s: String): (Long) -> Long {
    var (a, o, b) = s.split(" ")
    return when (o) {
        "+" -> { x: Long -> ((if(a == "old") { x } else { a.toLong() }) + (if(b == "old") { x } else { b.toLong() })) }
        "*" -> { x: Long -> ((if(a == "old") { x } else { a.toLong() }) * (if(b == "old") { x } else { b.toLong() })) }
        else -> throw Exception("Unknown operator")
    }
}

// Part 1
print("Answer 1: ")
var monkeys = parseMonkeys()
var inspections: MutableMap<Int, Int> = mutableMapOf()
monkeys.forEachIndexed { i, _ -> inspections.put(i, 0) }
(0..19).forEach { r ->
    monkeys.forEachIndexed { i, m ->
        m.items.forEach { item ->
            inspections.put(i, inspections.get(i)!! + 1)
            var newWorry = m.operation(item)
            var reducedWorry = floor(newWorry.toFloat() / 3).toLong()
            if (reducedWorry % m.test.first == 0L) {
                monkeys[m.test.second].items.add(reducedWorry)
            } else {
                monkeys[m.test.third].items.add(reducedWorry)
            }
        }
        m.items.clear()
    }
}
inspections.values.sortedDescending().let {
    it[0] * it[1]
}.also {
    println(it)
}

//Part 2
print("Answer 2: ")
inspections.clear()
monkeys = parseMonkeys()
monkeys.forEachIndexed { i, _ -> inspections.put(i, 0) }
val commonWorryDivider = monkeys.map { it.test.first }.fold(1L) { a, b -> a * b }

(0..9999).forEach { r ->
    monkeys.forEachIndexed { i, m ->
        m.items.forEach { item ->
            inspections.put(i, inspections.get(i)!! + 1)
            var newWorry = m.operation(item)
            var reducedWorry = newWorry % commonWorryDivider
            if (reducedWorry % m.test.first == 0L) {
                monkeys[m.test.second].items.add(reducedWorry)
            } else {
                monkeys[m.test.third].items.add(reducedWorry)
            }
        }
        m.items.clear()
    }
}
inspections.values.sortedDescending().let {
    it[0].toLong() * it[1].toLong()
}
