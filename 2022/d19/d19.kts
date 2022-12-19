import java.io.File

val input: String = File("input").readText().trim()

data class RobotFactory(
    val oreRobotOreCost: Int,
    val clayRobotOreCost: Int,
    val obsidianRobotOreCost: Int,
    val obsidianRobotClayCost: Int,
    val geodeRobotOreCost: Int,
    val geodeRobotObsidianCost: Int,
)

// Part 1
//print("Answer 1: ")
val bluePrints = input
    .split("\n")
    .map { line ->
        line.split("costs ", " ore", " and ", " clay", " obsidian")
        .map { try { it.toInt() } catch (e: Exception) { null } }
        .filter { it != null }
    }.map { RobotFactory(it[0]!!, it[1]!!, it[2]!!, it[3]!!, it[4]!!, it[5]!!)}


fun newStock(oldStock: MutableMap<String, Int>, robots: Map<String, Int>): MutableMap<String, Int> {
    val newStock: MutableMap<String, Int> = mutableMapOf()
    newStock.put("ore", (robots.get("ore") ?: 0) + (oldStock.get("ore") ?: 0))
    newStock.put("clay", (robots.get("clay") ?: 0) + (oldStock.get("clay") ?: 0))
    newStock.put("obsidian", (robots.get("obsidian") ?: 0) + (oldStock.get("obsidian") ?: 0))
    newStock.put("geode", (robots.get("geode") ?: 0) + (oldStock.get("geode") ?: 0))
    return newStock
}

data class State(
    var rf: RobotFactory,
    var rs: Map<String, Int> = mapOf("ore" to 1, "clay" to 0, "obsidian" to 0, "geode" to 0),
    var stock: MutableMap<String, Int> = mutableMapOf("ore" to 0, "clay" to 0, "obsidian" to 0, "geode" to 0),
    var forbiddenBots: List<String> = mutableListOf(),
    var time: Int = 1
)

val bestResult = DeepRecursiveFunction<State, Int> { state: State ->
    // Unpack state
    var rf = state.rf
    var rs = state.rs
    var stock = state.stock
    var forbiddenBots = state.forbiddenBots
    var time = state.time
    var shortage: MutableMap<String, Int> = mutableMapOf()

    var newStock = newStock(stock, rs)
    if (time == endTime) {
        return@DeepRecursiveFunction newStock["geode"] ?: 0
    }

    var options: MutableList<Int> = mutableListOf()

    // Can we build a geode robot?
    if (stock.get("ore")!! >= rf.geodeRobotOreCost && stock.get("obsidian")!! >= rf.geodeRobotObsidianCost && !forbiddenBots.contains("geode")) {
        return@DeepRecursiveFunction callRecursive(State(
            rf,
            rs.toMutableMap().also{ it.put("geode", it.get("geode")!! + 1) },
            newStock(stock, rs).also {
                it.put("ore", it.get("ore")!! - rf.geodeRobotOreCost)
                it.put("obsidian", it.get("obsidian")!! - rf.geodeRobotObsidianCost)
            },
            mutableListOf(),
            time + 1
        ))
    }

    // Can we build an obsidian robot
    if (stock.get("ore")!! >= rf.obsidianRobotOreCost && stock.get("clay")!! >= rf.obsidianRobotClayCost && !forbiddenBots.contains("obsidian") && rs.get("obsidian")!! < rf.geodeRobotObsidianCost) {
         options.add(callRecursive(State(
            rf,
            rs.toMutableMap().also{ it.put("obsidian", it.get("obsidian")!! + 1) },
            newStock(stock, rs).also {
                it.put("ore", it.get("ore")!! - rf.obsidianRobotOreCost)
                it.put("clay", it.get("clay")!! - rf.obsidianRobotClayCost)
            },
             mutableListOf(),
            time + 1
        )))
        options.add(callRecursive(State(
            rf,
            rs,
            newStock(stock, rs),
            forbiddenBots.toMutableList().also { it.add("obsidian") },
            time + 1
        )))
    }

    // Can we build a clay bot
    if (stock.get("ore")!! >= rf.clayRobotOreCost && !forbiddenBots.contains("clay") && rs.get("clay")!! < rf.obsidianRobotClayCost) {
        options.add(callRecursive(State(
            rf,
            rs.toMutableMap().also { it.put("clay", it.get("clay")!! + 1) },
            newStock(stock, rs).also {
                it.put("ore", it.get("ore")!! - rf.clayRobotOreCost)
            },
            mutableListOf(),
            time + 1
        )))
        options.add(callRecursive(State(
            rf,
            rs,
            newStock(stock, rs),
            forbiddenBots.toMutableList().also { it.add("clay") },
            time + 1
        )))
    }

    // Can we build an ore bot
    if (stock.get("ore")!! >= rf.oreRobotOreCost && !forbiddenBots.contains("ore") && rs.get("ore")!! < listOf(rf.clayRobotOreCost, rf.obsidianRobotOreCost, rf.geodeRobotOreCost).max()) {
        options.add(callRecursive(State(
            rf,
            rs.toMutableMap().also { it.put("ore", it.get("ore")!! + 1) },
            newStock(stock, rs).also {
                it.put("ore", it.get("ore")!! - rf.oreRobotOreCost)
            },
            mutableListOf(),
            time + 1
        )))
        options.add(callRecursive(State(
            rf,
            rs,
            newStock(stock, rs),
            forbiddenBots.toMutableList().also { it.add("ore") },
            time + 1
        )))
    }

    if (options.isEmpty()) {
        // Wait a round
        return@DeepRecursiveFunction callRecursive(State(rf, rs, newStock, forbiddenBots,time + 1))
    } else {
        return@DeepRecursiveFunction options.max()
    }
}


// Part 1
var total = 0
var endTime = 24
bluePrints.forEachIndexed { i, bluePrint ->
    var index = i + 1
    var score = bestResult(State(bluePrint))
    println("$index: $score -> ${index * score}")
    total += index * score
}
println("Answer 1: $total")

// Part 2
print("Answer 2: ")
total = 1
endTime = 32
listOf(bluePrints[0], bluePrints[1], bluePrints[2]).forEachIndexed { i, bluePrint ->
    var score = bestResult(State(bluePrint))
    println("${i+1}: $score")
    total = total * score
}
println("Answer 1: $total")
