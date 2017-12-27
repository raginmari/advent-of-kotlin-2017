import java.io.File

fun main(args: Array<String>) {

  val filename = args.firstOrNull() ?: "day-?-input.txt"
  val puzzleInput = loadPuzzleInput(filename)
  println("Input is ${puzzleInput}")

  println("Result is ${solve(puzzleInput)}")
}

fun loadPuzzleInput(filename: String): String {

  val lines = File(filename).readLines()
  val puzzleInput = lines.first()

  return puzzleInput
}

fun solve(puzzleInput: String): Int {

  // TODO: Implement

  val result = 0

  return result
}
