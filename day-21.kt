import java.io.File
import kotlin.math.*

typealias Pattern = Array<Int>
typealias Rules = Map<String, Pattern>

fun main(args: Array<String>) {

  val filename = args.firstOrNull() ?: "day-21-input.txt"
  val puzzleInput = loadPuzzleInput(filename)
  println("Input is ${puzzleInput}")

  println("Result is ${solve(puzzleInput)}")
}

fun loadPuzzleInput(filename: String): List<String> {

  val puzzleInput = File(filename).readLines()

  return puzzleInput
}

fun solve(puzzleInput: List<String>): Int {

  val enhancementRules = parseEnhancementRulesFrom(puzzleInput)
  var pattern = arrayOf(0, 1, 0, 0, 0, 1, 1, 1, 1)

  for (i in 1..5) {
    pattern = performIteration(pattern, enhancementRules)
  }

  val numberOfOneBits = pattern.filter { it == 1 }.size
  val result = numberOfOneBits

  return result
}

fun performIteration(pattern: Pattern, rules: Rules): Pattern {

  val enhancedPatterns = subdivide(pattern).map { enhance(it, rules) }
  val newPattern = stitch(enhancedPatterns)

  return newPattern
}

fun subdivide(pattern: Pattern): List<Pattern> {

  val result = mutableListOf<Array<Int>>()

  val rowLength = sizeOfPattern(pattern)
  val subPatternSize = if (rowLength % 2 == 0) 2 else 3
  val numberOfSubPatternsPerRow = rowLength / subPatternSize
  val numberOfSubPatterns = numberOfSubPatternsPerRow * numberOfSubPatternsPerRow

  println("Subdividing pattern ${pattern.joinToString("")} into ${numberOfSubPatterns} patterns of size ${subPatternSize}")

  var index = 0
  while (true) {
    if (index >= pattern.size) break

    val indices = subdivisionIndices(subPatternSize, rowLength).map { index + it }
    val subPattern = pattern.sliceArray(indices)
    result.add(subPattern)

    println("Created sub pattern ${subPattern.joinToString("")}")

    index += subPatternSize
    if (index % rowLength == 0 && index % (rowLength * subPatternSize) != 0) {
      index += (subPatternSize - 1) * rowLength
    }
  }

  return result
}

fun subdivisionIndices(subPatternSize: Int, rowLength: Int): Array<Int> {

  when (subPatternSize) {
    2 -> return subdivisionIndices2(rowLength)
    3 -> return subdivisionIndices3(rowLength)
    else -> throw IllegalArgumentException()
  }
}

fun subdivisionIndices2(rowLength: Int): Array<Int> {

  return arrayOf(0, 1, rowLength, rowLength + 1)
}

fun subdivisionIndices3(rowLength: Int): Array<Int> {

  val doubleRowLength = 2 * rowLength
  return arrayOf(0, 1, 2, rowLength, rowLength + 1, rowLength + 2, doubleRowLength, doubleRowLength + 1, doubleRowLength + 2)
}

fun enhance(pattern: Pattern, rules: Rules): Pattern {

  val patternString = pattern.joinToString("")
  val enhancedPattern = rules.get(patternString)!!

  println("Enhancing pattern ${pattern.joinToString("")} to ${enhancedPattern.joinToString("")}")

  return enhancedPattern
}

fun stitch(patterns: List<Pattern>): Pattern {

  val subPatternSize = sizeOfPattern(patterns.first())
  val subPatternsPerRow = sqrt(patterns.size.toFloat()).toInt()
  val rowLength = subPatternSize * subPatternsPerRow

  val arrayCapacity = patterns.size * patterns.first().size
  val stitchedPattern = Array(arrayCapacity, {0})

  println("Stitching ${patterns.size} patterns of size ${subPatternSize} to pattern with ${arrayCapacity} entries")

  for (i in 0 until patterns.size) {
    val y = i / subPatternsPerRow
    val x = i - subPatternsPerRow * y
    val index = x * subPatternSize + y * subPatternSize * rowLength
    val indices = stitchIndices(subPatternSize, rowLength).map { index + it }

    val pattern = patterns.get(i)
    for (srcIndex in 0 until indices.size) {
      val dstIndex = indices.get(srcIndex)
      println("Copying source ${pattern[srcIndex]} at index ${srcIndex} to index ${dstIndex}")
      stitchedPattern[dstIndex] = pattern[srcIndex]
    }
  }

  return stitchedPattern
}

fun stitchIndices(subPatternSize: Int, rowLength: Int): Array<Int> {

  val arrayCapacity = subPatternSize * subPatternSize
  return Array(arrayCapacity, {
    val y = it / subPatternSize
    val x = it - subPatternSize * y
    x + y * rowLength
  })
}

// --- Transform patterns

fun rotate(pattern: Pattern): Pattern {

  when (sizeOfPattern(pattern)) {
    2 -> return rotate2(pattern)
    3 -> return rotate3(pattern)
    else -> throw IllegalArgumentException()
  }
}

