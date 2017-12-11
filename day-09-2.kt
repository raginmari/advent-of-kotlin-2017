import java.io.File
import java.util.Stack

fun main(args: Array<String>) {

  /*val input: String = args.firstOrNull() ?: "{}"*/
  val input: String = args.firstOrNull() ?: File("day-09-input.txt").readLines().first() ?: "{}"

  println("Result is ${solve(input)}")
}

fun solve(input: String): Int {

  var state = State()
  var nonCancelledGarbageSymbols = 0
  var skipNext = false
  for (i in input.indices) {
    val symbol = input.get(i)

    // Skip any oen character if skipNext is set
    if (skipNext) {
      skipNext = false
      continue
    }

    if (state.mode() == Mode.GARBAGE && symbol != '>' && symbol != '!') {
      nonCancelledGarbageSymbols += 1
    }

    when (symbol) {
      '{' -> state.mode().enterGroup(state)
      '}' -> state.mode().leaveGroup(state)
      '!' -> skipNext = true
      '<' -> state.mode().enterGarbage(state)
      '>' -> state.mode().leaveGarbage(state)
      else -> {}
    }
  }

  val result = nonCancelledGarbageSymbols

  return result
}

class State {

  var modeStack: Stack<Mode>

  init {
    modeStack = Stack()
    modeStack.push(Mode.END)
    modeStack.push(Mode.START)
  }

  fun mode(): Mode {
    return modeStack.peek()
  }
}

enum class Mode {

  START {
    override fun start(state: State) {}
    override fun enterGroup(state: State) {
      state.modeStack.pop()
      Mode.enterMode(state, Mode.GROUP)
    }
    override fun leaveGroup(state: State) {}
    override fun enterGarbage(state: State) {}
    override fun leaveGarbage(state: State) {}
  },

  GROUP {
    override fun start(state: State) {}
    override fun enterGroup(state: State) { Mode.enterMode(state, Mode.GROUP) }
    override fun leaveGroup(state: State) {
      Mode.leaveMode(state)
    }
    override fun enterGarbage(state: State) { Mode.enterMode(state, Mode.GARBAGE) }
    override fun leaveGarbage(state: State) {}
  },

  GARBAGE {
    override fun start(state: State) {}
    override fun enterGroup(state: State) {}
    override fun leaveGroup(state: State) {}
    override fun enterGarbage(state: State) {}
    override fun leaveGarbage(state: State) { Mode.leaveMode(state) }
  },

  END {
    override fun start(state: State) {}
    override fun enterGroup(state: State) {}
    override fun leaveGroup(state: State) {}
    override fun enterGarbage(state: State) {}
    override fun leaveGarbage(state: State) {}
  };

  // Use companion object declaration to implement static methods
  companion object {

    fun enterMode(state: State, mode: Mode) {
      state.modeStack.push(mode)
      mode.start(state)
    }

    fun leaveMode(state: State) {
      state.modeStack.pop()
    }
  }

  abstract fun start(state: State)
  abstract fun enterGroup(state: State)
  abstract fun leaveGroup(state: State)
  abstract fun enterGarbage(state: State)
  abstract fun leaveGarbage(state: State)
}
