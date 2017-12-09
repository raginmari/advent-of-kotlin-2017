import java.io.File
import kotlin.text.Regex

fun main(args: Array<String>) {

  /*val input = File("day-07-input-test.txt").readLines()*/
  val input = File("day-07-input.txt").readLines()

  println("Result is ${solve(input)}")
}

fun solve(input: List<String>): String {

  // Matches a program name
  val regex = Regex("([a-z])+")

  // Keeps track of all programs and provides efficient lookup by name
  var programs = mutableMapOf<String, Program>()

  for (line in input) {
    // Parse program names from line
    val programNames = regex.findAll(line).toList().map { it -> it.value }

    var tower = mutableListOf<Program>()
    for (name in programNames) {
      // Do not create duplicate programs but use existing programs
      val existingProgram = programs.get(name)
      // Add the program (no matter if existing or new) to the current tower
      tower.add(existingProgram ?: Program(name))
      // Add the program to the map (may already be part of the map, though)
      programs.put(name, tower.last())
    }

    //-----
    // NOTE: this is overengineered, because instead of an actual parent a "has parent" flag would suffice!
    //-----
    
    // Set the parent of all programs except the first one (the root).
    for (i in tower.indices.drop(1)) {
      // The parent of a program is the root of its tower
      tower.get(i).parent = tower.get(0)
    }
  }

  // There must be one orphan program i.e. a program without a parent = the result
  val orphan = programs.values.find { it -> it.parent == null }

  val result = orphan?.name ?: "Error"

  return result
}

data class Program(val name: String) {
  var parent: Program? = null
}
