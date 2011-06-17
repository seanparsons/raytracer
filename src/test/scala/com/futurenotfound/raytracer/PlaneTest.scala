package com.futurenotfound.raytracer

import org.specs.SpecificationWithJUnit
import org.specs.mock.Mockito

class PlaneTest extends SpecificationWithJUnit with Mockito {
  "Plane" should {
    val material = new Material(new Colour(1d, 1d, 1d), 1d, 0d)
    "intersect at the origin with a basic plane" in {
      val plane = new Plane(new DirectionVector(0, -1, 0), new PositionVector(0, 0, 0), material)
      val ray = new CameraRay(new PositionVector(0d, -10d, 0d), new DirectionVector(0, 1, 0), 3, 1000)
      val intersection = plane.intersect(ray)
      intersection.get.location must equalTo(new PositionVector(0, 0, 0))
    }
    "intersect at the origin with an angled plane" in {29
      val plane = new Plane(new PositionVector(-1, -1, 0).normalized, new PositionVector(0, 0, 0), material)
      val ray = new CameraRay(new PositionVector(0d, -10d, 0d), new DirectionVector(0, 1, 0), 3, 1000)
      val intersection = plane.intersect(ray)
      intersection.get.location.x must beCloseTo(0d, 0.0001d)
      intersection.get.location.y must beCloseTo(0d, 0.0001d)
      intersection.get.location.z must beCloseTo(0d, 0.0001d)
    }
    "intersect at an expected point with an angled plane from a perpendicular angle" in {
      val plane = new Plane(new PositionVector(-1, -1, 0).normalized, new PositionVector(0, 0, 0), material)
      val ray = new CameraRay(new PositionVector(5d, -10d, -10d), new PositionVector(0, 1, 1).normalized, 3, 1000)
      val intersection = plane.intersect(ray)
      intersection.get.location.x must beCloseTo(5d, 0.0001d)
      intersection.get.location.y must beCloseTo(-5d, 0.0001d)
      intersection.get.location.z must beCloseTo(-5d, 0.0001d)
    }
    "intersect at the origin with an angled plane from a perpendicular angle" in {
      val plane = new Plane(new PositionVector(-1, -1, 0).normalized, new PositionVector(0, 0, 0), material)
      val ray = new CameraRay(new PositionVector(0d, -10d, -10d), new PositionVector(0, 1, 1).normalized, 3, 1000)
      val intersection = plane.intersect(ray)
      intersection.get.location.x must beCloseTo(0d, 0.0001d)
      intersection.get.location.y must beCloseTo(0d, 0.0001d)
      intersection.get.location.z must beCloseTo(0d, 0.0001d)
    }
    "intersect with a line parallel to the origin" in {
      val plane = new Plane(new PositionVector(-1, -1, 0).normalized, new PositionVector(0, 0, 0), material)
      val ray = new CameraRay(new PositionVector(5d, -10d, 0d), new DirectionVector(0, 1, 0), 3, 1000)
      val intersection = plane.intersect(ray)
      intersection.get.location.x must beCloseTo(5d, 0.0001d)
      intersection.get.location.y must beCloseTo(-5d, 0.0001d)
      intersection.get.location.z must beCloseTo(0d, 0.0001d)
    }
  }
}