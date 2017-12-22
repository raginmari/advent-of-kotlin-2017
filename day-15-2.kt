import java.io.File
import kotlin.math.*

fun main(args: Array<String>) {

  val input = inputFrom(args.firstOrNull()) ?: loadPuzzleInput()
  println("Input: ${input}")

  println("Result is ${solve(input)}")
}

fun inputFrom(string: String?): List<Int>? {

  if (string == null) return null;
  val input = string.split(",").map { it.toInt() }

  return input
}

fun loadPuzzleInput(): List<Int> {

  val puzzleInput = listOf(277, 349)

  return puzzleInput
}

fun solve(input: List<Int>): Int {

  val generators = arrayOf(
    Generator(input.get(0), 16807, 4),
    Generator(input.get(1), 48271, 8))
  var numberOfMatches = 0

  for (i in 1..5000000) {
    val valueA = generators[0].generateNextValue()
    val valueB = generators[1].generateNextValue()
    if (judge(valueA, valueB)) {
      numberOfMatches += 1
    }
  }

  val result = numberOfMatches

  return result
}

fun judge(valueA: Long, valueB: Long): Boolean {

  val bitsA = valueA and 0xffff
  val bitsB = valueB and 0xffff
  val result = bitsA == bitsB

  return result
}

class Generator(initialValue: Int, val factor: Int, val multiple: Int) {

  var lastValue: Long // Long to avoid overflow

  init {

    lastValue = initialValue.toLong()
  }

  fun generateNextValue(): Long {

    var value: Long
    do {
      value = (lastValue * factor) % 2147483647
      lastValue = value
    } while (value % multiple != 0L)

    return value
  }
}
