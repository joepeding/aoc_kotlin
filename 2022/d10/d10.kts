import java.io.File

val input: String = File("input").readText()

// Part 1
print("Answer 1: ")
var register = 1
var out = 0
var c = 0
var pixels: MutableList<String> = mutableListOf()
input
    .split("\n")
    .filter { it.isNotEmpty() }
    .forEach { s ->
        repeat(if (s.startsWith("noop")) { 1 } else { 2 }) {
            c = c + 1
            if (listOf(20, 60, 100, 140, 180, 220).contains(c)) {
                out += c * register
            }
            pixels.add(if(listOf(register - 1, register, register + 1).contains((c-1)%40)) { "#" } else { "." })
        }

        if (s.startsWith("noop")) { return@forEach }
        register += s.split(" ")[1].toInt()
    }
println(out)


//Part 2
println("Answer 2: ")
pixels.windowed(40,40).forEach { println( it.joinToString("") ) }
