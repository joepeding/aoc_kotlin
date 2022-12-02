import java.io.File

fun process(line: String): Long {
//    println("Processing line: '$line'")

    if (line.matches("^[0-9]+$".toRegex())) {
        return line.toLong()
    }

    if (line[0] == '(') {
        return process(substituteFirstParethesisBlock(line))
    }

    if (line.matches("^\\d.*".toRegex())) {
        return process(substituteFirstStatement(line))
    }

    println("Unreachable")
    return 0
}

fun substituteFirstParethesisBlock(line: String): String {
    if (line[0] != '(') {
        throw IllegalArgumentException("Line doesn't start with '('")
    }
    var closingIndex = findClosingParenthesisIndex(line, 0)
    if (closingIndex < line.lastIndex) {
        return "${process(line.substring(1, closingIndex))}${line.substring(closingIndex + 1)}"
    } else {
        return "${process(line.substring(1, closingIndex))}"
    }
}

fun substituteFirstStatement(line: String): String {
    if (line.matches("^\\d+\\s[+\\*]\\s\\(.*".toRegex())) {
        var (number1, op, rest) = line.split("\\s".toRegex(), 3)
        return "$number1 $op ${substituteFirstParethesisBlock(rest)}"
    } else if(line.matches("^\\d+\\s[+\\*]\\s\\d+$".toRegex())) {
        var (number1, op, number2) = line.split("\\s".toRegex(), 3)
        if (op[0] == '+') {
            return "${number1.toLong() + number2.toLong()}"
        } else {
            return "${number1.toLong() * number2.toLong()}"
        }
    } else if(line.matches("^\\d+\\s[+\\*]\\s\\d+.+".toRegex())) {
        var (number1, op, number2, rest) = line.split("\\s".toRegex(), 4)
        if (op[0] == '+') {
            return "${number1.toLong() + number2.toLong()} $rest"
        } else {
            return "${number1.toLong() * number2.toLong()} $rest"
        }
    } else {
        throw IllegalArgumentException("Couldn't understand line: $line")
    }
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

var lines: List<String> = File("input").readLines()
"Part 1: ${lines.map { it -> process(it) }.sum()}"
