import java.io.File
import kotlin.math.*

// Too elaborate! Solution is found by determining the particle with the lowest acceleration.
// Learned about operator overloading, companion objects, regex (again) though :)

fun main(args: Array<String>) {

  val filename = args.firstOrNull() ?: "day-20-input.txt"
  val puzzleInput = loadPuzzleInput(filename)
  
  println("Result is ${solve(puzzleInput)}")
}

fun loadPuzzleInput(filename: String): List<String> {

  val lines = File(filename).readLines()
  val puzzleInput = lines

  return puzzleInput
}

fun solve(puzzleInput: List<String>): Int {

  val particles = puzzleInput.map { particleFrom(it) }
  println("Particles: ${particles}")

  val lowestAccParticleIndex = particles.indexOf(particles.minBy { it.acc.manhatten() })

  val result = lowestAccParticleIndex

  return result
}

fun particleFrom(string: String): Particle {

  val regexMatchingTriple = Regex("(-?\\d+,-?\\d+,-?\\d+)")
  val matchedTriples = regexMatchingTriple.findAll(string).toList().map { it.value }
  if (matchedTriples.size != 3) throw IllegalArgumentException()

  val pos = vecFrom(matchedTriples.get(0))
  val vel = vecFrom(matchedTriples.get(1))
  val acc = vecFrom(matchedTriples.get(2))
  val particle = Particle(pos, vel, acc)

  return particle
}

fun vecFrom(string: String): Vec3 {

  val components = string.split(",")
  if (components.size != 3) throw IllegalArgumentException()

  val x = components.get(0).toInt()
  val y = components.get(1).toInt()
  val z = components.get(2).toInt()
  val vec = Vec3(x, y, z)

  return vec
}

fun updateParticle(particle: Particle) {

  particle.vel += particle.acc
  particle.pos += particle.vel
}

data class Vec3(val x: Int, val y: Int, val z: Int) {

  companion object Constants {
    val zero = Vec3(0, 0, 0)
  }

  operator fun plus(other: Vec3): Vec3 {
    return Vec3(x + other.x, y + other.y, z + other.z)
  }

  fun manhatten(): Int {
    return x.absoluteValue + y.absoluteValue + z.absoluteValue
  }
}

data class Particle(var pos: Vec3, var vel: Vec3, val acc: Vec3)
