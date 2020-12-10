import java.io.File

var lines: MutableList<Triple<String, Int, Boolean>> = File("input").readLines().map { it.split(" ") }.map { it -> Triple(it[0], it[1].toInt(), false) }.toMutableList()
lines.forEach { println(it) }
val instructionCount = lines.size

var i = 0;
var acc = 0;
while (!lines[i].third) {
    if (lines[i].first == "jmp") {
        i += lines[i].second - 1
    } else if(lines[i].first == "acc") {
        acc += lines[i].second
    }
    lines[i] = lines[i].copy(third = true)
    i++

    if (i >= instructionCount) {
        println("End of instructionset reached")
        break
    }
}
println("InstructionNum: $i")
println("Acc: $acc")



lines = File("input").readLines().map { it.split(" ") }.map { it -> Triple(it[0], it[1].toInt(), false) }.toMutableList()
var superIndex = 0
while (superIndex < instructionCount) {
    if (lines[superIndex].first == "acc") {
        superIndex++
        continue
    }

    var linesCopy: MutableList<Triple<String, Int, Boolean>> = lines.map {it -> it.copy() }.toMutableList()
    if (linesCopy[superIndex].first == "jmp") {
        println("Changing line $superIndex from jmp to nop")
        linesCopy[superIndex] = linesCopy[superIndex].copy(first = "nop")
    } else {
        println("Changing line $superIndex from nop to jmp")
        linesCopy[superIndex] = linesCopy[superIndex].copy(first = "jmp")
    }

    var i = 0;
    var acc = 0;
    while (!linesCopy[i].third) {
        if (linesCopy[i].first == "jmp") {
            i += linesCopy[i].second - 1
        } else if(linesCopy[i].first == "acc") {
            acc += linesCopy[i].second
        }
        linesCopy[i] = linesCopy[i].copy(third = true)
        i++

        if (i >= instructionCount) {
            println("End of instructionset reached")
            break
        }
    }
    println("InstructionNum: $i")
    println("Acc: $acc")

    superIndex++
}
