import java.io.File

fun nextIteration(input: MutableList<MutableList<Char>>): MutableList<MutableList<Char>> {
    var output = mutableListOf<MutableList<Char>>()
    input.forEachIndexed { rowNum, row ->
        output.add("".toMutableList())
        row.forEachIndexed { colNum, c ->
            if (c == 'L' && countSeatsAround(input, rowNum, colNum, '#') == 0) {
                output[rowNum].add('#')
            } else if (c == '#' && (countSeatsAround(input, rowNum, colNum, '#') - 1) >= 4) {
                output[rowNum].add('L')
            } else {
                output[rowNum].add(c)
            }
        }
    }
    return output
}

fun countSeatsAround(input: MutableList<MutableList<Char>>, rowNum: Int, colNum: Int, c: Char): Int {
    return input.filterIndexed { index, _ -> index >= rowNum - 1 && index <= rowNum + 1 }.map { row -> row.filterIndexed { index, _ -> index >= colNum - 1 && index <= colNum + 1 } }.flatten().count { it == c }
}

var lines: MutableList<MutableList<Char>> = File("input").readLines().map { it -> it.toMutableList() }.toMutableList()

// Part 1:
var iterationCounter = 0
var result = lines
while (iterationCounter < 200) {
    var newResult = nextIteration(result)
    println("${iterationCounter++}: ${newResult.size}: ${newResult[0].size} -> ${newResult.flatten().count {it -> it == '#'}}")
    if (result == newResult) {
        result = newResult
        break
    } else {
        result = newResult
    }
}
//println(countSeatsAround(result, 1, 8, '#'))


// Part 2:
fun nextIteration2(input: MutableList<MutableList<Char>>): MutableList<MutableList<Char>> {
    var output = mutableListOf<MutableList<Char>>()
    input.forEachIndexed { rowNum, row ->
        output.add("".toMutableList())
        row.forEachIndexed { colNum, c ->
            if (c == 'L' && countFirstVisibleSeatsAround(input, rowNum, colNum, '#') == 0) {
                output[rowNum].add('#')
            } else if (c == '#' && (countFirstVisibleSeatsAround(input, rowNum, colNum, '#')) >= 5) {
                output[rowNum].add('L')
            } else {
                output[rowNum].add(c)
            }
        }
    }
    return output
}
var directions = listOf(Pair(0,1), Pair(0, -1), Pair(-1, 1), Pair(-1, 0), Pair(-1, -1), Pair(1, 1), Pair(1, 0), Pair(1,-1))
fun countFirstVisibleSeatsAround(input: MutableList<MutableList<Char>>, rowNum: Int, colNum: Int, c: Char): Int {
    return directions.map { it -> findFirstSeatInDirection(input, rowNum, colNum, it.first, it.second)}.count { it -> it == c }
//    return input.filterIndexed { index, _ -> index >= rowNum - 1 && index <= rowNum + 1 }.map { row -> row.filterIndexed { index, _ -> index >= colNum - 1 && index <= colNum + 1 } }.flatten().count { it == c }
}
fun findFirstSeatInDirection(input: MutableList<MutableList<Char>>, row: Int, col: Int, dRow: Int, dCol: Int): Char {
    if (row + dRow < 0 || row + dRow >= input.size) {
        return '.'
    }
    if (col + dCol < 0 || col + dCol >= input[row + dRow].size) {
        return '.'
    }
    if (input[row + dRow][col + dCol] == '.') {
        return findFirstSeatInDirection(input, row + dRow, col + dCol, dRow, dCol)
    } else {
        return input[row + dRow][col + dCol]
    }
}
var iterationCounter2 = 0
var part2result = lines
while (iterationCounter2 < 200) {
    var newResult = nextIteration2(part2result)
    println("${iterationCounter2++}: ${newResult.size}: ${newResult[0].size} -> ${newResult.flatten().count {it -> it == '#'}}")
    if (part2result == newResult) {
        part2result = newResult
        break
    } else {
        part2result = newResult
    }
}