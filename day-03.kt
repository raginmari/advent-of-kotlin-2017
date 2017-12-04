import kotlin.math.*

val input = 361527

fun main(args: Array<String>) {

  println("Result is ${solve(1)}")
  println("Result is ${solve(12)}")
  println("Result is ${solve(23)}")
  println("Result is ${solve(1024)}")
  println("Result is ${solve(input)}")
}

fun solve(square: Int): Int {

  // CAN BE IMPROVED, see day-03-2.kt

  // 1) Find ring of the given square
  var ring = 0
  var counter = 1
  while (counter < square) {
    ring += 1

    // The edge becomes 2 squares longer per ring
    val edgeLength = 1 + ring * 2

    // Do not count the 4 corners twice
    counter += 4 * edgeLength - 4
  }

  // 2) Walk back along the current ring and find the coordinate of the result
  val edgeLength = 1 + ring * 2
  var x = edgeLength / 2
  var y = x
  var mode = 1
  var stepsToGo = edgeLength - 1
  while (counter > square) {
    counter -= 1
    when (mode) {
      1 -> x -= 1
      2 -> y -= 1
      3 -> x += 1
      4 -> y += 1
    }

    stepsToGo -= 1
    if (stepsToGo == 0) {
      // Change direction at the corners and walk the same number of steps
      mode += 1
      stepsToGo = edgeLength - 1
    }
  }

  val result = x.absoluteValue + y.absoluteValue

  return result
}
