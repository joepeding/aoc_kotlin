import java.io.File
import java.lang.StringBuilder

var cmds: List<Pair<String,String>> = File("input").readLines().map { it -> it.split(" = ")[0] to it.split(" = ")[1]}
var mem: MutableMap<Long, Long> = mutableMapOf()

fun applyMask(mask: String, value: Long): List<Long> {
    var old: String = StringBuilder(java.lang.Long.toBinaryString(value)).padStart(mask.length, '0').toString()
    var new = StringBuilder()
    mask.forEachIndexed { i, c -> if(c == '0') new.append(old[i]) else new.append(c)}

    return extendMasks(mutableListOf(new.toString())).map { it -> it.toLong(2) }
}

fun extendMasks(masks: MutableList<String>): List<String> {
    var firstX = masks[0].indexOfFirst { c -> c == 'X' }
    if (firstX >= 0) {
        var newMasks = mutableListOf<String>()
        newMasks.addAll(masks.map {it -> it.replaceFirst("X", "0")})
        newMasks.addAll(masks.map {it -> it.replaceFirst("X", "1")})
        return extendMasks(newMasks)
    } else {
        return masks
    }
}

var currentMask = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
cmds.forEach {(cmd, value) ->
    if (cmd == "mask") {
        currentMask = value
    } else {
        applyMask(currentMask, cmd.substring(4, cmd.lastIndex).toLong()).forEach {
            mem.put(it, value.toLong())
        }
    }
}

println("Part 1: ${mem.values.sum()}")