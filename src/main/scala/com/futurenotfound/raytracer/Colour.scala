package com.futurenotfound.raytracer

case class Colour(val red: Double, val green: Double, val blue: Double) {
  def *(intensity: Double) = new Colour(red * intensity, green * intensity, blue * intensity)
  def +(otherColour: Colour) = new Colour((red + otherColour.red).min(Colour.one),
                                          (green + otherColour.green).min(Colour.one),
                                          (blue + otherColour.blue).min(Colour.one))
  def *(otherColour: Colour) = new Colour(red * otherColour.red, green * otherColour.green, blue * otherColour.blue)
}

object Colour {
  val one: Double = 1
}