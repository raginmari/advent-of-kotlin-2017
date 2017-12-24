import java.io.File
import kotlin.math.*

fun main(args: Array<String>) {

  val input = inputFrom(args.firstOrNull()) ?: loadPuzzleInput()
  println("Input is ${input}")

  println("Result is ${solve(input)}")
}

fun inputFrom(string: String?): Int? {

  if (string == null) return null;
  val input = string.toInt()

  return input
}

fun loadPuzzleInput(): Int {

  return 394
}

fun solve(steps: Int): Int {

  var head = BufferItem(0, null)
  head.next = head

  var nextValue = 1
  for (i in 1..2017) {
    head = walk(head, steps)

    insert(head, nextValue)
    nextValue += 1

    head = head.next!!
  }

  val valueAfter2017 = find(head, 2017)!!.next!!.value

  val result = valueAfter2017

  return result
}

fun walk(item: BufferItem, steps: Int): BufferItem {

  var head = item
  for (i in 1..steps) {
    head = head.next!!
  }

  return head
}

fun insert(item: BufferItem, value: Int) {

  val insertedItem = BufferItem(value, item.next!!)
  item.next = insertedItem
}

fun find(item: BufferItem, value: Int): BufferItem? {

  var current = item
  do {
    if (current.value == value) return current
    current = item.next!!
  } while (current != item)

  return null
}

data class BufferItem(val value: Int, var next: BufferItem?)
