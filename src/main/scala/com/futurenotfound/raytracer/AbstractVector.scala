package com.futurenotfound.raytracer

import scala.math._
import com.futurenotfound.raytracer.InternalMath._

abstract class AbstractVector[T <: AbstractVector[T]] {
  def x: Double
  def y: Double
  def z: Double
  protected def createVector(x: Double, y: Double, z: Double): T
  // TODO: Push this down?
  def magnitude: Double
  def normalized = new DirectionVector(x / magnitude, y / magnitude, z / magnitude)
  @inline
  def +(vector: AbstractVector[_]) = createVector(x + vector.x, y + vector.y, z + vector.z)
  @inline
  def -(vector: AbstractVector[_]) = createVector(x - vector.x, y - vector.y, z - vector.z)
  @inline
  def *(number: Double) = createVector(x * number, y * number, z * number)
  @inline
  def /(vector: AbstractVector[_]) = createVector(x / vector.x, y / vector.y, z / vector.z)
  @inline
  def /(number: Double) = createVector(x / number, y / number, z / number)
  @inline
  def dot(vector: AbstractVector[_]): Double = ((x * vector.x) + (y * vector.y) + (z * vector.z)).toFloat
  @inline
  def cross(vector: T) = createVector(
    (y * vector.z) - (z * vector.y),
    (z * vector.x) - (x * vector.z),
    (x * vector.y) - (y * vector.x)
  )
}

case class DirectionVector(final val x: Double, final val y: Double, final val z: Double) extends AbstractVector[DirectionVector] {
  final val magnitude = 1.0
  @inline
  protected def createVector(x: Double, y: Double, z: Double): DirectionVector = new DirectionVector(x, y, z)
  def reflect(surfaceNormal: DirectionVector): DirectionVector = {
    // Vect1 - 2 * WallN * (WallN DOT Vect1)
    this - (surfaceNormal * surfaceNormal.dot(this) * 2)
  }
}

case class PositionVector(final val x: Double, final val y: Double, final val z: Double) extends AbstractVector[PositionVector] {
  final val magnitude = sqrt((x * x) + (y * y) + (z * z))
    @inline
  protected def createVector(x: Double, y: Double, z: Double): PositionVector = new PositionVector(x, y, z)
  def closest(vectors: Seq[PositionVector]) = vectors.sortBy(vector => distance(vector)).headOption
  def directionTo(to: PositionVector): DirectionVector = (to - this).normalized
  def distance(vector: PositionVector) = sqrt(square(abs(x - vector.x)) + square(abs(y - vector.y)) + square(abs(z - vector.z)))
}