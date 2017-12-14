import java.io.File
import kotlin.math.*

// !!!!!!!!!
// !!!!!!!!!

// Fails for input "n,se" (but solves the puzzle input)

// !!!!!!!!!
// !!!!!!!!!

fun main(args: Array<String>) {

  val input = args.firstOrNull() ?: loadPuzzleInput()
  println("Result is ${solve(input)}")
}

fun loadPuzzleInput(): String {

  val file = File("day-11-input.txt").readLines()
  val puzzleInput = file.first()

  return puzzleInput
}

fun solve(stepsString: String): Int {

  val steps = stepsString.split(",")

  // Build a map that associates directions to the number of steps in that direction
  var directionsMap = mutableMapOf("n" to 0, "ne" to 0, "se" to 0, "s" to 0, "sw" to 0, "nw" to 0)
  for (step in steps) {
    val value = 1 + (directionsMap.get(step) ?: 0)
    directionsMap.put(step, value)
  }

  println("${directionsMap}")

  // N : 0
  // NE: 1
  // SE: 2
  // S : 3
  // SW: 4
  // NW: 5
  val directionsToNumbers = mapOf("n" to 0, "ne" to 1, "se" to 2, "s" to 3, "sw" to 4, "nw" to 5)
  // Convert the map to a sorted array so that directions can be accessed more easily
  var directions = directionsMap.toSortedMap(compareBy<String> { directionsToNumbers.get(it)!! }).values.toMutableList()
  println("directions: ${directions}")

  // Inverse directions can be "normalized", e.g. 10 steps north and 9 steps south are equivalent to 1 step north and 0 steps south
  normalize(directions, 0, 3)
  normalize(directions, 1, 4)
  normalize(directions, 2, 5)
  println("normalized directions: ${directions}")

  // Pairs of steps in directions i and i + 2 can be replaced with one step in direction i + 1
  for (i in 0..5) {
    collate(directions, i, (i + 2) % 6, (i + 1) % 6)
  }
  println("collated directions: ${directions}")

  // The number of steps is the number of steps left in the array
  val numberOfSteps = directions.fold(0) { total, next -> total + next }

  val result = numberOfSteps

  return result
}

fun normalize(list: MutableList<Int>, index1: Int, index2: Int) {

  if (list[index1] < list[index2]) {
    list[index2] = list[index2] - list[index1]
    list[index1] = 0
  } else {
    list[index1] = list[index1] - list[index2]
    list[index2] = 0
  }
}

fun collate(list: MutableList<Int>, srcIndex1: Int, srcIndex2: Int, dstIndex: Int) {

  // For each pair of steps in direction 1 and 2, count one step in the destination direction
  list[dstIndex] += minOf(list[srcIndex1], list[srcIndex2])
  // Subtract the collated steps by normalizing the directions
  normalize(list, srcIndex1, srcIndex2)
}
