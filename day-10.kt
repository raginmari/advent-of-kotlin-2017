import java.io.File

fun main(args: Array<String>) {

  val input = "34,88,2,222,254,93,150,0,199,255,39,32,137,136,1,167"
  val lengths = input.split(",").toList().map { it.toInt() }
  println("Result is ${solve(256, lengths)}")

  /*println("Result is ${solve(5, listOf(3, 4, 1, 5))}")*/
}

fun solve(size: Int, lengths: List<Int>): Int {

  var ribbon = Array<Int>(size, { it }).toMutableList()
  var position = 0
  var skip = 0

  for (length in lengths) {

    // Store the elements to reverse in a separate list
    val elementsToReverse: List<Int>
    if (position + length < size) {
      elementsToReverse = ribbon.subList(position, position + length)
    } else {
      // Wrap around at the end
      val rest = length - (size - position)
      elementsToReverse = ribbon.subList(position, size) + ribbon.subList(0, rest)
    }

    // Reverse the elements
    val reversedElements = elementsToReverse.reversed()

    // Replace the elements in the ribbon with the inverted elements
    for (i in 0 until length) {
      val dstIndex = (position + i) % size
      ribbon.set(dstIndex, reversedElements.get(i))
    }

    // Advance position and skip size
    position = (position + length + skip) % size
    skip += 1
  }

  val result = ribbon.get(0) * ribbon.get(1)

  return result
}
