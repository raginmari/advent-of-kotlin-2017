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

fun solve(puzzleInput: List<String>): Long {

  val instructions = puzzleInput.map { instructionFrom(it) }
  val program = Program(instructions)
  val device = Device()

  program.run(device)

  val result = device.lastPlayedFrequency

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

// A device has a set of registers and tracks the most recently played frequency
class Device {

  private var registers = mutableMapOf<Char, Long>()
  var lastPlayedFrequency = Long.MAX_VALUE

  fun setRegister(name: Char, value: Long) {
    registers.put(name, value)
  }

  fun getRegister(name: Char): Long {
    return registers.get(name) ?: 0
  }
}

// A program has a number of instructions and keeps track of the current instruction index.
class Program(val instructions: List<Instruction>) {

  var instructionIndex = 0

  fun run(device: Device) {

    while (true) {
      // Instructions update the instruction index
      val nextInstruction = instructions.get(instructionIndex)
      nextInstruction.execute(device, this)

      if (instructionIndex >= instructions.size || instructionIndex < 0) {
        // Terminate when the program steps out of bounds
        break
      }
    }
  }
}

// Enum instruction parameter
sealed class Parameter {

  // A number parameter. Its value is the number itself
  class Number(val value: Long): Parameter() {

    override fun value(device: Device): Long {
      return value
    }
  }

  // A register parameter. Its value is the register value
  class Register(val name: Char): Parameter() {

    override fun value(device: Device): Long {
      return device.getRegister(name)
    }

    override fun charValue(): Char {
      return name
    }
  }

  abstract fun value(device: Device): Long

  open fun charValue(): Char { throw UnsupportedOperationException() }
}

// Enum instruction
sealed class Instruction {

  class Snd(val frequency: Parameter): Instruction() {

    override fun execute(device: Device, program: Program) {

      println("Playing frequency ${frequency.value(device)}")
      device.lastPlayedFrequency = frequency.value(device)
      program.instructionIndex += 1
    }
  }

  class Set(val destination: Parameter, val value: Parameter): Instruction() {

    override fun execute(device: Device, program: Program) {

      println("Setting register ${destination.charValue()} to ${value.value(device)}")
      device.setRegister(destination.charValue(), value.value(device))
      program.instructionIndex += 1
    }
  }

  class Add(val destination: Parameter, val increment: Parameter): Instruction() {

    override fun execute(device: Device, program: Program) {

      val registerName = destination.charValue()
      val sum = device.getRegister(registerName) + increment.value(device)
      println("Adding ${increment.value(device)} to register ${registerName} (new value is ${sum})")
      device.setRegister(registerName, sum)
      program.instructionIndex += 1
    }
  }

  class Mul(val destination: Parameter, val factor: Parameter): Instruction() {

    override fun execute(device: Device, program: Program) {

      val registerName = destination.charValue()
      val product = device.getRegister(registerName) * factor.value(device)
      println("Multiplying register ${registerName} by ${factor.value(device)} (new value is ${product})")
      device.setRegister(registerName, product)
      program.instructionIndex += 1
    }
  }

  class Mod(val destination: Parameter, val mod: Parameter): Instruction() {

    override fun execute(device: Device, program: Program) {

      val registerName = destination.charValue()
      val remainder = device.getRegister(registerName) % mod.value(device)
      println("Modulo register ${registerName} by ${mod.value(device)} (new value is ${remainder})")
      device.setRegister(registerName, remainder)
      program.instructionIndex += 1
    }
  }

  class Rcv(val check: Parameter): Instruction() {

    override fun execute(device: Device, program: Program) {
      if (check.value(device) != 0L) {
        println("Recovering frequency ${device.lastPlayedFrequency}")
        program.instructionIndex = Int.MAX_VALUE // Terminate the program
      } else {
        println("Skipping recovery...")
        program.instructionIndex += 1
      }
    }
  }

  class Jgz(val check: Parameter, val offset: Parameter): Instruction() {

    override fun execute(device: Device, program: Program) {

      if (check.value(device) > 0L) {
        val index = program.instructionIndex + offset.value(device).toInt()
        println("Jumping to instruction ${index}")
        program.instructionIndex = index
      } else {
        println("Skipping jump...")
        program.instructionIndex += 1
      }
    }
  }

  abstract fun execute(device: Device, program: Program)
}
