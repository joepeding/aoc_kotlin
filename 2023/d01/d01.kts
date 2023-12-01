import java.io.File

val input: String = File("input").readText()
val calibrationstrings: List<Int> = input
    .split("\n")
    .filter { it.isNotBlank() }
    .map { string ->
        string
            .toCharArray()
            .filter { it.isDigit() }
            .let { "${it.first()}${it.last()}" }
            .toInt()
    }

print("Answer 1: ")
println(calibrationstrings.sum())

val correctedCalibrationstrings: List<Int> = input
    .split("\n")
    .filter { it.isNotBlank() }
    .map { it.justNumbers() }
    .map { string ->
        string
            .toCharArray()
            .filter { it.isDigit() }
            .let { "${it.first()}${it.last()}" }
            .toInt()
    }
print("Answer 2: ")
println(correctedCalibrationstrings.sum())

fun String.justNumbers(): String = when {
    this.isEmpty() -> ""
    this.first().isDigit() -> this.first().toString() + this.substring(1).justNumbers()
    this.startsWith("one") -> "1" + this.substring(1).justNumbers()
    this.startsWith("two") -> "2" + this.substring(1).justNumbers()
    this.startsWith("three") -> "3" + this.substring(1).justNumbers()
    this.startsWith("four") -> "4" + this.substring(1).justNumbers()
    this.startsWith("five") -> "5" + this.substring(1).justNumbers()
    this.startsWith("six") -> "6" + this.substring(1).justNumbers()
    this.startsWith("seven") -> "7" + this.substring(1).justNumbers()
    this.startsWith("eight") -> "8" + this.substring(1).justNumbers()
    this.startsWith("nine") -> "9" + this.substring(1).justNumbers()
    else -> this.substring(1).justNumbers()
}
