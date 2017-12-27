import java.io.File
import kotlin.math.*
import kotlin.text.*

fun main(args: Array<String>) {

  val filename = args.firstOrNull() ?: "day-18-input.txt"
  val puzzleInput = loadPuzzleInput(filename)
  println("Input is ${puzzleInput}")

  println("Result is ${solve(puzzleInput)}")
}

fun loadPuzzleInput(filename: String): List<String> {

  val lines = File(filename).readLines()

  return lines
}

fun solve(puzzleInput: List<String>): Int {

  val instructions = puzzleInput.map { instructionFrom(it) }

  val programA = Program(0, instructions)
  val programB = Program(1, instructions)

  while (true) {
    // Each program executes a single instruction
    programA.step(programB.incomingMessageQueue)
    programB.step(programA.incomingMessageQueue)

    if (programA.state == Program.State.WAITING && programB.state == Program.State.WAITING) {
      // Both programs are waiting for input
      println("DEADLOCK")
      programA.state = Program.State.TERMINATED
      programB.state = Program.State.TERMINATED
    }

    if (programA.state == Program.State.TERMINATED && programB.state == Program.State.TERMINATED) {
      // Both programs are finished
      println("FINISHED")
      break;
    }
  }

  val result = programB.numberOfExecutedSendInstructions

  return result
}

// Converst given string to program instruction
fun instructionFrom(string: String): Instruction {

  val components = string.split(" ")
  // Parse parameters
  val parameters = components.subList(1, components.size).map { parameterFrom(it) }

  val type = components.first()
  when (type) {
    "snd" -> return Instruction.Snd(parameters.get(0))
    "set" -> return Instruction.Set(parameters.get(0), parameters.get(1))
    "add" -> return Instruction.Add(parameters.get(0), parameters.get(1))
    "mul" -> return Instruction.Mul(parameters.get(0), parameters.get(1))
    "mod" -> return Instruction.Mod(parameters.get(0), parameters.get(1))
    "rcv" -> return Instruction.Rcv(parameters.get(0))
    "jgz" -> return Instruction.Jgz(parameters.get(0), parameters.get(1))
    else -> throw IllegalArgumentException()
  }
}

// Converst given string to program instruction parameter
fun parameterFrom(string: String): Parameter {

  val result: Parameter

  if (string.first().category == CharCategory.LOWERCASE_LETTER) {
    // Make a register name parameter
    val registerName = string.first()
    result = Parameter.Register(registerName)
  } else {
    // (Try and) make a number parameter
    val number = string.toLong()
    result = Parameter.Number(number)
  }

  return result
}

// A program has a number of instructions and keeps track of the current instruction index.
class Program(val programID: Long, val instructions: List<Instruction>) {

  enum class State {
    RUNNING, WAITING, TERMINATED
  }

  private var registers = mutableMapOf<Char, Long>()

  var state = State.RUNNING
  var instructionIndex = 0
  var incomingMessageQueue = mutableListOf<Long>()
  var numberOfExecutedSendInstructions = 0

  init {
    setRegister('p', programID)
  }

  fun step(otherMessageQueue: MutableList<Long>) {

    if (state == State.TERMINATED) throw IllegalStateException()

    val nextInstruction = instructions.get(instructionIndex)
    nextInstruction.execute(this, otherMessageQueue)

    if (instructionIndex < 0 || instructionIndex >= instructions.size) {
      // The instruction index is out of bounds
      state = State.TERMINATED
    }
  }

  fun setRegister(name: Char, value: Long) {
    registers.put(name, value)
  }

  fun getRegister(name: Char): Long {
    return registers.get(name) ?: 0
  }

  fun send(value: Long, queue: MutableList<Long>) {
    queue.add(value)
    numberOfExecutedSendInstructions += 1
  }
}

// Enum instruction parameter
sealed class Parameter {

  // A number parameter. Its value is the number itself
  class Number(val value: Long): Parameter() {

