/**
 *  This code is not pretty.
 *  - There's too much repeated logic that should be abstracted
 *  - I should have gone with data classes instead of maps for the collections of minerals and robots
 *      - There's too many not-null assertion operators (`!!`) for things that I know won't be null.
 *      - There's too many magic strings 
 *
 */


import java.io.File
import kotlin.math.ceil

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
val bluePrints = input
    .split("\n")
    .map { line ->
        line.split("costs ", " ore", " and ", " clay", " obsidian")
        .map { try { it.toInt() } catch (e: Exception) { null } }
        .filter { it != null }
    }.map { RobotFactory(it[0]!!, it[1]!!, it[2]!!, it[3]!!, it[4]!!, it[5]!!)}


fun newStock(oldStock: MutableMap<String, Int>, robots: Map<String, Int>, turns: Int = 1): MutableMap<String, Int> {
    val newStock: MutableMap<String, Int> = mutableMapOf()
    newStock.put("ore", (turns * robots.get("ore")!!) + (oldStock.get("ore")!!))
    newStock.put("clay", (turns * robots.get("clay")!!) + (oldStock.get("clay")!!))
    newStock.put("obsidian", (turns * robots.get("obsidian")!!) + (oldStock.get("obsidian")!!))
    newStock.put("geode", (turns * robots.get("geode")!!) + (oldStock.get("geode")!!))
    return newStock
}

data class State(
    var rf: RobotFactory,
    var rs: Map<String, Int> = mapOf("ore" to 1, "clay" to 0, "obsidian" to 0, "geode" to 0),
    var stock: MutableMap<String, Int> = mutableMapOf("ore" to 0, "clay" to 0, "obsidian" to 0, "geode" to 0),
    var time: Int = 1
)

