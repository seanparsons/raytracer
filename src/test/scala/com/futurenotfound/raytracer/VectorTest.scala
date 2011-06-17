package com.futurenotfound.raytracer

import org.specs.mock.Mockito
import org.specs.SpecificationWithJUnit

class VectorTest extends SpecificationWithJUnit with Mockito {
  "AbstractVector" should {
    "return a distance of 2 in a 1-dimensional difference" in {
      val distance = new PositionVector(0, 0, 0).distance(new PositionVector(2, 0, 0))
      distance must equalTo(2)
    }
    "normalize a 1-dimensional vector" in {
      val positionVector = new PositionVector(10, 0, 0)
      positionVector.normalized must equalTo(new DirectionVector(1, 0, 0))
    }
    "normalize a 2-dimensional vector" in {
      val normalizedVector = new PositionVector(10, 10, 0).normalized
      normalizedVector.x must beCloseTo(0.7071067d, 0.0000001d)
      normalizedVector.y must beCloseTo(0.7071067d, 0.0000001d)
      normalizedVector.z must equalTo(0d)
    }
    "calculate a simple reflection vector" in {
      val vector = new PositionVector(1, 1, 0).normalized
      val normal = new PositionVector(-1, 0, 0).normalized
      val reflection = vector.reflect(normal)
      val expectedResult = new PositionVector(-1, 1, 0).normalized
      reflection.x must beCloseTo(expectedResult.x, 0.00001d)
      reflection.y must beCloseTo(expectedResult.y, 0.00001d)
      reflection.z must equalTo(expectedResult.z)
    }
    /*
    "calculate a complicated reflection vector" in {
      val vector = new PositionVector(10, -1, 1).normalized
      val normal = new PositionVector(-1, -1, 10).normalized
      val reflection = vector.reflect(normal)
      val expectedResult = new PositionVector(9, -1, 1).normalized
      reflection.x must beCloseTo(expectedResult.x, 0.00001d)
      reflection.y must beCloseTo(expectedResult.y, 0.00001d)
      reflection.z must equalTo(expectedResult.z)
    }*/
  }
}