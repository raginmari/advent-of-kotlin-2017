import java.io.File
import kotlin.math.*

fun main(args: Array<String>) {

  val input = loadPuzzleInput()
  println("Result is ${solve(input)}")
}

fun loadPuzzleInput(): List<String> {

  val lines = File("day-13-input.txt").readLines()
  val puzzleInput = lines.map { it.replace(Regex("\\s"), "") }

  return puzzleInput
}

fun solve(input: List<String>): Int {

  println("input: ${input}")

  // Build the firewall
  var maxDepth = 0
  val firewall = mutableMapOf<Int, Layer>()
  input.forEach {
    val layer = makeLayer(it)
    firewall.put(layer.depth, layer)
    maxDepth = maxOf(maxDepth, layer.depth)
  }
  println("firewall: ${firewall}")

  var position = -1 // The first step brings us to position 0
  var severity = 0
  val durationOfTheWholeTrip = maxDepth + 2 // +2: enter in step one, leave in the last step
  for (pico in 0 until durationOfTheWholeTrip) {
    // Move self by one layer
    position += 1

    // Check if we collide with the tracker in this layer (if there is any)
    val tracker = firewall.get(position)
    if (tracker != null && tracker.position == 0) {
      // Caught! Compute and add severity
      severity += tracker.depth * tracker.range
    }

    // Move all trackers
    firewall.values.forEach { it.moveOneStep() }
  }

  val result = severity

  return result
}

fun makeLayer(string: String): Layer {

  val components = string.split(":")
  val depth = components.get(0).toInt()
  val range = components.get(1).toInt()
  val layer = Layer(depth, range)

  return layer
}

data class Layer(val depth: Int, val range: Int) {

  var position = 0
  val steps: List<Int>
  var stepIndex = 0

  init {
    // range 3
    // 0,1,2
    // 2,1,0
    // (0),1,2,(2),1,0
    val positions = Array<Int>(range, { it }).toList()
    val temp = positions.drop(1).toMutableList()
    temp.addAll(positions.reversed().drop(1))
    steps = temp.toList()
  }

  fun moveOneStep() {

    position = steps[stepIndex]
    stepIndex = (stepIndex + 1) % steps.size
  }
}
