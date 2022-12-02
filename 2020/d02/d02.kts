import java.io.File
import java.util.SortedSet
import java.util.regex.Pattern

val lines: MutableList<String> = File("input").useLines { it.toMutableList() }
val passwords: MutableList<List<String>> = lines.map {
        it -> it.split(": ")
    } as MutableList<List<String>>

fun policyToRegex (policy: String): Pattern {
    val policyParts = policy.split(" ")
    val regexString = StringBuilder()
    regexString.append('^') // Start
    regexString.append("(?:") // Start non-capturing group
    regexString.append("[^${policyParts[1]}]*+") // Any number of other characters
    regexString.append(policyParts[1]) // The character we're looking for
    regexString.append(')') // End group
    regexString.append("{${policyParts[0].replace('-',',')}}") // Expected count
    regexString.append("[^${policyParts[1]}]*+") // Any number of other characters
    regexString.append('$') // End of string

    return Pattern.compile(regexString.toString())
}

//println(passwords.filter { it -> !policyToRegex(it[0]).matcher(it[1]).matches() })
println(passwords.filter { it -> policyToRegex(it[0]).matcher(it[1]).matches() }.size)


fun policyToAlternativeRegex (policy: String): Pattern {
    val (counts, letter) = policy.split(" ")
    val (firstPos, secondPos) = counts.split('-').map { it -> it.toInt() }
    var leading = firstPos - 1
    var between = secondPos - firstPos - 1

    return Pattern.compile("(^.{$leading}$letter.{$between}[^$letter])|(^.{$leading}[^$letter].{$between}$letter)")
}

println(passwords.filter { it -> policyToAlternativeRegex(it[0]).matcher(it[1]).find() }.size)




//
//println("FAILS")
//passwords.filter {
//    it -> !(
//        policyToAlternativeRegex(it[0]).first.matcher(it[1]).find()
//                xor
//        policyToAlternativeRegex(it[0]).second.matcher(it[1]).find()
//    )
//}.forEach {
//    print(it[0])
//    print("      ")
//    print(policyToAlternativeRegex(it[0]))
//    print("      ")
//    println("'${it[1]}'")
//}
//println("PASSES")
//passwords.filter {
//    it -> (
//        policyToAlternativeRegex(it[0]).first.matcher(it[1]).find()
//                xor
//                policyToAlternativeRegex(it[0]).second.matcher(it[1]).find()
//        )
//}.forEach {
//    print(it[0])
//    print("      ")
//    print(policyToAlternativeRegex(it[0]))
//    print("      ")
//    println("'${it[1]}'")
//}
