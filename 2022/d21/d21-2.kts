import java.io.File

val input: String = File("input").readText().trim()
var monkeys: MutableMap<String, Monkey> = parseMonkeys()
fun parseMonkeys() = input
    .split("\n")
    .map {
        it.split(": ").let { (name, value) ->
            try {
                name to Monkey(name, value.trim().toLong(), null, null, null)
            } catch(e: Exception) {
                name to value.split(" ").let { (a, op, b) -> Monkey(name, null, a, b, op) }
            }
        }
    }.toMap().toMutableMap()

// Part 1
print("Answer 1: ")
println(monkeys["root"]!!.calcVal())

// Part 2
print("Answer 2: ")
monkeys = parseMonkeys()
monkeys.get("root")!!.op = "="
println(monkeys.get("root")!!.calcVal())

data class Monkey(var name: String, var value: Long?, var a: String?, var b: String?, var op: String?) {
    public fun calcVal(): Long {
        if (a == null) { return value!! }
        when(op) {
            "+" -> value = monkeys[a!!]!!.calcVal() + monkeys[b!!]!!.calcVal()
            "*" -> value = monkeys[a!!]!!.calcVal() * monkeys[b!!]!!.calcVal()
            "-" -> value = monkeys[a!!]!!.calcVal() - monkeys[b!!]!!.calcVal()
            "/" -> value = monkeys[a!!]!!.calcVal() / monkeys[b!!]!!.calcVal()
            "=" -> value = if (monkeys[a!!]!!.containsHuman()) {
                monkeys[a!!]!!.calculateHumanValue(monkeys[b]!!.calcVal())
            } else {
                monkeys[b!!]!!.calculateHumanValue(monkeys[a]!!.calcVal())
            }
            else -> throw Exception("Invalid operator")
        }
        return value!!
    }

    public fun calculateHumanValue(target: Long): Long {
        if (name == "humn") { return target }
        return if (monkeys[a]!!.containsHuman()) {
            when(op) {
                "+" -> monkeys[a!!]!!.calculateHumanValue(target - monkeys[b!!]!!.calcVal())
                "*" -> monkeys[a!!]!!.calculateHumanValue(target / monkeys[b!!]!!.calcVal())
                "-" -> monkeys[a!!]!!.calculateHumanValue(target + monkeys[b!!]!!.calcVal())
                "/" -> monkeys[a!!]!!.calculateHumanValue(target * monkeys[b!!]!!.calcVal())
                else -> throw Exception("Invalid operator")
            }
        } else {
            when(op) {
                "+" -> monkeys[b!!]!!.calculateHumanValue(target - monkeys[a!!]!!.calcVal())
                "*" -> monkeys[b!!]!!.calculateHumanValue(target / monkeys[a!!]!!.calcVal())
                "-" -> monkeys[b!!]!!.calculateHumanValue(monkeys[a!!]!!.calcVal() - target)
                "/" -> monkeys[b!!]!!.calculateHumanValue(monkeys[a!!]!!.calcVal() / target)
                else -> throw Exception("Invalid operator")
            }
        }
    }

    public fun containsHuman(): Boolean {
        if (name == "humn") { return true }
        if (a != null && monkeys[a]!!.containsHuman()) { return true }
        if (b != null && monkeys[b]!!.containsHuman()) { return true }
        return false
    }
}
