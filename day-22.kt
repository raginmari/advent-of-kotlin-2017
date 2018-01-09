import java.io.File

typealias Node = Byte

fun main(args: Array<String>) {

  val filename = args.firstOrNull() ?: "day-22-input.txt"
  val puzzleInput = loadPuzzleInput(filename)
  println("Input is ${puzzleInput}")

  println("Result is ${solve(puzzleInput)}")
}

fun solve(puzzleInput: PuzzleInput): Int {

  val grid = mutableMapOf<Long, Byte>()
  val x0 = (5000 - puzzleInput.width / 2).toLong()

  for (node in puzzleInput.nodes) {

  }

  val result = 0

  return result
}

data class PuzzleInput(val width: Int, val nodes: List<Node>)

fun loadPuzzleInput(filename: String): PuzzleInput {

  val lines = File(filename).readLines()

  var width: Int? = null
  val allNodes = mutableListOf<Node>()
  for (line in lines) {
    val nodes = parseNodesFrom(line)
    width = width ?: nodes.size
    allNodes.addAll(nodes)
  }

  val puzzleInput = PuzzleInput(width!!, allNodes)

  return puzzleInput
}

fun parseNodesFrom(string: String): List<Node> {

  val result = string.asIterable().map {
    when (it) {
      '#' -> 1.toByte()
      '.' -> 0.toByte()
      else -> throw IllegalArgumentException()
    }
  }

  return result
}
