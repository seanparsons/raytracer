package com.futurenotfound.raytracer

case class Camera(final val position: PositionVector, final val distance: Int) {
  def drawRays(viewPort: Viewport): Seq[CameraRay] = {
    viewPort.points.map{point =>
      new CameraRay(point, position, position.directionTo(point.positionVector), 1, distance)
    }
  }
}