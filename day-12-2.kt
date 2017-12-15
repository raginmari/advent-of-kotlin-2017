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

  // Translate the input to a map and strings to integer
  var pipes = mutableMapOf<Int, List<Int>>()
  for (connection in connections) {
    val components = connection.split("<->")
    val sourceProgram = components.get(0).toInt()
    val destinationPrograms = components.get(1).split(",").map { it.toInt() }
    pipes.put(sourceProgram, destinationPrograms)
  }

  // Start with a queue containing all programs
  var queue = pipes.keys.toMutableList()
  var numberOfGroups = 0
  while (!queue.isEmpty()) {
    // Remove the first program from the queue
    val program = queue.first()
    queue.remove(program)

    // Compute the group of the program
    val group = findGroupOfProgram(program, pipes)
    println("${group}")

    // Count the group (cannot be empty because it contains 'program')
    numberOfGroups += 1

    // Remove all programs in the group from the queue or else the group
    // would be computed and counted for each one of its members
    queue.removeAll(group)
  }

  val result = numberOfGroups

  return result
}

fun findGroupOfProgram(sourceProgram: Int, pipes: Map<Int, List<Int>>): Set<Int> {

  var queue = mutableListOf(sourceProgram)
  var visitedPrograms = mutableSetOf<Int>()
  while (!queue.isEmpty()) {
    // Remove the first program from the queue
    val program = queue.first()
    queue.remove(program)

    if (visitedPrograms.contains(program)) {
      // This program has been reached before
      continue
    }

    // Keep track of the fact that the current program has been visited
    visitedPrograms.add(program)

    // Enqueue all programs that can be reached from the current program
    val connectedPrograms = pipes.get(program) ?: listOf<Int>()
    queue.addAll(connectedPrograms)
  }

  return visitedPrograms.toSet()
}
