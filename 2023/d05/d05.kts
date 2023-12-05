import java.io.File
import java.lang.Exception
import kotlin.math.exp
import kotlin.math.pow
import kotlin.streams.toList
import kotlin.time.Duration.Companion.seconds

data class ConversionRange(val dest: Long, val source: Long, val range: Long)

val input: String = File("input").readText()
val parts = input
    .split("\n\n")

val seeds = parts.first().substringAfter(": ").trim().split("\\s+".toRegex()).map { it.toLong() }
@OptIn(kotlin.ExperimentalStdlibApi::class)
val maps = parts.slice(1 until parts.size).map {
    it
        .substringAfter(":")
        .split("\n")
        .filter { line -> line.isNotEmpty() }
        .map { line -> line
            .split("\\s+".toRegex())
            .map { n -> n.trim().toLong() }
            .let { ns -> ConversionRange(ns[0], ns[1], ns[2]) }
        }
}

val result = seeds
    .map { it.toNextNumber(0) }
//    .filter { it.isNotBlank() }
//    .map { it.substringAfter(": ").trim() }

println("Answer 1: ${result.min()}")




var flattenedMap = maps[0]
(1 until maps.size).forEach { index ->
    flattenedMap = flattenedMap.map { cr ->
        cr.convert(maps[index]).also {
            println("Mapped $cr to $it")
            if (it.any { c -> c.range < 1 }) { throw Exception() }
            if (it.sumOf { c -> c.range } != cr.range) { throw Exception() }
        }.also { println("----") }
    }.flatten()
}

println("Answer 2: ")
val result2 = seeds
    .windowed(2, 2, false)
    .map { ConversionRange(it.first(), it.first(), it.last()) }
    .map { it.convert(flattenedMap) }
    .flatten()
println(result2.minOf { it.dest })
println(result2)

fun Long.toNextNumber(mapId: Int): Long {
    val dest = maps[mapId]
        .firstOrNull { it.source <= this && this < it.source + it.range }
        ?.let { it.dest - it.source + this } ?: this
    return if (maps.getOrNull(mapId + 1) != null) {
        dest.toNextNumber(mapId + 1)
    } else {
        dest
    }
}

fun Long.toNextNumber2(): Long = flattenedMap
    .firstOrNull { it.source <= this && this < it.source + it.range }
    ?.let { it.dest - it.source + this } ?: this

fun ConversionRange.convert(nextMap: List<ConversionRange>?): List<ConversionRange> {
    if(this.range < 0L) { throw Exception() }
    if(this.range == 0L) { return listOf() }
    if(nextMap == null) { return listOf(this) }
    val mappedRange = nextMap.firstOrNull { it.source <= this.dest && this.dest < it.source + it.range }
    println("Mapping $this on $mappedRange of $nextMap")
    if (mappedRange == null && nextMap.minOf { it.source } >= this.dest + this.range) {
        println("Whole range is before all possible destinations")
        return listOf(this)
    } else if (mappedRange == null && nextMap.maxOf { it.source + it.range } < this.dest + this.range) {
        println("Whole range is after all possible destinations")
        return listOf(this)
    } else if (mappedRange == null && nextMap.minOf { it.source } > this.dest) {
        println("Part of range is before all possible destinations")
//        val newRange = nextMap.minOf { it.source } - this.dest
        val lastNumberThatFits = minOf(nextMap.minOf { it.source } - 1, this.dest + this.range)
        val newRange = lastNumberThatFits - this.dest + 1
        return mutableListOf(
            ConversionRange(this.dest, this.source, newRange).also { println("Mapped to $it") }
        ).also {
            it.addAll(
                ConversionRange(this.dest + newRange, this.source + newRange, this.range - newRange).convert(nextMap)
            )
        }
    } else if (mappedRange == null && this.isBetweenRanges(nextMap)) {
        println("Part of range is between ranges")
        val lastNumberThatFits = nextMap
            .filter { it.source > this.dest }
            .minBy { it.source }
            .source
            .minus(1)
        println("lastN: $lastNumberThatFits")
        val newRange = minOf(lastNumberThatFits - this.dest + 1, this.range)
        println("newR: $newRange")
        return mutableListOf(
            ConversionRange(this.dest, this.source, newRange).also { println("Mapped to $it") }
        ).also {
            it.addAll(
                ConversionRange(this.dest + newRange, this.source + newRange, this.range - newRange).convert(nextMap)
            )
        }
    } else if (mappedRange == null) {
        throw Exception("bbb") // Does not happen
    } else if (mappedRange.source + mappedRange.range >= this.dest + this.range) {
        println("Whole range fits inside mapped destination")
        val offset = this.dest - mappedRange.source
        return listOf(ConversionRange(mappedRange.dest + offset, this.source, this.range))
    } else {
        println("Part of range fits inside mapped destination")
//        println("Here")
        val lastNumberThatFits = minOf(this.dest + this.range - 1, mappedRange.source + mappedRange.range - 1)
        println("lastN: $lastNumberThatFits")
        val newRange = lastNumberThatFits - maxOf(mappedRange.source, this.dest) + 1
//        println("Newrange: $newRange")
        val offset = this.dest - mappedRange.source
        return mutableListOf(
            ConversionRange(mappedRange.dest + offset, this.source, newRange).also { println("Mapped to $it") }
        ).also {
            it.addAll(
                ConversionRange(this.dest + newRange, this.source + newRange, this.range - newRange).convert(nextMap)
            )
        }
    }
}

fun D05.ConversionRange.isBetweenRanges(nextMap: List<D05.ConversionRange>): Boolean = nextMap
        .windowed(2, 1, false)
        .map {
            it.first().source + it.first().range - 1 < this.dest &&
            it.last().source >= this.dest + this.range
        }.any()
