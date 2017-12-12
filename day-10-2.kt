import java.io.File

fun main(args: Array<String>) {

  val puzzleInput = "34,88,2,222,254,93,150,0,199,255,39,32,137,136,1,167"
  val input = args.firstOrNull() ?: puzzleInput

  println("Result is ${solve(input)}")
}

fun solve(input: String): String {

  val bytes = bytesOf(input)
  println("bytes: ${bytes}")

  var lengths = bytes + listOf(17, 31, 73, 47, 23)
  println("lengths: ${lengths}")

  var ribbon = Array<Int>(256, { it }).toList()
  println("ribbon: ${ribbon}")

  val sparseHash = sparseHashOf(ribbon, lengths)
  println("sparse hash: ${sparseHash}")

  val denseHash = denseHashOf(sparseHash)
  println("dense hash: ${denseHash}")

  val hash = hexOf(denseHash)

  val result = hash

  return result
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

fun sparseHashOf(ribbon: List<Int>, lengths: List<Int>): List<Int> {

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
