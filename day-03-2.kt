import kotlin.collections.MutableMap

val input = 361527

fun main(args: Array<String>) {

  println("Result is ${solve(input)}")
}

fun solve(limit: Int): Int {

  // The grid is a map of coordinates (x, y) and values.
  // Each value is the sum of all neighbors of the cell at the coordinate
  var grid = mutableMapOf<Pair<Int, Int>, Int>()
  grid.put(Pair(0, 0), 1)

  // Stores the last value
  var head = 1
  // Stores the current mode: move right (1), up (2), left (3), down (4)
  var mode = 1
  // Stores the number of steps to walk before changing the mode (direction of movement)
  var stepsPerMode = 1
  // Stores the number of steps to walk. Counts down to 0.
  var stepsToGo = stepsPerMode
  // The current coordinate
  var x = 0
  var y = 0

  // While the last value is less or equal the given limit
  while (head <= limit) {
    // Move a step
    when (mode) {
      1 -> x += 1
      2 -> y += 1
      3 -> x -= 1
      4 -> y -= 1
    }

    stepsToGo -= 1
    if (stepsToGo == 0) {
      // Increment step size in modes 2 and 4
      if (mode == 2 || mode == 4) { stepsPerMode += 1 }
      stepsToGo = stepsPerMode

      // Advance (and wrap) mode
      mode = if (mode == 4) { 1 } else { mode + 1 }
    }

    // Compute the sum of the neighbors at the current coordinate and store it in the map
    val coordinate = Pair(x, y)
    head = sumOfNeighborsAtCoordinate(coordinate, grid)
    grid.put(coordinate, head)
  }

  val result = head

  return result
}

fun sumOfNeighborsAtCoordinate(coordinate: Pair<Int, Int>, grid: Map<Pair<Int, Int>, Int>): Int {

  val x = coordinate.first
  val y = coordinate.second
  var sum = 0
  sum += grid.get(Pair(x + 1, y + 0)) ?: 0
  sum += grid.get(Pair(x + 1, y + 1)) ?: 0
  sum += grid.get(Pair(x + 0, y + 1)) ?: 0
  sum += grid.get(Pair(x - 1, y + 1)) ?: 0
  sum += grid.get(Pair(x - 1, y + 0)) ?: 0
  sum += grid.get(Pair(x - 1, y - 1)) ?: 0
  sum += grid.get(Pair(x + 0, y - 1)) ?: 0
  sum += grid.get(Pair(x + 1, y - 1)) ?: 0

  return sum
}