    override fun value(program: Program): Long {
      return value
    }
  }

  // A register parameter. Its value is the register value
  class Register(val name: Char): Parameter() {

    override fun value(program: Program): Long {
      return program.getRegister(name)
    }

    override fun charValue(): Char {
      return name
    }
  }

  abstract fun value(program: Program): Long

  open fun charValue(): Char { throw UnsupportedOperationException() }
}

// Enum instruction
sealed class Instruction {

  class Snd(val value: Parameter): Instruction() {

    override fun execute(program: Program, outgoingMessageQueue: MutableList<Long>) {

      // Adds a value to the given outgoing message queue
      val valueToSend = value.value(program)
      program.send(valueToSend, outgoingMessageQueue)
      println("${program.programID} sending ${valueToSend} (${program.numberOfExecutedSendInstructions})")

      program.instructionIndex += 1
    }
  }

  class Set(val destination: Parameter, val value: Parameter): Instruction() {

    override fun execute(program: Program, outgoingMessageQueue: MutableList<Long>) {

      println("${program.programID} setting register ${destination.charValue()} to ${value.value(program)}")
      program.setRegister(destination.charValue(), value.value(program))

      program.instructionIndex += 1
    }
  }

  class Add(val destination: Parameter, val increment: Parameter): Instruction() {

    override fun execute(program: Program, outgoingMessageQueue: MutableList<Long>) {

      val registerName = destination.charValue()
      val sum = program.getRegister(registerName) + increment.value(program)
      println("${program.programID} adding ${increment.value(program)} to register ${registerName} (new value is ${sum})")
      program.setRegister(registerName, sum)

      program.instructionIndex += 1
    }
  }

  class Mul(val destination: Parameter, val factor: Parameter): Instruction() {

    override fun execute(program: Program, outgoingMessageQueue: MutableList<Long>) {

      val registerName = destination.charValue()
      val product = program.getRegister(registerName) * factor.value(program)
      println("${program.programID} multiplying register ${registerName} by ${factor.value(program)} (new value is ${product})")
      program.setRegister(registerName, product)

      program.instructionIndex += 1
    }
  }

  class Mod(val destination: Parameter, val mod: Parameter): Instruction() {

    override fun execute(program: Program, outgoingMessageQueue: MutableList<Long>) {

      val registerName = destination.charValue()
      val remainder = program.getRegister(registerName) % mod.value(program)
      println("${program.programID} modulo register ${registerName} by ${mod.value(program)} (new value is ${remainder})")
      program.setRegister(registerName, remainder)

      program.instructionIndex += 1
    }
  }

  class Rcv(val destination: Parameter): Instruction() {

    override fun execute(program: Program, outgoingMessageQueue: MutableList<Long>) {

      if (!program.incomingMessageQueue.isEmpty()) {
        // Removes the first value from the incoming message queue and stores it in a register
        val registerName = destination.charValue()
        val receivedValue = program.incomingMessageQueue.first()
        println("${program.programID} received value ${receivedValue} and stored it in ${registerName}")
        program.incomingMessageQueue.removeAt(0)
        program.setRegister(registerName, receivedValue)

        // Continue running (may be in state WAITING)
        program.state = Program.State.RUNNING

        program.instructionIndex += 1
      } else {
        // Cannot receive = wait for input
        program.state = Program.State.WAITING
      }
    }
  }

  class Jgz(val check: Parameter, val offset: Parameter): Instruction() {

    override fun execute(program: Program, outgoingMessageQueue: MutableList<Long>) {

      if (check.value(program) > 0L) {
        val index = program.instructionIndex + offset.value(program).toInt()
        println("${program.programID} jumping to instruction ${index}")
        program.instructionIndex = index
      } else {
        println("${program.programID} skipping jump...")
        program.instructionIndex += 1
      }
    }
  }

  abstract fun execute(program: Program, outgoingMessageQueue: MutableList<Long>)
}
