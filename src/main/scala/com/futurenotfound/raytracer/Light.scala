package com.futurenotfound.raytracer

case class Light(val centre: PositionVector, val radius: Double, val material: Material) extends SphericalSceneObject