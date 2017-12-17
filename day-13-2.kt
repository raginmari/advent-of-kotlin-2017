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

/*

Idee:
Finde Delay x, bei dem kein Tracker in Position 0 ist, wenn er erreicht wird

x ist der Delay. Es wird die "depth" des Trackers addiert, da die Bedingung erfüllt sein soll, wenn er nach dem Delay ERREICHT wird.
Jeder Tracker ist nur zu gewissen Zeitpunkten in Position 0. Die Zeitpunkte hängen von seiner "range" ab und damit der Anzahl Schritte,
die er braucht um von Position 0 wieder in Position 0 zu landen ("#steps" in der Gleichung).

Die Ungleichung, die für jeden Tracker erfüllt sein muss:
(x + <depth>) % <#steps> != 0

Ist 0 in Picosekunden: 0, 4, ...
Steps: 1, 2, 1, 0
(x + 0) % 4 != 0

0, 2, 4
1, 0
(x + 1) % 2 != 0

0, 6, 12
1, 2, 3, 2, 1, 0
(x + 4) % 6 != 0

0, 6, 12
1, 2, 3, 2, 1, 0
(x + 6) % 6 != 0

Also:
1) (x + 0) % 4 != 0
2) (x + 1) % 2 != 0
3) (x + 4) % 6 != 0
4) (x + 6) % 6 != 0
-> x = 1: 2 schlägt fehl
-> x = 2: 3
-> x = 3: 2
-> x = 4: 1
-> x = 5: 2
-> x = 6: 4
-> x = 7: 2
-> x = 8: 3
-> x = 9: 2
-> x = 10: ENDE

*/

fun solve(input: List<String>): Int {

  val inequalities = mutableListOf<Inequality>()
  for (string in input) {
    val components = string.split(":")
    val depth = components.get(0).toInt()
    val range = components.get(1).toInt()
    inequalities.add(Inequality(depth, range))
  }
  println("inequalities: ${inequalities}")

  var delay = 0
  while (true) {
    val madeItThroughUndetected = inequalities.fold(true) { total, next -> total && next.resolve(delay) }
    if (madeItThroughUndetected) {
      break
    }
    delay += 1
  }

  val result = delay

  return result
}

class Inequality(val depth: Int, range: Int) {

  val numberOfSteps: Int

  init {
    numberOfSteps = 2 * range - 2
  }

  fun resolve(x: Int): Boolean {

    return ((x + depth) % numberOfSteps) != 0
  }

  override fun toString(): String {

    return "Inequality(depth:${depth}, numberOfSteps:${numberOfSteps})"
  }
}
