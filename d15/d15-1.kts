import java.io.File
var turns: MutableList<Int> = File("input").readLines().map { it -> it.split(",") }.flatten().map {it -> it.toInt()}.toMutableList()

while (turns.size < 2020) {
    turns.add(turns.subList(0, turns.lastIndex).reversed().indexOf(turns.last()) + 1)
}

turns.forEach {println(it)}