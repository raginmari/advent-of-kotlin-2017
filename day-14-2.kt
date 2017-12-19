import java.io.File
import kotlin.math.*

fun main(args: Array<String>) {

  val input = args.firstOrNull() ?: loadPuzzleInput()
  println("Result is ${solve(input)}")
}

fun loadPuzzleInput(): String {

  // Test input: flqrgnkx
  val puzzleInput = "ugkiagan"

  return puzzleInput
}

fun solve(input: String): Int {

  val grid = mutableListOf<Int>()
  for (i in 0..127) {
    val hashInput = "${input}-${i}"
    val hash = hash(hashInput)

    // Convert hex hash to binary
    val binaryString = binaryStringOf(hash)

    // Convert binary string to integer bits
    val bits = binaryString.asSequence().map { intValueOf(it) }

    grid.addAll(bits)
  }

  var numberOfRegions = 0
  for (i in 0 until grid.size) {
    if (isRegionAt(i, grid)) {
      // Count the region
      numberOfRegions += 1
      // Mark the region (its members will not be counted as separate regions in later iterations)
      markRegionAt(i, grid)
    }
  }

  val result = numberOfRegions

  return result
}

fun isRegionAt(cellIndex: Int, grid: MutableList<Int>): Boolean {

  // 0: "free"
  // 1: "used"
  // 2: already part of a region
  val cell = grid.get(cellIndex)
  return cell == 1
}

fun markRegionAt(cellIndex: Int, grid: MutableList<Int>) {

  val cell = grid.get(cellIndex)
  if (cell == 0 || cell == 2) { return }

  grid.set(cellIndex, 2)

  // Recurse to all four neighbors
  val neighborIndexes = listOf(Pair(1,0), Pair(0,1), Pair(-1,0), Pair(0,-1))
  for (pair in neighborIndexes) {
    val neighborIndex = neighborIndexOf(cellIndex, pair.first, pair.second)
    if (neighborIndex != null) { markRegionAt(neighborIndex, grid) }
  }
}

fun intValueOf(char: Char): Int  = char.toInt() - '0'.toInt()

fun neighborIndexOf(cellIndex: Int, dx: Int, dy: Int): Int? {

  val y = cellIndex / 128
  val x = cellIndex - y * 128

  // Handle edge cases
  if (x == 0 && dx < 0) { return null }
  if (y == 0 && dy < 0) { return null }
  if (x == 127 && dx > 0) { return null }
  if (y == 127 && dy > 0) { return null }

  val neighborIndex = (y + dy) * 128 + (x + dx)

  return neighborIndex
}

fun binaryStringOf(hexString: String): String {

  var result = ""
  for (hexChar in hexString.asSequence()) {
    result += binaryStringOf(hexChar)
  }

  return result
}

fun binaryStringOf(hexChar: Char): String {

  when (hexChar) {
    '0' -> return "0000"
    '1' -> return "0001"
    '2' -> return "0010"
    '3' -> return "0011"
    '4' -> return "0100"
    '5' -> return "0101"
    '6' -> return "0110"
    '7' -> return "0111"
    '8' -> return "1000"
    '9' -> return "1001"
    'a', 'A' -> return "1010"
    'b', 'B' -> return "1011"
    'c', 'C' -> return "1100"
    'd', 'D' -> return "1101"
    'e', 'E' -> return "1110"
    'f', 'F' -> return "1111"
  }

  return ""
}

fun hash(string: String): String {

  var lengths = bytesOf(string) + listOf(17, 31, 73, 47, 23)
  val sparseHash = sparseHashOf(lengths)
  val denseHash = denseHashOf(sparseHash)
  val hash = hexOf(denseHash)

  return hash
}

fun bytesOf(string: String): List<Int> {

  val result = string.asSequence().map { it.toInt() }.toList()

  return result
}

fun hexOf(hash: List<Int>): String {

  val result = hash.map { it.toString(16) }.fold("") { partial, next ->
    if (next.length == 1) {
      "${partial}0${next}"
    } else {
      "${partial}${next}"
    }
  }

  return result
}

fun denseHashOf(sparseHash: List<Int>): List<Int> {

  var hash = mutableListOf<Int>()

  for (i in 0 until sparseHash.size step 16) {
    val block = sparseHash.subList(i, i + 16)
    hash.add(reduceBlock(block))
  }

  return hash
}

fun reduceBlock(block: List<Int>): Int {

  val result = block.reduce { partial, next -> partial xor next }

  return result
}

fun sparseHashOf(lengths: List<Int>): List<Int> {

  val ribbon = Array<Int>(256, { it }).toList()
  var hash = ribbon.toMutableList()
  val size = hash.size
  var position = 0
  var skip = 0

  for (round in 0 until 64) {

    for (length in lengths) {

      // Store the elements to reverse in a separate list
      val elementsToReverse: List<Int>
      if (position + length < size) {
        elementsToReverse = hash.subList(position, position + length)
      } else {
        // Wrap around at the end
        val rest = length - (size - position)
        elementsToReverse = hash.subList(position, size) + hash.subList(0, rest)
      }

      // Reverse the elements
      val reversedElements = elementsToReverse.reversed()

      // Replace the elements in the ribbon with the inverted elements
      for (i in 0 until length) {
        val dstIndex = (position + i) % size
        hash.set(dstIndex, reversedElements.get(i))
      }

      // Advance position and skip size
      position = (position + length + skip) % size
      skip += 1
    }
  }

  return hash
}
