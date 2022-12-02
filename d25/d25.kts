import kotlin.math.log

var test = false
var PKcard = 0L
var PKdoor = 0L
if (test) {
    PKcard = 5764801L
    PKdoor = 17807724L
} else {
    PKcard = 15113849L
    PKdoor = 4206373L
}

fun loopSizeFromPK(pk: Long): Long {
    var loopCounter = 0L
    var currentValue = pk
    while (currentValue != 1L) {
        while (currentValue % 7 != 0L) {
            currentValue += 20201227
        }
        currentValue = currentValue / 7
        loopCounter++
    }
    return loopCounter
}

fun transform(subjectNumber: Long, loopSize: Long): Long {
    var value = 1L
    for (i in 1..(loopSize)) {
        value = (value * subjectNumber) % 20201227
    }
    return value
}

val loopSizeCard = loopSizeFromPK(PKcard)
val encryptionKey = transform(PKdoor, loopSizeCard)
println("Part 1: $encryptionKey")

