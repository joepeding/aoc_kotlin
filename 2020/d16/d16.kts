import java.io.File

// Parse input
var lines: List<String> = File("input").readText().split("\\s\\s".toRegex())
var rules: Map<String, List<IntProgression>> = lines[0]
        .split("\\n".toRegex())
        .map { line -> line.split(": ") }
        .map { it -> it[0] to it[1].split(" or ")
            .map { range -> (range.substring(0, range.indexOf('-')).toInt())..(range.substring(range.indexOf('-')+1).toInt()) }
        }.toMap()
var myTicket: List<Int> = lines[1].split("\\n".toRegex())[1].split(",").map { field -> field.toInt() }
var nearbyTickets: List<List<Int>> = lines[2]
        .split("\\n".toRegex())
        .filter {it != "nearby tickets:" && !it.isEmpty()}
        .map { ticket -> ticket.split(",").map { field -> field.toInt() } }

// Part 1
var validNumbers = (1..1000).filter { rules.any { (_, ranges) -> ranges.any { range -> range.contains(it) } } }
println("Part 1: ${nearbyTickets.map { it -> it.filter { !validNumbers.contains(it) }.sum() }.sum()}")

// Part 2
var validTickets = nearbyTickets.filter { ticket -> ticket.all { validNumbers.contains(it) } }
var fieldValues: MutableMap<Int, List<Int>> = mutableMapOf()
for (i in 0..(validTickets[0].size - 1)) {
    fieldValues.put(i, validTickets.map { it -> it[i] })
}

var validRulesPerField = fieldValues.mapValues { (_,numbers) -> rules.filter { (_, ranges) -> ranges.flatten().containsAll(numbers) }.keys.toMutableSet() }.toMutableMap()
var definiteMapping: MutableMap<Int, String> = mutableMapOf()
while (validRulesPerField.size > 0) {
    validRulesPerField.filter { (_,v) -> v.size == 1 }.forEach{(k,v) -> definiteMapping.put(k, v.first()) }
    definiteMapping.keys.forEach { validRulesPerField.remove(it) }
    definiteMapping.values.forEach { validRulesPerField.values.forEach { remainingRules -> remainingRules.remove(it) } }
}
print("Part 2: ")
println(myTicket.filterIndexed { i,_ -> definiteMapping.filter { (_,v) -> v.contains("departure") }.keys.contains(i) }.map { it -> it.toLong() }.reduce { acc, n -> acc * n })