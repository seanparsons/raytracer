package com.futurenotfound.raytracer

case class Camera(val position: PositionVector, val distance: Int) {
  def drawRays(viewPort: Viewport): Seq[Line[CameraRay]] = {
    viewPort.lines.map{line =>
      new Line(line.points.map{point =>
        new CameraRay(position, position.directionTo(point), 1, distance)
      })
    }
  }
}