import java.io.File
import javax.xml.stream.events.EndDocument
import kotlin.Exception
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.streams.toList

data class Modules(
    val type: ModuleType,
    val targets: List<String>,
    val inputs: MutableMap<String, Boolean>,
    var flipflopState: Boolean = false,
    var firstTimeHigh: Int? = null
)
enum class ModuleType { BROADCASTER, FLIPFLOP, CONJUNCTION, BUTTON}
data class Signal(val source: String, val target: String, val high: Boolean)
fun String.toModules() =
    split("\n")
        .filter { it.isNotBlank() }
        .map {
            if (it.startsWith("broadcaster")) {
                "broadcaster" to
                        Modules(
                            ModuleType.BROADCASTER,
                            it.substringAfter("-> ").split(", "),
                            mutableMapOf()
                        )
            } else {
                it.substring(1).substringBefore(" ->") to
                        Modules(
                            if (it.startsWith('%')) ModuleType.FLIPFLOP else ModuleType.CONJUNCTION,
                            it.substringAfter("-> ").split(", "),
                            mutableMapOf()
                        )
            }
        }
        .toMap()
        .toMutableMap()
fun MutableMap<String, Modules>.setInputs() = forEach { (name, mod) ->
    this
        .filter { it.value.targets.contains(name) }
        .forEach { mod.inputs[it.key] = false }
}
fun Pair<Long, Long>.gcd(): Long = if (second == 0L) first else Pair(second, first % second).gcd()
fun Pair<Long, Long>.lcm(): Long = abs(first) * (abs(second) / gcd())

val input: String = File("input").readText()
val modules = input.toModules()
modules.setInputs()
var highSignals = 0L
var lowSignals = 0L
var buttonPress = 1
repeat(10000) {buttonPress ->
    val signalQueue = mutableListOf(Signal("button", "broadcaster", false))
    while (signalQueue.isNotEmpty()) {
        val s = signalQueue.removeFirst()
        if (s.high) { highSignals++ } else { lowSignals++ }
        val t = modules[s.target] ?: continue
        when (t.type) {
            ModuleType.BUTTON -> {}
            ModuleType.BROADCASTER -> t.targets.forEach { signalQueue.add((Signal(s.target, it, s.high))) }
            ModuleType.FLIPFLOP -> if (!s.high) {
                t.flipflopState = !t.flipflopState.also { if (t.firstTimeHigh == null) { t.firstTimeHigh = buttonPress } }
                t.targets.forEach { signalQueue.add(Signal(s.target, it, t.flipflopState)) }
            }
            ModuleType.CONJUNCTION -> {
                t.inputs[s.source] = s.high
                if (t.firstTimeHigh == null && !t.inputs.all { it.value }) { t.firstTimeHigh = buttonPress }
                t.targets.forEach { signalQueue.add(Signal(s.target, it, !t.inputs.values.all { it })) }
            }
        }
    }
    if (buttonPress == 999) { println("Answer 1: $highSignals HIGH * $lowSignals LOW = ${highSignals * lowSignals}") }
}

modules
    .filter { it.key in modules.values.first { it.targets.contains("rx") }.inputs }
    .map { it.value.firstTimeHigh!! + 1 }
    .map { it.toLong() }
    .fold(1L) { a, b -> Pair(a, b).lcm() }
    .let { println("Answer 2: $it") }