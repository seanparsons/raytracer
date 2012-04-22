package com.futurenotfound.raytracer

case class ScreenPoint(x: Int, y: Int, positionVector: PositionVector)

case class Viewport(final val bottomLeft: PositionVector,
                    final val bottomRight: PositionVector,
                    final val topLeft: PositionVector,
                    final val horizontalResolution: Int,
                    final val verticalResolution: Int) {
  final val rightDifference = bottomRight - bottomLeft
  final val upDifference = topLeft - bottomLeft
  final val points: Seq[ScreenPoint] = for {
    verticalStep <- 0 until verticalResolution
    horizontalStep <- 0 until horizontalResolution
  } yield ScreenPoint(horizontalStep, verticalStep, bottomLeft +
    (rightDifference * horizontalStep / horizontalResolution) +
    (upDifference * verticalStep / verticalResolution))
  def add(positionVector: PositionVector) = copy(bottomLeft = bottomLeft + positionVector, bottomRight = bottomRight + positionVector, topLeft = topLeft + positionVector)
}