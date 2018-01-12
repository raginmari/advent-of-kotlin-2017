import java.io.File

typealias Node = Byte
typealias Position = Pair<Int, Int>

fun main(args: Array<String>) {

  val filename = args.firstOrNull() ?: "day-22-input.txt"
  val puzzleInput = loadPuzzleInput(filename)
  println("Input is ${puzzleInput}")

  val numberOfMoves = args.getOrNull(1)?.toInt() ?: 10000

  println("Result is ${solve(puzzleInput, numberOfMoves)}")
}

fun solve(puzzleInput: PuzzleInput, numberOfMoves: Int): Int {

  val grid = gridFromPuzzleInput(puzzleInput).toMutableMap()

  val center = center()
  val virus = Virus(Direction.UP, center.first, center.second)

  var numberOfInfections = 0
  for (i in 0 until numberOfMoves) {
    numberOfInfections += if (moveOnce(virus, grid)) 1 else 0
  }

  val result = numberOfInfections

  return result
}

fun moveOnce(virus: Virus, grid: MutableMap<Position, Node>): Boolean {
  val currentPosition = Pair(virus.x, virus.y)
  val currentNode = grid.get(currentPosition) ?: 0

  val didInfect: Boolean
  when (currentNode.toInt()) {
    0 -> {
      virus.turnLeft()
      grid.put(currentPosition, 1)
      didInfect = true
    }
    1 -> {
      virus.turnRight()
      grid.put(currentPosition, 0)
      didInfect = false
    }
    else -> throw IllegalArgumentException()
  }

  virus.moveForward()

  return didInfect
}

fun gridFromPuzzleInput(puzzleInput: PuzzleInput): Map<Position, Node> {

  val grid = mutableMapOf<Position, Node>()
  val x0 = (center().first - puzzleInput.width / 2)
  val x1 = x0 + puzzleInput.width

  var x = x0
  var y = x0
  for (node in puzzleInput.nodes) {
    val coordinate = Pair(x, y)
    grid.put(coordinate, node)

    x += 1
    if (x == x1) {
      y += 1
      x = x0
    }
  }

  return grid
}

fun center(): Position {

  return Pair(5000, 5000)
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

class Virus(var direction: Direction, var x: Int, var y: Int) {

  fun turnLeft() {
    this.direction = this.direction.left()
  }

  fun turnRight() {
    this.direction = this.direction.left().opposite()
  }

  fun moveForward() {
    this.x += this.direction.dx
    this.y += this.direction.dy
  }
}

data class PuzzleInput(val width: Int, val nodes: List<Node>)

fun loadPuzzleInput(filename: String): PuzzleInput {

  val lines = File(filename).readLines()

  var width: Int? = null
  val allNodes = mutableListOf<Node>()
  for (line in lines) {
    val nodes = parseNodesFrom(line)
    width = width ?: nodes.size
    allNodes.addAll(nodes)
  }

  val puzzleInput = PuzzleInput(width!!, allNodes)

  return puzzleInput
}

fun parseNodesFrom(string: String): List<Node> {

  val result = string.asIterable().map {
    when (it) {
      '#' -> 1.toByte()
      '.' -> 0.toByte()
      else -> throw IllegalArgumentException()
    }
  }

  return result
}
