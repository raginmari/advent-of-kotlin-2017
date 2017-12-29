import java.io.File

fun main(args: Array<String>) {

  val filename = args.firstOrNull() ?: "day-19-input.txt"
  val puzzleInput = loadPuzzleInput(filename)

  println("Result is ${solve(puzzleInput)}")
}

fun loadPuzzleInput(filename: String): List<String> {

  val lines = File(filename).readLines().filter { !it.isEmpty() }

  return lines
}

fun solve(puzzleInput: List<String>): Int {

  var path = parsePuzzleInput(puzzleInput)
  val numberOfSteps = walkPath(path)

  val result = numberOfSteps

  return result
}

fun walkPath(path: Map<Pair<Int, Int>, PathItem>): Int {

  var x = path.keys.filter { it.second == 0 }.firstOrNull()?.first ?: throw IllegalStateException()
  var y = 0
  var dir = Direction.DOWN
  var numberOfSteps = 1 // The first one

  while (true) {
    if (walkTo(dir, x, y, path)) {
      println("Walking on")
    } else if (walkTo(dir.left(), x, y, path)) {
      dir = dir.left()
      println("Walking left")
    } else if (walkTo(dir.left().opposite(), x, y, path)) {
      dir = dir.left().opposite()
      println("Walking right")
    } else {
      // Reached the end of the path
      println("Stopping")
      return numberOfSteps
    }

    x = x + dir.dx
    y = y + dir.dy
    numberOfSteps += 1
  }
}

fun walkTo(dir: Direction, x: Int, y: Int, path: Map<Pair<Int, Int>, PathItem>): Boolean {

  val targetX = x + dir.dx
  val targetY = y + dir.dy
  val pathItem = path.get(Pair(targetX, targetY))
  if (pathItem == null) {
    return false
  }

  return true
}

enum class Direction {

  UP {
    init { dy = -1 }
    override fun opposite(): Direction = Direction.DOWN
    override fun left(): Direction = Direction.LEFT
  },

  DOWN {
    init { dy = +1 }
    override fun opposite(): Direction = Direction.UP
    override fun left(): Direction = Direction.RIGHT
  },

  LEFT {
    init { dx = -1 }
    override fun opposite(): Direction = Direction.RIGHT
    override fun left(): Direction = Direction.DOWN
  },

  RIGHT {
    init { dx = +1 }
    override fun opposite(): Direction = Direction.LEFT
    override fun left(): Direction = Direction.UP
  };

  var dx = 0
  var dy = 0

  abstract fun opposite(): Direction
  abstract fun left(): Direction
}

fun parsePuzzleInput(puzzleInput: List<String>): Map<Pair<Int, Int>, PathItem> {

  var result = mutableMapOf<Pair<Int, Int>, PathItem>()

  for (i in 0 until puzzleInput.size) { // i is y position of all items of the line
    // Parse each line of the input
    val line = puzzleInput.get(i)
    val parsedLine = parseLine(line)

    for (element in parsedLine) {
      // Add the parsed path element to the path
      val x = element.first
      val y = i
      val coordinate = Pair(x, y)
      result.put(coordinate, element.second)
    }
  }

  return result.toMap()
}

fun parseLine(line: String): List<Pair<Int, PathItem>> {

  var result = mutableListOf<Pair<Int, PathItem>>()
  for (index in line.indices) {
    val character = line.get(index)
    val pathItem = parsePathItem(character)
    if (pathItem != null) {
      val x = index
      result.add(Pair(x, pathItem))
    }
  }

  return result
}

fun parsePathItem(character: Char): PathItem? {

  if (character == ' ') {
    // No path item
    return null
  }

  if (character == '|' || character == '-' || character == '+') {
    // Path item without letter
    return PathItem(null)
  }

  if (character.category == CharCategory.UPPERCASE_LETTER) {
    // Path item with letter
    return PathItem(character)
  }

  throw IllegalArgumentException()
}

data class PathItem(val letter: Char? = null)
