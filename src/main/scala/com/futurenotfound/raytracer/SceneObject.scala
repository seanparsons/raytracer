package com.futurenotfound.raytracer

import scala.math._
import com.futurenotfound.raytracer.InternalMath._

trait SceneObject{
  def intersect(ray: Ray): Option[Intersection]
  def normal(point: PositionVector): DirectionVector
}

case class Intersection(final val location: PositionVector,
                        final val externalHit: Boolean,
                        final val material: Material,
                        final val sceneObject: SceneObject) {
  def distance(vector: PositionVector) = location.distance(vector)
}

trait SphericalSceneObject extends SceneObject {
  def centre: PositionVector
  def radius: Double
  def material: Material
  def normal(point: PositionVector): DirectionVector = (point - centre).normalized
  def intersect(ray: Ray): Option[Intersection] = {
    ray match {
      case reflectedRay: ReflectedRay if reflectedRay.originator == this => None
      case _ => {
        //a = (x2 - x1)2 + (y2 - y1)2 + (z2 - z1)2
        val a = square(ray.end.x - ray.origin.x) +
                square(ray.end.y - ray.origin.y) +
                square(ray.end.z - ray.origin.z)
        //b = 2[ (x2 - x1) (x1 - x3) + (y2 - y1) (y1 - y3) + (z2 - z1) (z1 - z3) ]
        val b = 2 * (
          ((ray.end.x - ray.origin.x) * (ray.origin.x - centre.x)) +
          ((ray.end.y - ray.origin.y) * (ray.origin.y - centre.y)) +
          ((ray.end.z - ray.origin.z) * (ray.origin.z - centre.z)))
        //c = x32 + y32 + z32 + x12 + y12 + z12 - 2[x3 x1 + y3 y1 + z3 z1] - r2
        val c = square(centre.x) +
                square(centre.y) +
                square(centre.z) +
                square(ray.origin.x) +
                square(ray.origin.y) +
                square(ray.origin.z) -
                (2 * ((centre.x * ray.origin.x) +
                      (centre.y * ray.origin.y) +
                      (centre.z * ray.origin.z))) -
                square(radius)
        val inner = (b * b) - (4 * a * c)
        if (inner < 0) None
        else {
          val innerSqrt = sqrt(inner)
          val intersectionVector = ray.origin.closest(Array(
            ray.origin + (ray.difference * ((-b + innerSqrt) / (2 * a))),
            ray.origin + (ray.difference * ((-b - innerSqrt) / (2 * a)))
          )).head
          Some(new Intersection(intersectionVector, ray.origin.distance(centre) > radius, material, this))
        }
      }
    }
  }
}

trait PlanarSceneObject extends SceneObject {
  def normal: DirectionVector
  def point: PositionVector
  def material: Material
  def intersect(ray: Ray): Option[Intersection] = {
    ray match {
      case reflectedRay: ReflectedRay if reflectedRay.originator == this => None
      case _ => {
        val numerator = (point - ray.origin).dot(normal)
        val denominator = ray.direction.dot(normal)
        if (denominator == 0d) {
          if (numerator == 0d) {
            Some(new Intersection(ray.origin, true, material, this))
          }
          else None
        }
        else {
          Some(new Intersection(ray.origin + (ray.direction * numerator / denominator), true, material, this))
        }
      }
    }
  }
  def normal(point: PositionVector): DirectionVector = normal
}

case class Plane(val normal: DirectionVector, val point: PositionVector, val material: Material) extends PlanarSceneObject

case class Sphere(val centre: PositionVector, val radius: Double, val material: Material) extends SphericalSceneObject
