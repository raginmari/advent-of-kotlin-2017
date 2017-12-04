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

    // Convert each passphrase to a map of characters to the number of occurrences of the character in the passphrase (buckets).
    // Add the map to a set eliminating duplicates (i.e. all anagrams of the passphrase!)
    var uniqueComponents: MutableSet<Map<Char, Int>> = mutableSetOf()
    components.forEach {
      var buckets = mutableMapOf<Char, Int>()

      for (i in it.indices) {
        val key = it.get(i)
        val count = buckets.get(key) ?: 0 // Add to existing value or start at 0
        buckets.put(key, count + 1)
      }

      uniqueComponents.add(buckets)
    }

    if (components.size == uniqueComponents.size) {
      numberOfValidPassphrases += 1
    }
  }

  val result = numberOfValidPassphrases

  return result
}
