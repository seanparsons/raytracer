package com.futurenotfound.raytracer

case class Colour(final val red: Double, final val green: Double, final val blue: Double) {
  def *(intensity: Double) = new Colour(red * intensity, green * intensity, blue * intensity)
  def +(otherColour: Colour) = new Colour((red + otherColour.red).min(Colour.one),
                                          (green + otherColour.green).min(Colour.one),
                                          (blue + otherColour.blue).min(Colour.one))
  def *(otherColour: Colour) = new Colour(red * otherColour.red, green * otherColour.green, blue * otherColour.blue)
}

object Colour {
  final val one: Double = 1
}