import java.io.File

var lines = File("input").readText().split("\\n\\n".toRegex()).map {it -> it.split("\\n".toRegex())}
var rules: Map<Int, String> = lines[0].map { it -> it.split(": ")[0].toInt() to it.split(": ")[1]}.toMap()
var messages = lines[1]

fun substituteRules(rule: String): String {
    if (rule.contains('|')) {
        var parts = rule.split(" | ")
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
        if(rule.toInt() == 8) {
            return "((${substituteRules(rules[42]!!)})+)"
        } else if(rule.toInt() == 11) {
            return substitute11()
        }
        return substituteRules(rules[rule.toInt()]!!)
    } else {
        println("Unparseable rule: '$rule'")
        return ""
    }
}

fun substitute11(): String {
    var r42 = substituteRules(rules[42]!!)
    var r31 = substituteRules(rules[31]!!)
    // As recursion is not supported in JVM regex, we'll go with a poor man's equivalent.
    var dumbRecursiveRegex = "($r42$r31)"
    for(i in 2..10) {
        dumbRecursiveRegex += "|(($r42){$i}($r31){$i})"
    }
    return "($dumbRecursiveRegex)"
}

println(substituteRules(rules[0]!!))
println("Part 2: ${messages.filter { it.matches("^${substituteRules(rules[0]!!)}$".toRegex()) }.count()}")