import java.io.File

fun main(args: Array<String>) {

  val input = File("day-04-input.txt").readLines()
  println("Result is ${solve(input)}")
}

fun solve(passphrases: List<String>): Int {

  var numberOfValidPassphrases = 0
  passphrases.forEach {
    // Split each passphrase into its components
    val components = it.split(" ")

    // Add all components to a set eliminating duplicates
    var uniqueComponents: MutableSet<String> = mutableSetOf()
    components.forEach { uniqueComponents.add(it) }

    // The passphrase is valid if no component has been eliminated
    if (uniqueComponents.size == components.size) {
      numberOfValidPassphrases += 1
    }
  }

  val result = numberOfValidPassphrases

  return result
}
