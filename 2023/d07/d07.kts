import java.io.File
import kotlin.Exception

data class Hand(val cards: String, val bid: Int)
fun Hand.isFiveOfAKind(): Boolean = this.cards.all { it == this.cards.first() }
fun Hand.isFourOfAKind(): Boolean = this.cards.toCharArray().distinct().maxOf { c -> this.cards.count { it == c } } == 4
fun Hand.isFullHouse(): Boolean = this.cards.toCharArray().distinct().map { c -> this.cards.count { it == c } }.containsAll(listOf(2,3))
fun Hand.isThreeOfAKind(): Boolean = this.cards.toCharArray().distinct().maxOf { c -> this.cards.count { it == c } } == 3
fun Hand.isTwoPair(): Boolean = this.cards.toCharArray().distinct().map { c -> this.cards.count { it == c } }.count { it == 2 } == 2
fun Hand.isPair(): Boolean = this.cards.toCharArray().distinct().maxOf { c -> this.cards.count { it == c } } == 2
fun Hand.getTypeNum(): Int = when {
    this.isFiveOfAKind() -> 7
    this.isFourOfAKind() -> 6
    this.isFullHouse() -> 5
    this.isThreeOfAKind() -> 4
    this.isTwoPair() -> 3
    this.isPair() -> 2
    else -> 1
}
fun Char.toCamelPokerValue(isAdvanced: Boolean = false) = when {
    this.isDigit() -> this.digitToInt()
    this == 'T' -> 10
    this == 'J' -> if(isAdvanced) { 1 } else { 11 }
    this == 'Q' -> 12
    this == 'K' -> 13
    this == 'A' -> 14
    else -> throw Exception("Invalid char in hand")
}

val handComparator = Comparator<Hand> { h1, h2 ->
    when {
        h1.getTypeNum() != h2.getTypeNum() -> h1.getTypeNum() - h2.getTypeNum()
        h1.cards[0] != h2.cards[0] -> h1.cards[0].toCamelPokerValue() - h2.cards[0].toCamelPokerValue()
        h1.cards[1] != h2.cards[1] -> h1.cards[1].toCamelPokerValue() - h2.cards[1].toCamelPokerValue()
        h1.cards[2] != h2.cards[2] -> h1.cards[2].toCamelPokerValue() - h2.cards[2].toCamelPokerValue()
        h1.cards[3] != h2.cards[3] -> h1.cards[3].toCamelPokerValue() - h2.cards[3].toCamelPokerValue()
        h1.cards[4] != h2.cards[4] -> h1.cards[4].toCamelPokerValue() - h2.cards[4].toCamelPokerValue()
        else -> throw Exception("HELP!")
    }
}
val input: String = File("input").readText()
input
    .split("\n")
    .filter { it.isNotBlank() }
    .map { Hand(it.substringBefore(' '), it.substringAfter(' ').toInt()) }
    .sortedWith(handComparator)
    .mapIndexed { index, hand -> hand.bid * (index + 1) }
    .sum()
    .let { println("Answer 1: $it") }


// Part 2
fun Hand.getAdvancedTypeNum(): Int = "23456789TQKA".map {
    Hand(this.cards.replace('J', it), bid).getTypeNum()
}.max()
val advancedHandComparator = Comparator<Hand> { h1, h2 ->
    when {
        h1.getAdvancedTypeNum() != h2.getAdvancedTypeNum() -> h1.getAdvancedTypeNum() - h2.getAdvancedTypeNum()
        h1.cards[0] != h2.cards[0] -> h1.cards[0].toCamelPokerValue(true) - h2.cards[0].toCamelPokerValue(true)
        h1.cards[1] != h2.cards[1] -> h1.cards[1].toCamelPokerValue(true) - h2.cards[1].toCamelPokerValue(true)
        h1.cards[2] != h2.cards[2] -> h1.cards[2].toCamelPokerValue(true) - h2.cards[2].toCamelPokerValue(true)
        h1.cards[3] != h2.cards[3] -> h1.cards[3].toCamelPokerValue(true) - h2.cards[3].toCamelPokerValue(true)
        h1.cards[4] != h2.cards[4] -> h1.cards[4].toCamelPokerValue(true) - h2.cards[4].toCamelPokerValue(true)
        else -> throw Exception("HELP!")
    }
}
input
    .split("\n")
    .filter { it.isNotBlank() }
    .map { Hand(it.substringBefore(' '), it.substringAfter(' ').toInt()) }
    .sortedWith(advancedHandComparator)
    .mapIndexed { index, hand -> hand.bid * (index + 1) }
    .sum()
    .let { println("Answer 2: $it") }





