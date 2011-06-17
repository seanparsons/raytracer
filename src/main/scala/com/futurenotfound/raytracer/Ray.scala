package com.futurenotfound.raytracer

abstract class Ray{
  def origin: PositionVector
  def direction: DirectionVector
  def depth: Int
  def distance: Int
  val end = origin + new PositionVector(direction.x * distance, direction.y * distance, direction.z * distance)
  lazy val difference = end - origin
}

case class CameraRay(val origin: PositionVector,
                     val direction: DirectionVector,
                     val depth: Int,
                     val distance: Int) extends Ray {
}

case class ReflectedRay(val origin: PositionVector,
                        val direction: DirectionVector,
                        val depth: Int,
                        val distance: Int,
                        val originator: SceneObject) extends Ray {
}