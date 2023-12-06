import java.io.File

data class Race(val duration: Long, val distance: Long,)
fun Race.buttonTimeWins(buttomTime: Long): Boolean = buttomTime * (this.duration - buttomTime) > this.distance

val input: String = File("input").readText()

print("Answer 1:")
val races = input
    .split("\n")
    .map { it.substringAfter(":").trim().split("\\s+".toRegex()) }
    .let { rs ->
        rs.first().mapIndexed { index, time -> Race(time.toLong(), rs[1][index].toLong())}
    }.map {
        r -> (0..r.duration).count { t -> r.buttonTimeWins(t) }
    }.fold(1) { a: Int, b: Int -> a * b }
    .let { println("Answer 1: $it") }


print("Answer 2: ")
input
    .split("\n")
    .map { it.substringAfter(":").trim().split("\\s+".toRegex()) }
    .let { Race(it.first().joinToString("").toLong(), it[1].joinToString("").toLong()) }
    .let { r -> (0..r.duration).count { t -> r.buttonTimeWins(t) } }
    .let { println(it) }

