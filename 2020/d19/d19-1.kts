import java.io.File

var lines = File("input").readText().split("\\n\\n".toRegex()).map {it -> it.split("\\n".toRegex())}
var rules: Map<Int, String> = lines[0].map { it -> it.split(": ")[0].toInt() to it.split(": ")[1]}.toMap()
var messages = lines[1]

fun substituteRules(rule: String): String {
    if (rule.contains('|')) {
        var parts = rule.split(" | ", limit = 2)
        return "((${substituteRules(parts[0])})|(${substituteRules(parts[1])}))"
    } else if(rule.contains(' ')) {
        var parts = rule.split(' ')
        var returnVal = "("
        parts.forEach { returnVal += substituteRules(it)}
        returnVal += ")"
        return returnVal
    } else if(rule.matches("\"[a-z]\"".toRegex())) {
        return "${rule[1]}"
    } else if(rule.matches("\\d+".toRegex())) {
        return substituteRules(rules[rule.toInt()]!!)
    } else {
        println("Unparseable rule: '$rule'")
        return ""
    }
}
println(substituteRules(rules[0]!!))
println("Part 1: ${messages.filter { it.matches("^${substituteRules(rules[0]!!)}$".toRegex()) }.count()}")