import java.io.File
import java.util.SortedSet
import java.util.regex.Pattern
import kotlin.math.pow

fun codeToInt(code: CharSequence): Int = code.reversed().map { it -> if (it == 'B' || it == 'R') 1 else 0 }.reduceIndexed { index, acc, i -> acc + i * 2f.pow(index).toInt() }
fun passToId(pass: CharSequence): Int = 8 * codeToInt(pass.subSequence(0,7)) + codeToInt(pass.subSequence(7,10))

var takenIds =  File("input").useLines { it.toList() }.map {it -> passToId(it) }
var max = takenIds.maxOrNull() ?: 0
var min = takenIds.minOrNull() ?: 0

println("Max seat ID: ${max}")
println("Free seat IDs: ${ min.rangeTo(max).filter { it -> !takenIds.contains(it) } }")
