import java.io.File
import kotlin.text.Regex

fun main(args: Array<String>) {

  /*val input = File("day-07-input-test.txt").readLines()*/
  val input = File("day-07-input.txt").readLines()

  println("Result is ${solve(input)}")
}

fun solve(input: List<String>): Int {

  // Matches a program name and an optional weight
  val regex = Regex("([a-z]+)(?:\\D*([0-9]+))?")

  // Keeps track of all programs and provides efficient lookup by name
  var programs = mutableMapOf<String, Program>()

  for (line in input) {
    // Parse program names from line
    val matches = regex.findAll(line).toList()

    var pairs = mutableListOf<Pair<String, Int>>()
    for (match in matches) {
      // Fetch the program name
      val name = match.groupValues.get(1) // 0 is the entire match!

      // Fetch the optional program weight (set only if the program is the first in the line)
      val weightString = match.groupValues.last()
      val weight = if (weightString != "") { weightString.toInt() } else { 0 }

      pairs.add(Pair(name, weight))
    }

    var tower = mutableListOf<Program>()
    for (pair in pairs) {
      val name = pair.first

      var program = programs.get(name) // Use existing programs if possible
      if (program != null) {
        // Update the weight: overwrites 0 if the program was created without a weight
        // (i.e. if it was first encountered as part of a tower vs. the root of a tower)
        program.weight = maxOf(program.weight, pair.second)
      } else {
        program = Program(name, pair.second)
      }
      tower.add(program)

      programs.put(name, tower.last())
    }

    if (tower.size > 1) {
      var root = tower.get(0)
      root.children = tower.subList(1, tower.size)
    }

    // None of the children of the root of this tower can be the root of the tree
    tower.subList(1, tower.size).forEach { it.isRoot = false }
  }

  // Compute the subtree weights for all programs
  val root = programs.values.find { it.isRoot }!!
  computeWeightOfSubtreeOfProgram(root)
  computeDepthOfProgram(root)

  // Find the one program which is not balanced:
  // - find those programs whose subtrees have different weights (there can be more than one of those!!!)
  // - find the candidate located deepest in the tree (whose error "bubbles" up to its supertrees)
  val unbalancedProgram = programs.values.filter { !it.children.isEmpty() }.filter {
    val value = it.children.get(0).weightOfSubtree
    val allSubtreesWeighTheSame = it.children.fold(true) { total, next ->
      total && next.weightOfSubtree == value
    }
    !allSubtreesWeighTheSame
  }.maxBy { it.depth }

  // Find the fucking result already!
  val weightToBalanceTree: Int
  val unbalancedSubtreeWeights = unbalancedProgram!!.children.map { it.weightOfSubtree }
  val minSubtreeWeight = unbalancedSubtreeWeights.min()!!
  val maxSubtreeWeight = unbalancedSubtreeWeights.max()!!
  // Check if the min weight is the exception or the max:
  // "A A A b" (b being the minimum) or "a a a B" (B being the maximum)
  if (unbalancedSubtreeWeights.filter { it == minSubtreeWeight }.size > 1) {
    // The max value is the exception and must be reduced
    val subtreeWeightIndex = unbalancedSubtreeWeights.indexOf(maxSubtreeWeight)
    weightToBalanceTree = unbalancedProgram.children.get(subtreeWeightIndex).weight - (maxSubtreeWeight - minSubtreeWeight)
  } else {
    // The min value is the exception and must be increased
    val subtreeWeightIndex = unbalancedSubtreeWeights.indexOf(minSubtreeWeight)
    weightToBalanceTree = unbalancedProgram.children.get(subtreeWeightIndex).weight + (maxSubtreeWeight - minSubtreeWeight)
  }

  val result = weightToBalanceTree

  return result
}

// Recursively computes and sets the subtree weight of the given program.
// The subtree weight is the weight of the given program + the subtree weights of all of its children
fun computeWeightOfSubtreeOfProgram(program: Program): Int {

  val weight = if (program.children.isEmpty()) {
    program.weight
  } else {
    program.weight + program.children.fold(0) { total, next -> total + computeWeightOfSubtreeOfProgram(next) }
  }

  // Save the value in the program
  program.weightOfSubtree = weight

  return weight
}

// Recursively sets the depth of the given program in the tree.
fun computeDepthOfProgram(program: Program, depth: Int = 0) {

  program.depth = depth
  program.children.forEach { computeDepthOfProgram(it, depth + 1) } // Children are one level deeper in the tree
}

data class Program(val name: String, var weight: Int) {
  var children = mutableListOf<Program>()
  var depth = 0
  var weightOfSubtree = 0
  var isRoot = true
}