fun rotate2(pattern: Pattern): Pattern {

  // 12 34 -> 24 13
  return Array(pattern.size, {
    when (it) {
      0 -> pattern[1]
      1 -> pattern[3]
      2 -> pattern[0]
      3 -> pattern[2]
      else -> throw IllegalArgumentException()
    }
  })
}

fun rotate3(pattern: Pattern): Pattern {

  // 123 456 789 -> 369 258 147
  return Array(pattern.size, {
    when (it) {
      0 -> pattern[2]
      1 -> pattern[5]
      2 -> pattern[8]
      3 -> pattern[1]
      4 -> pattern[4]
      5 -> pattern[7]
      6 -> pattern[0]
      7 -> pattern[3]
      8 -> pattern[6]
      else -> throw IllegalArgumentException()
    }
  })
}

fun flipX(pattern: Pattern): Pattern {

  when (sizeOfPattern(pattern)) {
    2 -> return flipX2(pattern)
    3 -> return flipX3(pattern)
    else -> throw IllegalArgumentException()
  }
}

fun flipX2(pattern: Pattern): Pattern {

  // 12 34 -> 21 43
  return Array(pattern.size, {
    when (it) {
      0 -> pattern[1]
      1 -> pattern[0]
      2 -> pattern[3]
      3 -> pattern[2]
      else -> throw IllegalArgumentException()
    }
  })
}

fun flipX3(pattern: Pattern): Pattern {

  // 123 456 789 -> 321 654 987
  return Array(pattern.size, {
    when (it) {
      0 -> pattern[2]
      1 -> pattern[1]
      2 -> pattern[0]
      3 -> pattern[5]
      4 -> pattern[4]
      5 -> pattern[3]
      6 -> pattern[8]
      7 -> pattern[7]
      8 -> pattern[6]
      else -> throw IllegalArgumentException()
    }
  })
}

fun flipY(pattern: Pattern): Pattern {

  when (sizeOfPattern(pattern)) {
    2 -> return flipY2(pattern)
    3 -> return flipY3(pattern)
    else -> throw IllegalArgumentException()
  }
}

fun flipY2(pattern: Pattern): Pattern {

  // 12 34 -> 34 12
  return Array(pattern.size, {
    when (it) {
      0 -> pattern[2]
      1 -> pattern[3]
      2 -> pattern[0]
      3 -> pattern[1]
      else -> throw IllegalArgumentException()
    }
  })
}

fun flipY3(pattern: Pattern): Pattern {

  // 123 456 789 -> 789 456 123
  return Array(pattern.size, {
    when (it) {
      0 -> pattern[6]
      1 -> pattern[7]
      2 -> pattern[8]
      3 -> pattern[3]
      4 -> pattern[4]
      5 -> pattern[5]
      6 -> pattern[0]
      7 -> pattern[1]
      8 -> pattern[2]
      else -> throw IllegalArgumentException()
    }
  })
}

fun sizeOfPattern(pattern: Pattern): Int {

  return when (pattern.size) {
    4 -> 2
    9 -> 3
    else -> sqrt(pattern.size.toFloat()).toInt()
  }
}

// --- Load enhancement rules

fun parseEnhancementRulesFrom(strings: List<String>): Rules {

  val rules = strings.map { parseEnhancementRuleFrom(it) }

  var result = mutableMapOf<String, Pattern>()
  rules.forEach {
    val enhancedPattern = it.second
    allVariantsOfPattern(it.first).forEach {
      val string = it.joinToString("")
      if (!result.contains(string)) {
        result.put(string, enhancedPattern)
      }
    }
  }

  return result
}

fun allVariantsOfPattern(pattern: Pattern): Array<Pattern> {

  var result = mutableSetOf<Pattern>()
  result.add(pattern)
  result.add(flipX(pattern))
  result.add(flipY(pattern))

  val rotatedOnce = rotate(pattern)
  result.add(rotatedOnce)
  result.add(flipX(rotatedOnce))
  result.add(flipY(rotatedOnce))

  val rotatedTwice = rotate(rotatedOnce)
  result.add(rotatedTwice)
  result.add(flipX(rotatedTwice))
  result.add(flipY(rotatedTwice))

  val rotatedThrice = rotate(rotatedTwice)
  result.add(rotatedThrice)
  result.add(flipX(rotatedThrice))
  result.add(flipY(rotatedThrice))

  return result.toTypedArray()
}

fun parseEnhancementRuleFrom(string: String): Pair<Pattern, Pattern> {

  val regex = Regex("([\\.#/]+)")
  val patterns = regex.findAll(string).toList().map { parsePatternFrom(it.value) }

  val result = Pair(patterns.get(0), patterns.get(1))

  println("Parsed rule ${string} to ${patterns.get(0).joinToString("")} -> ${patterns.get(1).joinToString("")}")

  return result
}

fun parsePatternFrom(string: String): Pattern {

  val regex = Regex("[\\.#]")
  val symbols = regex.findAll(string).toList().map {
    when (it.value.first()) {
      '#' -> 1
      '.' -> 0
      else -> throw IllegalArgumentException()
    }
  }

  val result = symbols.toTypedArray()

  return result
}
