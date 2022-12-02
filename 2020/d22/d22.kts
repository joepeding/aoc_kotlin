import java.io.File

var decks = File("testinput").readText().split("\\n\\n".toRegex()).map { it -> it.split(":\n")[1] }.map { it -> it.split("\n").map { it.toInt() }.toMutableList() }
fun combat(deck0: MutableList<Int>, deck1: MutableList<Int>): Int {
    while (deck0.size > 0 && deck1.size > 0) {
        println(listOf(deck0, deck1))
        var p0 = deck0.first()
        deck0.removeAt(0)
        var p1 = deck1.first()
        deck1.removeAt(0)

        if (p1 > p0) {
            deck1.add(p1)
            deck1.add(p0)
        } else {
            deck0.add(p0)
            deck0.add(p1)
        }
    }
    return calculateScore(if(deck0.isEmpty()) deck1 else deck0)
}

fun calculateScore(deck: List<Int>): Int = deck.reversed().reduceIndexed { index, acc, l -> acc + ((index + 1) * l) }

println("Part 1: ${combat(decks[0], decks[1])}")

var sameDecks = File("input").readText().split("\\n\\n".toRegex()).map { it -> it.split(":\n")[1] }.map { it -> it.split("\n").map { it.toInt() }.toMutableList() }
fun recursiveCombat(deck0: MutableList<Int>, deck1: MutableList<Int>, previousConfigurations: HashSet<Pair<List<Int>,List<Int>>>, subgameString: String = "1"): Pair<Int, List<Int>> {
    var subgameCounter = 0
    while (deck0.size > 0 && deck1.size > 0) {
        // Prevent infinite games
        var currentConfiguration = Pair(deck0, deck1)
        if (previousConfigurations.contains(currentConfiguration)) {
            return Pair(0, deck0)
        } else {
            previousConfigurations.add(currentConfiguration)
        }

        // Deal cards
        var p0 = deck0.first()
        deck0.removeAt(0)
        var p1 = deck1.first()
        deck1.removeAt(0)
        println("$subgameString: $p0 (${currentConfiguration.first}) vs $p1 (${currentConfiguration.second}")
        if (deck0.size >= p0 && deck1.size >= p1) {
            //Recursive combat
            var recursiveCombatResult = recursiveCombat(deck0.take(p0).toMutableList(), deck1.take(p1).toMutableList(), hashSetOf<Pair<List<Int>,List<Int>>>(), "$subgameString.${subgameCounter++}")
            if(recursiveCombatResult.first == 0) {
                deck0.add(p0)
                deck0.add(p1)
            } else {
                deck1.add(p1)
                deck1.add(p0)
            }
        } else {
            // Determine outcome according to normal rules
            if (p1 > p0) {
                deck1.add(p1)
                deck1.add(p0)
            } else {
                deck0.add(p0)
                deck0.add(p1)
            }
        }
    }
    return if(deck0.isEmpty()) Pair(1, deck1) else Pair(0, deck0)
}
println("Part 2: ${calculateScore(recursiveCombat(sameDecks[0], sameDecks[1], hashSetOf()).second)}")