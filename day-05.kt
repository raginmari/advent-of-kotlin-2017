import java.io.File

fun main(args: Array<String>) {

  val input = File("day-05-input.txt").readLines()
  val jumpOffsets = input.map { line -> line.toInt() }

  println("Result is ${solve(jumpOffsets)}")
  /*println("Result is ${solve(listOf(0, 3, 0, 1, -3))}")*/
}

fun solve(input: List<Int>): Int {

  var pos = 0
  var numberOfJumps = 0
  var jumpOffsets = input.toMutableList()
  while (pos >= 0 && pos < jumpOffsets.size) { // Jump while in bounds
    val offset = jumpOffsets.get(pos)
    jumpOffsets.set(pos, offset + 1)

    pos += offset
    numberOfJumps += 1
  }

  val result = numberOfJumps

  return result
}
