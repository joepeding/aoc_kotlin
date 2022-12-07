import java.io.File as JavaFile
import java.util.TreeMap

open class FsItem(
    val name: String,
    val children: MutableList<FsItem> = mutableListOf(),
    val parent: FsItem? = null
) {
    open fun size(): Int = children.sumOf { it.size() }
}

class File(
    name: String,
    val size: Int,
    parent: FsItem
): FsItem(name, mutableListOf(), parent) {
    override fun size() = size
}

val input: String = JavaFile("input").readText()
val fs = FsItem("/")
val allDirs: MutableList<FsItem> = mutableListOf()
var currentDir = fs

// Part 1
print("Answer 1: ")
input
    .split("$ ")
    .forEach { s ->
        if (s.startsWith("ls")) {
            addDirs(currentDir, s)
        } else if (s.startsWith("cd")) {
            val nextDir = s.trim().split(" ")[1]
            currentDir = when (nextDir) {
                "/" -> fs
                ".." -> currentDir.parent!!
                else -> currentDir.children.filter { it.name == nextDir }.first()
            }
        }
    }
println(allDirs.filter { it.size() <= 100000 }.sumOf { it.size() })

fun addDirs(d: FsItem, s: String): Unit {
    s
        .split("\n")
        .filter { it.isNotBlank() }
        .forEach {
            if (it == "ls") {
                // do nothing
            } else if (it.startsWith("dir")) {
                val newDir = FsItem(it.split(" ").get(1), mutableListOf(), d)
                d.children.add(newDir)
                allDirs.add(newDir)
            } else {
                it.split(" ").let { (size, name) -> d.children.add(File(name, size.toInt(), d)) }
            }
        }
}

//Part 2
print("Answer 2: ")
val total = 70000000
val totalUsed = fs.size()
val totalFree = total - totalUsed
val toFree = 30000000 - totalFree

allDirs.sortBy { it.size() }
allDirs.filter { it.size() > toFree }.first().let { println("Dir ${it.name}: ${it.size()}") }


