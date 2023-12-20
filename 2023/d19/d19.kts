import java.io.File
import javax.xml.stream.events.EndDocument
import kotlin.Exception
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.streams.toList

data class Part(val x: Int, val m: Int, val a: Int, val s: Int)
data class RuleParts(val param: Char?, val operator: Char?, val value: Int?, val next: String)
data class Rule(val check: (Part) -> Boolean, val next: String)
data class Workflow(val name: String, val ruleParts: List<RuleParts>, val rules: List<Rule>)

val input: String = File("input").readText()
val workflows = input
    .substringBefore("\n\n")
    .split("\n")
    .filter { it.isNotBlank() }
    .map {
        val ruleParts = it.substringAfter('{').substringBefore('}')
            .split(',')
            .map { rule ->
                if (rule.contains(':').not()) {
                    RuleParts(null, null, null, rule)
                } else {
                    RuleParts(
                        rule.first(),
                        rule[1],
                        rule.substring(2).substringBefore(':').toInt(),
                        rule.substringAfter(':')
                    )
                }
            }
        Workflow(
            it.substringBefore('{'),
            ruleParts,
            ruleParts.toRules()
        )
     }
    .toMutableList()

fun List<RuleParts>.toRules() = map {
    if (it.value == null) { return@map Rule({true}, it.next) }
    val ruleFun = { part: D19.Part ->
        when(it.param) {
            'x' -> if (it.operator == '>') { part.x > it.value } else { part.x < it.value }
            'm' -> if (it.operator == '>') { part.m > it.value } else { part.m < it.value }
            'a' -> if (it.operator == '>') { part.a > it.value } else { part.a < it.value }
            's' -> if (it.operator == '>') { part.s > it.value } else { part.s < it.value }
            else -> throw Exception("Invalid param")
        }
    }
    Rule(ruleFun, it.next)
}
fun Workflow.nextFor(part: Part): String = rules.first { it.check(part) }.next
fun List<Workflow>.accepts(part: Part): Boolean {
    var nextWorkflow = this.first { it.name == "in" }
    while (listOf("A", "R").contains(nextWorkflow.nextFor(part)).not()) {
        nextWorkflow = first { it.name == nextWorkflow.nextFor(part) }
    }
    return nextWorkflow.nextFor(part) == "A"
}

val parts = input
    .substringAfter("\n\n")
    .split("\n")
    .filter { it.isNotBlank() }
    .map {
        Part(
            it.substringAfter("x=").substringBefore(",").toInt(),
            it.substringAfter("m=").substringBefore(",").toInt(),
            it.substringAfter("a=").substringBefore(",").toInt(),
            it.substringAfter("s=").substringBefore("}").toInt(),
        )
    }

parts
    .filter { workflows.accepts(it) }
//    .onEach { println(it) }
    .sumOf { it.x + it.m + it.a + it.s }
    .let { println("Answer 1: $it") }


var allAccepted = 0L
println("Answer 2: ${acceptedRanges("in", 1..4000, 1..4000, 1..4000, 1..4000)}")
println("Answer 2: $allAccepted") // Not sure where the error is that makes the method always return 0, but this out of scope var contain the correct value. Fixing is is not a priority anymore.

fun acceptedRanges(nextWorkflow: String, x: IntRange, m: IntRange, a: IntRange, s: IntRange): Long {
    if (nextWorkflow == "A") {
        val accepted = x.count().toLong() * m.count().toLong() * a.count().toLong() * s.count().toLong()
        println("Analyzing for $nextWorkflow with x:$x m:$m a:$a s:$s")
//        println("Accepted: $accepted")
        allAccepted += accepted
        return accepted
    }
    if (nextWorkflow == "R") {
        println("Analyzing for $nextWorkflow with x:$x m:$m a:$a s:$s")
        return 0L
    }
    var remainingX = x
    var remainingM = m
    var remainingA = a
    var remainingS = s
    return workflows
        .first { it.name == nextWorkflow }
        .ruleParts
        .map { rule ->
            if (rule.value == null || rule.operator == null || rule.param == null) {
                return acceptedRanges(rule.next, remainingX, remainingM, remainingA, remainingS)
            }
            when (rule.param) {
                'x' -> if (rule.operator == '>' && remainingX.last > rule.value) {
                    acceptedRanges(rule.next, (rule.value + 1)..remainingX.last, remainingM, remainingA, remainingS)
                        .also { remainingX = remainingX.first..rule.value}
                } else if (rule.operator == '<' && remainingX.first < rule.value) {
                    acceptedRanges(rule.next, remainingX.first..<rule.value, remainingM, remainingA, remainingS)
                        .also { remainingX = rule.value..remainingX.last }
                } else {
                    0L
                }

                'm' -> if (rule.operator == '>' && remainingM.last > rule.value) {
                    acceptedRanges(rule.next, remainingX, (rule.value + 1)..remainingM.last, remainingA, remainingS)
                        .also { remainingM = remainingM.first..rule.value }
                } else if (rule.operator == '<' && remainingM.first < rule.value) {
                    acceptedRanges(rule.next, remainingX, remainingM.first..<rule.value, remainingA, remainingS)
                        .also { remainingM = rule.value..remainingM.last}
                } else {
                    0L
                }

                'a' -> if (rule.operator == '>' && remainingA.last > rule.value) {
                    acceptedRanges(rule.next, remainingX, remainingM, (rule.value + 1)..remainingA.last, remainingS)
                        .also {remainingA = remainingA.first..rule.value }
                } else if (rule.operator == '<' && remainingA.first < rule.value) {
                    acceptedRanges(rule.next, remainingX, remainingM, remainingA.first..<rule.value, remainingS)
                        .also { remainingA = rule.value..remainingA.last }
                } else {
                    0L
                }

                's' -> if (rule.operator == '>' && remainingS.last > rule.value) {
                    acceptedRanges(rule.next, remainingX, remainingM, remainingA, (rule.value + 1)..remainingS.last)
                        .also {remainingS = remainingS.first..rule.value }
                } else if (rule.operator == '<' && remainingS.first < rule.value) {
                    acceptedRanges(rule.next, remainingX, remainingM, remainingA, remainingS.first..<rule.value)
                        .also { remainingS = rule.value..remainingS.last }
                } else {
                    0L
                }
                else -> throw Exception("Invalid param")
            }
        }.also { println(it) }
        .sum()
        .also {
            println("$nextWorkflow accepts $it out of x:$x m:$m a:$a s:$s")
        }
}