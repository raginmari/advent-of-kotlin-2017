import java.io.File

fun main(args: Array<String>) {

  val lines = File("day-06-input.txt").readLines().first()
  val input = lines.split(" ").map { string -> string.toInt() }
  println("Input is ${input}")

  println("Result is ${solve(input)}")
  /*println("Result is ${solve(listOf(0, 2, 7, 0))}")*/
}

fun solve(input: List<Int>): Int {

  // Keeps track of the configurations of the banks that the algorithm has already encountered and their index in the history.
  var history = mutableMapOf<List<Int>, Int>()

  var step = 0
  var banks = input
  do {
    // Add the configuration and its index to history
    history.put(banks, step)
    step += 1

    // Distribute the contents of the fullest bank
    val fullestBankIndex = indexOfFullestBank(banks)
    banks = distributeBankAtIndex(banks, fullestBankIndex)

  } while (!history.containsKey(banks)) // Run until a configuration is produces a second time

  val indexOfFirstOccurrence = history.get(banks) ?: 0
  val numberOfDistributionsBetweenOccurrences = step - indexOfFirstOccurrence

  val result = numberOfDistributionsBetweenOccurrences

  return result
}

fun indexOfFullestBank(banks: List<Int>): Int {

  // Computes the index of the largest element in the list. Not as efficient as possible but short
  return banks.indexOf(banks.max())
}

fun distributeBankAtIndex(inputBanks: List<Int>, startingIndex: Int): List<Int> {

  var banks = inputBanks.toMutableList()
  val numberOfBanks = banks.size

  // Clear the bank at the given starting index and store its contents for distribution
  val toDistribute = banks.get(startingIndex)
  banks.set(startingIndex, 0)

  // Increment the banks in order starting from the given index and wrapping at the end of the list
  for (i in 1..toDistribute) {
    val index = (startingIndex + i) % numberOfBanks
    val value = banks.get(index) + 1
    banks.set(index, value)
  }

  return banks.toList()
}
