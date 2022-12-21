import java.io.File

val input: String = File("input").readText().trim()
var monkeys: MutableMap<String, Monkey> = parseMonkeys()
fun parseMonkeys(): MutableMap<String, Monkey> = input
    .split("\n")
    .map {
        it.split(": ").let { (name, value) ->
            try {
                name to Monkey(value.trim().toLong(), { value.trim().toLong() } )
            } catch(e: Exception) {
                name to Monkey(null, value.split(" ").let { (a, op, b) ->
                    when(op) {
                        "+" -> { monkeys -> monkeys[a]!!.calcVal() + monkeys[b]!!.calcVal() }
                        "*" -> { monkeys -> monkeys[a]!!.calcVal() * monkeys[b]!!.calcVal() }
                        "-" -> { monkeys -> monkeys[a]!!.calcVal() - monkeys[b]!!.calcVal() }
                        "/" -> { monkeys -> monkeys[a]!!.calcVal() / monkeys[b]!!.calcVal() }
                        else -> throw Exception("Invalid operator")
                    }
                })
            }
        }
    }.toMap().toMutableMap()

// Part 1
print("Answer 1: ")
println(monkeys["root"]!!.calcVal())

// Part 2
print("Answer 2: ")
for (answer in (2262667199562L*2) until (2262667199562L*2  + 10L)) {
    monkeys = parseMonkeys()
    var root = input
        .split("\n")
        .map { it.split(": ") }
        .first { (name, op) -> name == "root" }
        .let { Monkey(
            null,
            it[1].split(" ").let { (a, op, b) ->
                { monkeys -> println("${monkeys[a]!!.calcVal()} == ${monkeys[b]!!.calcVal()}"); if (monkeys[a]!!.calcVal() == monkeys[b]!!.calcVal()) { 1L } else { 0L } }
            }
        )}
    monkeys.put("root", root)
    monkeys.get("humn")!!.value = answer
    if (monkeys["root"]!!.calcVal() == 1L) {
        println(answer)
        break
    }
}

data class Monkey(var value: Long?, var operation: (MutableMap<String, Monkey>) -> Long) {
    public fun calcVal(): Long {
        if (value == null) { value = operation(monkeys) }
        return value!!
    }
}
