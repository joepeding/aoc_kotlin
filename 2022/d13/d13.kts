import java.io.File

val input: String = File("input").readText()

enum class Type { LIST, INTEGER }

class Input(
    public var parent: Input? = null,
    public var children: MutableList<Input> = mutableListOf(),
    public var value: Int? = null,
    public var type: Type = Type.LIST
) {
    override fun toString(): String {
        if ( type == Type.LIST ) {
            return "$children"
        } else {
            return value.toString()
        }
    }
}

fun inputToInputs(source: String): Input {
    var current: Input = Input(null)
    source
        .substring(1, source.length-1)
        .let { all ->
            var temp = ""
            all.windowed(1,1).forEach { c ->
                when (c) {
                    "[" -> {
                        var newChild = Input(current)
                        current.children.add(newChild)
                        current = newChild
                    }
                    "]" -> {
                        if ( temp.isNotBlank() ) {
                            current.children.add(Input(parent = current, value = temp.toInt(), type = Type.INTEGER))
                            temp = ""
                        }
                        current = current.parent!!
                    }
                    "," ->  {
                        if ( temp.isNotBlank() ) { current.children.add(Input(parent = current, value = temp.toInt(), type = Type.INTEGER)) }
                        temp = ""
                    }
                    else -> {
                        temp = "$temp$c"
                    }
                }
            }
            if ( temp.isNotEmpty() ) { current.children.add(Input(parent = current, value = temp.toInt(), type = Type.INTEGER)) }
        }
    return current
}

fun compareInput(left: Input, right: Input): Int {
    if (left.type == Type.INTEGER && right.type == Type.INTEGER) {
        when {
            right.value!! >  left.value!! ->  return 1
            right.value!! == left.value!! ->  return 0
            right.value!! <  left.value!! ->  return -1
        }
    }

    if (left.type == Type.INTEGER && right.type == Type.LIST) {
        return compareInput(Input(null, mutableListOf(left)), right)
    }

    if (left.type == Type.LIST && right.type == Type.INTEGER) {
        return compareInput(left, Input(null, mutableListOf(right)))
    }

    if (left.children.isEmpty() && right.children.isEmpty()) { return 0 }
    left.children.forEachIndexed { i: Int, l: Input ->
        if (i > right.children.size - 1) { return -1 }
        var result = compareInput(l, right.children.get(i))
        when (result) {
            -1 -> return -1
            1 -> return 1
        }
    }
    if (right.children.size > left.children.size) {
        return 1
    }
    return 0
}

// Part 1
print("Answer 1: ")
var correctIndices = mutableListOf<Int>()
var pairs = input
    .split("\n\n")
    .map { it
        .split("\n")
        .filter { it.isNotEmpty() }
        .map { inputToInputs(it) }
    }
    .forEachIndexed { i: Int, p: List<Input> ->
        p.let { (left, right) ->
            if (compareInput(left, right) == 1) {
                correctIndices.add(i +1)
            }
        }
    }
println(correctIndices.sum())

//Part 2
print("Answer 2: ")
val sep1 = inputToInputs("[[2]]")
val sep2 = inputToInputs("[[6]]")
var packets: List<Input> = input
    .split("\n")
    .filter { it.isNotEmpty() }
    .map { inputToInputs(it) }
    .toMutableList()
    .also { it.addAll(listOf(sep1, sep2))}
    .sortedWith( Comparator { left: Input, right: Input -> -1 * compareInput(left, right)} )

(packets.indexOf(sep1) + 1) * (packets.indexOf(sep2) + 1)

