import java.io.File
import kotlin.math.*

fun main(args: Array<String>) {

  val input = loadPuzzleInput()
  println("Result is ${solve(input)}")
}

fun loadPuzzleInput(): List<String> {

  val puzzleInput = File("day-12-input.txt").readLines()

  return puzzleInput
}

fun solve(input: List<String>): Int {

  val connections = input.map { it.replace(Regex("\\s"), "") }
  println("${connections}")

  // Translate the input to a map and strings to integer
  var pipes = mutableMapOf<Int, List<Int>>()
  for (connection in connections) {
    val components = connection.split("<->")
    val sourceProgram = components.get(0).toInt()
    val destinationPrograms = components.get(1).split(",").map { it.toInt() }
    pipes.put(sourceProgram, destinationPrograms)
  }

  println("${pipes}")

  var queue = mutableListOf(0)
  var numberOfReachablePrograms = 0
  var visitedPrograms = mutableSetOf<Int>()
  while (!queue.isEmpty()) {
    // Removed the first program from the queue
    val program = queue.first()
    queue.remove(program)

    if (visitedPrograms.contains(program)) {
      // This program has been reached and counted before
      continue
    }

    // Keep track of the fact that the current program has been visited
    visitedPrograms.add(program)

    // Count the current program
    numberOfReachablePrograms += 1

    // Enqueue all programs that can be reached from the current program
    val connectedPrograms = pipes.get(program) ?: listOf<Int>()
    queue.addAll(connectedPrograms)
  }

  val result = numberOfReachablePrograms

  return result
}
