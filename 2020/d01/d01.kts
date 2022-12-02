import java.io.File
import java.util.SortedSet

val lines: List<String> = File("input").useLines { it.toList() }
val numbers: List<Int> = lines.map { it -> it.toInt() }

fun getTwoToSum(numbers: List<Int>, sum: Int): Pair<Int, Int>? {
    numbers.forEach {
        var first = it
        numbers.filter {it != first}.sorted().forEach {
            if (first + it == sum) {
                return Pair(first, it)
            }
        }
    }
    return null
}

val result = getTwoToSum(numbers, 2020)
if (result != null) {
    println("${result.first} * ${result.second} = ${result.first * result.second}")
}


fun getNToSum(numbers: List<Int>, n: Int, sum: Int): MutableSet<Int>? {
    numbers.forEach {
        var first = it
        var otherNumbers = numbers.filter { it != first }

        if (n-1 == 2) {
            var rest = getTwoToSum(otherNumbers, sum - first)
            if (rest != null) {
                return mutableSetOf(rest.first, rest.second, first)
            }
        } else {
            var rest = getNToSum(otherNumbers, n-1, sum - first)
            if (rest != null) {
                rest.add(first)
                return rest
            }
        }
    }
    return null
}

val result2 = getNToSum(numbers, 3, 2020)
if (result2 != null) {
    println("${result2}: ${result2.reduce {acc, i -> acc * i}}")
}