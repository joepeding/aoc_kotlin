import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

// Set up initial state
var input = "962713854".toList().map { it -> it.toString().toInt()}
var cups: MutableMap<Int, Pair<Int, Int>> = mutableMapOf()
input.forEachIndexed { i, c -> cups.put(c, Pair(if(i==0) 1000000 else input[i-1]!!, if(i==8) 10 else input[i+1]!!)) }
(10..1000000).forEach {
    cups.put(it, Pair(if (it == 10) input[8]!! else it - 1, if (it == 1000000) input[0] else it + 1))
}

fun previous(cups: MutableMap<Int,Pair<Int,Int>>, current: Int): Int = cups[current]!!.first
fun next(cups: MutableMap<Int,Pair<Int,Int>>, current: Int): Int = cups[current]!!.second
fun target(current: Int, held: List<Int>): Int = if (current - 1 == 0) target(1000001, held) else if(held.contains(current - 1)) target(current - 1, held) else current - 1

println("Starting")
var current = input[0]
for (m in 1..10000000) {
    if(m % 100000 == 0) { println("Move: $m") }

    // Take out 3 cups
    var plus1 = next(cups, current)
    var plus2 = next(cups, plus1)
    var plus3 = next(cups, plus2)
    var held = listOf(plus1, plus2, plus3)
    var oldAfter = next(cups, plus3)
    cups.put(current, Pair(previous(cups, current), oldAfter))
    cups.put(oldAfter, Pair(current, next(cups, oldAfter)))

    // Insert 3 cups
    var targetBefore = target(current, held)
    var targetAfter = next(cups, targetBefore)
    cups.put(targetBefore, Pair(previous(cups, targetBefore), plus1))
    cups.put(plus1, Pair(targetBefore, plus2))
    cups.put(plus3, Pair(plus2, targetAfter))
    cups.put(targetAfter, Pair(plus3, next(cups, targetAfter)))

    // Move current
    current = next(cups, current)
}

var onePlusOne = next(cups, 1)
var onePlusTwo = next(cups, onePlusOne)
println("Part 2: $onePlusOne * $onePlusTwo = ${onePlusOne.toLong() * onePlusTwo.toLong()}")
//println("Part 2: ${input.subList(input.indexOf(1), input.indexOf(1) + 4)}")
