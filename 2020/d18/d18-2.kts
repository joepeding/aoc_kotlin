import java.io.File

fun process(line: String): Long {
    var firstParenthesis = line.indexOfFirst { c -> c == '(' }
    if (firstParenthesis > -1) {
        var closeParenthesis = findClosingParenthesisIndex(line, firstParenthesis)
        if (closeParenthesis < line.lastIndex) {
            return process("${line.substring(0, firstParenthesis)}${process(line.substring(firstParenthesis + 1, closeParenthesis))}${line.substring(closeParenthesis + 1)}")
        } else {
            return process("${line.substring(0, firstParenthesis)}${process(line.substring(firstParenthesis + 1, closeParenthesis))}")
        }
    }

    // Line contains no more parentheses
    // Split line on multiplication operators
    return line.split(" * ").map { it -> addNumbers(it) }.reduce { acc, l -> acc * l }
}

fun findClosingParenthesisIndex(line: String, openIndex: Int): Int {
    var depth = 1
    var i = openIndex + 1
    while (i <= line.lastIndex) {
        if(line[i] == '(') {
            depth++
        } else if (line[i] == ')') {
            depth--
        }
        if (depth == 0) {
            break
        }
        i++
    }
    return i
}

fun addNumbers(line: String): Long = line.split(" + ").map { it -> it.toLong() }.sum()

println(process("1 + (2 * 3) + (4 * (5 + 6))"))
println(process("2 * 3 + (4 * 5)"))
println(process("5 + (8 * 3 + 9 + 3 * 4 * 3"))
println(process("5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))"))
println(process("((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2"))

var lines: List<String> = File("input").readLines()
"Part 2: ${lines.map { it -> process(it) }.sum()}"
