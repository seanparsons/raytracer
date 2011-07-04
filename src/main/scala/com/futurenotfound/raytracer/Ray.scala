package com.futurenotfound.raytracer

abstract class Ray {
  def origin: PositionVector
  def direction: DirectionVector
  def depth: Int
  def distance: Int
  final val end = origin + new PositionVector(direction.x * distance, direction.y * distance, direction.z * distance)
  final val difference = end - origin
}

case class CameraRay(final val origin: PositionVector,
                     final val direction: DirectionVector,
                     final val depth: Int,
                     final val distance: Int) extends Ray {
}

case class ReflectedRay(final val origin: PositionVector,
                        final val direction: DirectionVector,
                        final val depth: Int,
                        final val distance: Int,
                        final val originator: SceneObject) extends Ray {
}