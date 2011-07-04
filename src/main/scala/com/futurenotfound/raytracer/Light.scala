package com.futurenotfound.raytracer

case class Light(final val centre: PositionVector, final val radius: Double, final val material: Material) extends SphericalSceneObject