import java.io.File

val input: String = File("input").readText().trim()

// Part 1
println("Answer 1: ")
solve()

fun solve() {
    // Sum every position (starting from the back)
    var sum = mutableListOf<Int>()
    input.
        split("\n")
        .forEach {
            it.map {
                when(it) {
                    '=' -> -2
                    '-' -> -1
                    '0' -> 0
                    '1' -> 1
                    '2' -> 2
                    else -> throw Exception("Invalid digit")
                }
            }.reversed()
            .forEachIndexed { i, n ->
                if (sum.getOrNull(i) != null) { sum[i] = sum[i] + n } else { sum.add(n) }
            }
        }

    // Convert the sum of every position to the approriate character for that position
    var snafu = ""
    var queue: MutableList<Int> = mutableListOf()
    var extra = 0
    queue.addAll(sum)
    while (queue.isNotEmpty()) {
        var n = queue.removeFirst() + extra
        println("Converting $n")
        when (n % 5) {
            -4 -> { snafu = "${snafu}1"; extra = n / 5 - 1; println("$snafu -> $extra") }
            -3 -> { snafu = "${snafu}2"; extra = n / 5 - 1; println("$snafu -> $extra") }
            -2 -> { snafu = "${snafu}="; extra = n / 5; println("$snafu -> $extra") }
            -1 -> { snafu = "${snafu}-"; extra = n / 5; println("$snafu -> $extra") }
            0 -> { snafu = "${snafu}0"; extra = n / 5; println("$snafu -> $extra") }
            1 -> { snafu = "${snafu}1"; extra = n / 5; println("$snafu -> $extra") }
            2 -> { snafu = "${snafu}2"; extra = n / 5; println("$snafu -> $extra") }
            3 -> { snafu = "${snafu}="; extra = n / 5 + 1; println("$snafu -> $extra") }
            4 -> { snafu = "${snafu}-"; extra = n / 5 + 1; println("$snafu -> $extra") }
            else -> throw Exception("Help")
        }
    }

    // Print (reversed!)
    println(snafu.reversed())
}
