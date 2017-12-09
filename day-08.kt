import java.io.File
import kotlin.text.Regex

fun main(args: Array<String>) {

  /*val input = File("day-08-input-test.txt").readLines()*/
  val input = File("day-08-input.txt").readLines()

  println("Result is ${solve(input)}")
}

fun solve(input: List<String>): Int {

  var registers = mutableMapOf<String, Int>()
  val instructions = input.map { parseInstruction(it) }

  instructions.forEach { it.execute(registers) }
  val maxRegisterValue = registers.values.toList().max()!!

  val result = maxRegisterValue

  return result
}

fun parseInstruction(string: String): Instruction {

  // Each line is expected to have seven components, no more, no less
  val components = string.split(" ").toList()

  val registerName = components.get(0)
  val increment = parseIncrement(components.subList(1, 1 + 2)) // Parse components 1, 2
  val condition = parseCondition(components.subList(4, 4 + 3)) // Parse components 4, 5, 6

  val instruction = Instruction(registerName, increment, condition)

  return instruction
}

fun parseIncrement(components: List<String>): Int {

  val name = components.get(0)
  val value = components.get(1).toInt()

  val increment: Int
  when (name) {
    "inc" -> increment = value
    "dec" -> increment = -value
    else -> {
      increment = 0 // Should not happen
    }
  }

  return increment
}

fun parseCondition(components: List<String>): Condition {

  val registerName = components.get(0)
  val operatorString = components.get(1)
  val comparison = convertToComparison(operatorString)!!
  val constant = components.get(2).toInt()

  val condition = Condition(registerName, comparison, constant)

  return condition
}

fun convertToComparison(operatorString: String): Comparison? {

  when (operatorString) {
    ">" -> return Comparison.GREATER
    ">=" -> return Comparison.GREATER_EQUAL
    "<" -> return Comparison.LESSER
    "<=" -> return Comparison.LESSER_EQUAL
    "==" -> return Comparison.EQUAL
    "!=" -> return Comparison.NOT_EQUAL
    else -> {
      return null
    }
  }
}

data class Instruction(val registerName: String, val increment: Int, val condition: Condition) {

  fun execute(registers: MutableMap<String, Int>) {

    if (condition.evaluate(registers)) {
      val value = registers.get(registerName) ?: 0
      registers.put(registerName, value + increment)
    }
  }
}

enum class Comparison {

  GREATER {
    override fun compare(lhs: Int, rhs: Int): Boolean { return lhs > rhs }
  },
  GREATER_EQUAL {
    override fun compare(lhs: Int, rhs: Int): Boolean { return lhs >= rhs }
  },
  LESSER {
    override fun compare(lhs: Int, rhs: Int): Boolean { return lhs < rhs }
  },
  LESSER_EQUAL {
    override fun compare(lhs: Int, rhs: Int): Boolean { return lhs <= rhs }
  },
  EQUAL {
    override fun compare(lhs: Int, rhs: Int): Boolean { return lhs == rhs }
  },
  NOT_EQUAL {
    override fun compare(lhs: Int, rhs: Int): Boolean { return lhs != rhs }
  };

  abstract fun compare(lhs: Int, rhs: Int): Boolean
}

data class Condition(val registerName: String, val comparison: Comparison, val constant: Int) {

  fun evaluate(registers: Map<String, Int>): Boolean {

    val registerValue = registers.get(registerName) ?: 0
    return comparison.compare(registerValue, constant)
  }
}
