import java.io.File

var foods: List<Pair<List<String>,List<String>>> = File("input").readLines().map { it -> it.split(" (contains ").let { (ingredients, allergens) -> ingredients.split(" ") to allergens.substring(0, allergens.lastIndex).split(", ")}}

var allergenOccurrences: MutableMap<String, MutableList<List<String>>> = mutableMapOf()
for ((ingredients, allergens) in foods) {
    for (a in allergens) {
        if(!allergenOccurrences.containsKey(a)) {
            allergenOccurrences.put(a, mutableListOf<List<String>>())
        }
        allergenOccurrences[a]!!.add(ingredients)
    }
}

var allergenOptions: MutableMap<String, MutableList<String>> = allergenOccurrences.mapValues { (_, iLs) -> iLs.flatten().toSet().filter { i -> iLs.all {iL -> iL.contains(i) } }.toMutableList() }.toMutableMap()

var ingredientToAllergen: MutableMap<String, String> = mutableMapOf()
while (allergenOptions.size > 0) {
    var newResult = allergenOptions.filter { it.value.size == 1 }.entries.first()
    ingredientToAllergen.put(newResult.value.first(), newResult.key)
    allergenOptions.remove(newResult.key)
    allergenOptions.values.forEach { it.remove(newResult.value.first()) }
}

println("Part 1: ${foods.map { it.first }.flatten().filter { !ingredientToAllergen.keys.contains(it) }.count()}")
println("Part 2: ${ingredientToAllergen.entries.sortedBy { it.value }.map { it.key }.reduce { acc, s -> "$acc,$s" }}")

allergenOccurrences.forEach { println(it) }
println()
allergenOptions.forEach { println(it) }
println()
ingredientToAllergen.forEach {println(it)}
