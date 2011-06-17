package com.futurenotfound.raytracer

import org.specs.mock.Mockito
import org.specs.SpecificationWithJUnit

class ColourTest extends SpecificationWithJUnit with Mockito {
  "Colour" should {
    "add colours" in {
      val red = new Colour(1, 0, 0)
      val blue = new Colour(0, 0, 1)
      val purple = new Colour(1, 0, 1)
      red + blue must equalTo(purple)
    }
  }
}