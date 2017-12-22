import java.io.File
import kotlin.math.*

fun main(args: Array<String>) {

  val input = inputFrom(args.firstOrNull()) ?: loadPuzzleInput()

  println("Result is ${solve(input)}")
}

fun inputFrom(string: String?): List<Command>? {

  if (string == null) return null;
  val input = string.split(",").mapNotNull { commandFrom(it) }

  return input
}

fun loadPuzzleInput(): List<Command> {

  val line = File("day-16-input.txt").readLines().first()
  val puzzleInput = line.split(",").mapNotNull { commandFrom(it) }

  return puzzleInput
}

fun solve(input: List<Command>): String {

  val programs = Array(16, { ('a'.toInt() + it).toChar() })

  val rehearsalStage = Stage(programs)
  val configurations = mutableSetOf<String>(rehearsalStage.programsAsString())
  while (true) {
    for (command in input) {
      command.execute(rehearsalStage)
    }

    val config = rehearsalStage.programsAsString()
    if (configurations.contains(config)) {
      break
    }

    configurations.add(config)
  }

  val loopLength = configurations.size
  val iterationsCount = 1000000000 % loopLength
  println("Repeats after ${loopLength} iterations => number of iterations to make is ${iterationsCount}")

  val stage = Stage(programs)
  for (i in 1..iterationsCount) {
    for (command in input) {
      command.execute(stage)
    }
  }

  val result = stage.programsAsString()

  return result
}

fun commandFrom(string: String): Command? {

  when (string.first()) {
    's' -> return spinCommandFrom(string)
    'x' -> return exchangeCommandFrom(string)
    'p' -> return partnerCommandFrom(string)
    else -> return null
  }
}

fun spinCommandFrom(string: String): Command {

  val param = string.substring(1)
  val count = param.toInt()
  val command = Command.Spin(count)

  return command
}

fun exchangeCommandFrom(string: String): Command {

  val paramsString = string.substring(1)
  val params = paramsString.split("/")
  val indexA = params[0].toInt()
  val indexB = params[1].toInt()
  val command = Command.Exchange(indexA, indexB)

  return command
}

fun partnerCommandFrom(string: String): Command {

  val paramsString = string.substring(1)
  val params = paramsString.split("/")
  val nameA = params[0].first()
  val nameB = params[1].first()
  val command = Command.Partner(nameA, nameB)

  return command
}

sealed class Command {

  data class Spin(val count: Int): Command() {

    override fun execute(stage: Stage) {

      for (i in 1..count) {
        stage.spin()
      }
    }
  }

  data class Exchange(val indexA: Int, val indexB: Int): Command() {

    override fun execute(stage: Stage) {

      stage.exchange(indexA, indexB)
    }
  }

  data class Partner(val nameA: Char, val nameB: Char): Command() {

    override fun execute(stage: Stage) {

      stage.partner(nameA, nameB)
    }
  }

  abstract fun execute(stage: Stage)
}

class Stage(val programs: Array<Char>) {

  var head: Int = 0

  fun spin() {

    head = (head + programs.size - 1) % programs.size
  }

  fun exchange(indexA: Int, indexB: Int) {

    val indexFromHeadA = indexFromHead(indexA)
    val indexFromHeadB = indexFromHead(indexB)
    val tmp = programs[indexFromHeadA]
    programs[indexFromHeadA] = programs[indexFromHeadB]
    programs[indexFromHeadB] = tmp
  }

  fun partner(nameA: Char, nameB: Char) {

    val indexA = programs.indexOf(nameA)
    val indexB = programs.indexOf(nameB)
    val tmp = programs[indexA]
    programs[indexA] = programs[indexB]
    programs[indexB] = tmp
  }

  fun indexFromHead(index: Int): Int = (head + index) % programs.size

  fun programsAsString(): String {

    var string = ""
    for (i in 0 until programs.size) {
      val index = indexFromHead(i)
      string += programs[index]
    }

    val result = string

    return result
  }
}