val bestResult = DeepRecursiveFunction<State, Int> { state: State ->
    // Unpack state
    var rf = state.rf
    var rs = state.rs
    var stock = state.stock
    var time = state.time

    var newStock = newStock(stock, rs)
    if (time == endTime) {
        return@DeepRecursiveFunction newStock["geode"] ?: 0
    }

    var options: MutableList<Int> = mutableListOf()
    // Can we build a geode robot?
    if (stock.get("ore")!! >= rf.geodeRobotOreCost && stock.get("obsidian")!! >= rf.geodeRobotObsidianCost) {
        options.add(callRecursive(State(
            rf,
            rs.toMutableMap().also { it.put("geode", it.get("geode")!! + 1) },
            newStock(stock, rs).also {
                it.put("ore", it.get("ore")!! - rf.geodeRobotOreCost)
                it.put("obsidian", it.get("obsidian")!! - rf.geodeRobotObsidianCost)
            },
            time + 1
        )))
    } else if (rs.get("obsidian")!! > 0) { // Can we wait for a geode robot?
        var oreForGeode = (rf.geodeRobotOreCost - stock.get("ore")!!).toDouble()
        var obsidianForGeode = (rf.geodeRobotObsidianCost - stock.get("obsidian")!!).toDouble()
        var turnsForGeode = listOf<Int>(
            ceil((oreForGeode / rs.get("ore")!!.toDouble())).toInt(),
            ceil((obsidianForGeode / rs.get("obsidian")!!.toDouble())).toInt()
        ).max() + 1
        if (time + turnsForGeode < endTime) {
            options.add(callRecursive(State(
                rf,
                rs.toMutableMap().also { it.put("geode", it.get("geode")!! + 1) },
                newStock(stock, rs, turnsForGeode).also {
                    it.put("ore", it.get("ore")!! - rf.geodeRobotOreCost)
                    it.put("obsidian", it.get("obsidian")!! - rf.geodeRobotObsidianCost)
                },
                time + turnsForGeode
            )))
        }
    }

    // Can we build an obsidian robot
    if (stock.get("ore")!! >= rf.obsidianRobotOreCost && stock.get("clay")!! >= rf.obsidianRobotClayCost && rs.get("obsidian")!! < rf.geodeRobotObsidianCost) {
         options.add(callRecursive(State(
            rf,
            rs.toMutableMap().also{ it.put("obsidian", it.get("obsidian")!! + 1) },
            newStock(stock, rs).also {
                it.put("ore", it.get("ore")!! - rf.obsidianRobotOreCost)
                it.put("clay", it.get("clay")!! - rf.obsidianRobotClayCost)
            },
            time + 1
        )))
    } else if (rs.get("clay")!! > 0 && rs.get("obsidian")!! < rf.geodeRobotObsidianCost) { // Can we wait for an obsidian robot?
        var oreForObsidian = (rf.obsidianRobotOreCost - stock.get("ore")!!).toDouble()
        var clayForObsidian = (rf.obsidianRobotClayCost - stock.get("clay")!!).toDouble()
        var turnsForObsidian = listOf(
            ceil((oreForObsidian / rs.get("ore")!!.toDouble())).toInt(),
            ceil((clayForObsidian / rs.get("clay")!!.toDouble())).toInt()
        ).max() + 1
        if (time + turnsForObsidian < endTime) {
            options.add(callRecursive(State(
                rf,
                rs.toMutableMap().also { it.put("obsidian", it.get("obsidian")!! + 1) },
                newStock(stock, rs, turnsForObsidian).also {
                    it.put("ore", it.get("ore")!! - rf.obsidianRobotOreCost)
                    it.put("clay", it.get("clay")!! - rf.obsidianRobotClayCost)
                },
                time + turnsForObsidian
            )))
        }
    }

    // Can we build a clay bot
    if (stock.get("ore")!! >= rf.clayRobotOreCost && rs.get("clay")!! < rf.obsidianRobotClayCost) {
        options.add(callRecursive(State(
            rf,
            rs.toMutableMap().also { it.put("clay", it.get("clay")!! + 1) },
            newStock(stock, rs).also {
                it.put("ore", it.get("ore")!! - rf.clayRobotOreCost)
            },
            time + 1
        )))
    } else if (rs.get("clay")!! < rf.obsidianRobotClayCost) { // Can we wait for an clay robot?
        var oreForClay = (rf.clayRobotOreCost - stock.get("ore")!!).toDouble()
        var turnsForClay = ceil((oreForClay / rs.get("ore")!!.toDouble())).toInt() + 1
        if (time + turnsForClay < endTime) {
            options.add(callRecursive(State(
                rf,
                rs.toMutableMap().also { it.put("clay", it.get("clay")!! + 1) },
                newStock(stock, rs, turnsForClay).also {
                    it.put("ore", it.get("ore")!! - rf.clayRobotOreCost)
                },
                time + turnsForClay
            )))
        }
    }

    // Can we build an ore bot
    if (stock.get("ore")!! >= rf.oreRobotOreCost && rs.get("ore")!! < listOf(rf.clayRobotOreCost, rf.obsidianRobotOreCost, rf.geodeRobotOreCost).max()) {
        options.add(callRecursive(State(
            rf,
            rs.toMutableMap().also { it.put("ore", it.get("ore")!! + 1) },
            newStock(stock, rs).also {
                it.put("ore", it.get("ore")!! - rf.oreRobotOreCost)
            },
            time + 1
        )))
    } else if (rs.get("ore")!! < listOf(rf.clayRobotOreCost, rf.obsidianRobotOreCost, rf.geodeRobotOreCost).max()) { // Can we wait for an ore bot?
        var oreForOre = (rf.oreRobotOreCost - stock.get("ore")!!).toDouble()
        var turnsForOre = ceil((oreForOre / rs.get("ore")!!.toDouble())).toInt() + 1
        if (time + turnsForOre < endTime) {
            options.add(callRecursive(State(
                rf,
                rs.toMutableMap().also { it.put("ore", it.get("ore")!! + 1) },
                newStock(stock, rs, turnsForOre).also {
                    it.put("ore", it.get("ore")!! - rf.oreRobotOreCost)
                },
                time + turnsForOre
            )))
        }
    }

    if (options.isEmpty()) {
        // Wait a round
        return@DeepRecursiveFunction callRecursive(State(rf, rs, newStock,time + 1))
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
total = 1
endTime = 32
listOf(bluePrints[0], bluePrints[1], bluePrints[2]).forEachIndexed { i, bluePrint ->
    var score = bestResult(State(bluePrint))
    println("${i+1}: $score")
    total = total * score
}
println("Answer 2: $total")
