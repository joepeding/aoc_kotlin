import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

// Set up initial state
var input = "389125467".toList().map { it -> it.toString().toInt()}
var cups: MutableMap<Int, Pair<Int, Int>> = mutableMapOf()
input.forEachIndexed { i, c -> cups.put(c, Pair(if(i==0) input[8] else input[i-1], if(i==8) input[0] else input[i+1])) }
println(cups)

fun previous(cups: MutableMap<Int,Pair<Int,Int>>, current: Int): Int = cups[current]!!.first
fun next(cups: MutableMap<Int,Pair<Int,Int>>, current: Int): Int = cups[current]!!.second
fun target(current: Int, held: List<Int>): Int = if (current - 1 == 0) target(10, held) else if(held.contains(current - 1)) target(current - 1, held) else current - 1

println("Starting")
var current = input[0]
for (m in 1..10) {
    println("Move $m  Pre: $cups - Current: $current")
    print("--- ")
    var cup = 1
    (1..9).forEach {
        cup = next(cups, cup)
        print(cup)
    }
    println()

    // Take out 3 cups
    var plus1 = next(cups, current)
    var plus2 = next(cups, plus1)
    var plus3 = next(cups, plus2)
    var held = listOf(plus1, plus2, plus3)
    var oldAfter = next(cups, plus3)
    cups.put(current, Pair(previous(cups, current), oldAfter))
    cups.put(oldAfter, Pair(current, next(cups, oldAfter)))
    println("--- Held: $held")
    println("--- Left: $cups")

    // Insert 3 cups
    var targetBefore = target(current, held)
    println("--- Target: $targetBefore")
    var targetAfter = next(cups, targetBefore)
    cups.put(targetBefore, Pair(previous(cups, targetBefore), plus1))
    cups.put(plus1, Pair(targetBefore, plus2))
    cups.put(plus3, Pair(plus2, targetAfter))
    cups.put(targetAfter, Pair(plus3, next(cups, targetAfter)))

    // Move current
    current = next(cups, current)
    println("Move $m Post: $cups - Current: $current")
}

println()
print("Part 1: ")
var cup = 1
(1..8).forEach {
    cup = next(cups, cup)
    print(cup)
}