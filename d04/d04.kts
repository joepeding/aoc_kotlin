import java.io.File
import java.util.SortedSet
import java.util.regex.Pattern

val requiredFields: Set<String> = setOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")
val validEyeColors: Set<String> = setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")

val mappedPassports: List<Map<String, String>> = File("input").readText().split(Pattern.compile("\\s\\s")).map { it.split(Pattern.compile("\\s")).map { it.split(Pattern.compile(":")) }.map { it[0] to it[1] }.toMap() }

fun validateFields(passport: Map<String, String>): Boolean {
    if (!Pattern.compile("^\\d{4}$").matcher(passport["byr"]).matches() || !(1920..2002).contains(passport["byr"]!!.toInt())) { return false }
    if (!Pattern.compile("^\\d{4}$").matcher(passport["iyr"]).matches() || !(2010..2020).contains(passport["iyr"]!!.toInt())) { return false }
    if (!Pattern.compile("^\\d{4}$").matcher(passport["eyr"]).matches() || !(2020..2030).contains(passport["eyr"]!!.toInt())) { return false }
    if (Pattern.compile("^\\d{2}in").matcher(passport["hgt"]).matches()) {
        if (!(59..76).contains(passport["hgt"]!!.subSequence(0,2).toString().toInt())) {
            return false
        }
    } else if (Pattern.compile("^\\d{3}cm").matcher(passport["hgt"]).matches()) {
        if (!(150..193).contains(passport["hgt"]!!.subSequence(0,3).toString().toInt())) {
            return false
        }
    } else {
        return false
    }
    if (!Pattern.compile("#[0-9a-f]{6}").matcher(passport["hcl"]).matches()) { return false }
    if (!validEyeColors.contains(passport["ecl"])) { return false }
    if (!Pattern.compile("\\d{9}").matcher(passport["pid"]).matches()) { return false }
    return true
}

println("Method 1:")
println("Total: " + mappedPassports.size)
println("Valid1: " + mappedPassports.filter {it -> it.keys.containsAll(requiredFields)}.size)
println("Valid2: " + mappedPassports.filter {it -> it.keys.containsAll(requiredFields)}.filter {it -> validateFields(it) }.size)
