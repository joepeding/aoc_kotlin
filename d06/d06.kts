import java.io.File
import java.util.regex.Pattern

println("Part 1:")
println(File("input").readText().split("\\s\\s".toRegex()).map { group -> group.split("\\s".toRegex()).map { person -> person.toList() }.flatten().toSet() }.map {it -> it.size}.sum() )

println("Part 2:")
println(File("input").readText().split("\\s\\s".toRegex()).map { group -> group.split("\\s".toRegex()) }.map { group -> ('a'..'z').filter { c -> group.all { person -> person.contains(c) } } }.map {it -> it.size}.sum() )