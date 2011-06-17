package com.futurenotfound.raytracer

import org.specs.mock.Mockito
import org.specs.SpecificationWithJUnit

class SphereTest extends SpecificationWithJUnit with Mockito {
  "Sphere" should {
    val sphere = new Sphere(new PositionVector(0, 0, 0), 2, new Material(new Colour(0.2, 0.2, 0.2), 1.0f, 1.0f))
    "intersect with a line" in {
      val ray = new CameraRay(new PositionVector(10, 0, 0), new DirectionVector(-1, 0, 0), 1, 1000)
      val intersection = sphere.intersect(ray)
      intersection must equalTo(Some(new Intersection(new PositionVector(2, 0, 0),
                                                      true,
                                                      new Material(new Colour(0.2, 0.2, 0.2), 1.0f, 1.0f),
                                                      sphere)))
    }
    "return obvious normal" in {
      sphere.normal(new PositionVector(2, 0, 0)) must equalTo(new DirectionVector(1, 0, 0))
    }
  }
}