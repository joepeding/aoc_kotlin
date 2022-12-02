import java.io.File
import java.util.regex.Pattern

val rules: Map<String, Map<String, Int>> = File("input").useLines { it.toList() }.map { rule -> rule.split(" bags contain ") }.map { it[0] to it[1].split("\\sbag[s]?[,\\.]\\s?".toRegex()).filter { inner -> !inner.isEmpty() && inner != "no other"}.map { inner -> inner.subSequence(2, inner.lastIndex+1).toString() to Character.getNumericValue(inner[0]) }.toMap() }.toMap()

fun getOuterColors(innerColor: String): MutableSet<String> {
    var outerColors: MutableSet<String> = mutableSetOf()
    rules.forEach { k, v ->
        if (v.contains(innerColor)) {
            outerColors.add(k)
        }
    }
    return outerColors
}

fun findAllOuterColors(innerColors: MutableSet<String>): MutableSet<String> {
    var outerColors: MutableSet<String> = innerColors.map { it -> getOuterColors(it) }.flatten().toMutableSet()
    outerColors.addAll(innerColors)
    if (outerColors.equals(innerColors)) {
        return outerColors
    } else {
        return findAllOuterColors(outerColors)
    }
}

fun countBags(outerColor: String): Int =  if(!rules.containsKey(outerColor)) 1 else 1 + rules[outerColor]!!.map { (color, number) -> number * countBags(color) }.sum()

println("Number of bags that can contain 'shiny gold': ${findAllOuterColors(mutableSetOf("shiny gold")).size - 1}")
println("Number of bags inside 'shiny gold': ${countBags("shiny gold") - 1}")
