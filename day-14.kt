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

  var totalNumberOfUsedBits = 0
  for (i in 0..127) {
    val hashInput = "${input}-${i}"
    val hash = hash(hashInput)
    val binaryHashValue = binaryStringOf(hash)
    val numberOfUsedBits = binaryHashValue.asSequence().fold(0) { total, next -> total + if (next == '1') 1 else 0 }
    totalNumberOfUsedBits += numberOfUsedBits
  }

  /*val testBinaryString = binaryStringOf("a0c2017")
  println("test binary string: ${testBinaryString}")*/

  val result = totalNumberOfUsedBits

  return result
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
