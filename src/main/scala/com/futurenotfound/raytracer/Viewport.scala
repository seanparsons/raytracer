package com.futurenotfound.raytracer

case class Viewport(val bottomLeft: PositionVector,
                    val bottomRight: PositionVector,
                    val topLeft: PositionVector,
                    val horizontalResolution: Int,
                    val verticalResolution: Int) {
  val rightDifference = bottomRight - bottomLeft
  val upDifference = topLeft - bottomLeft
  val lines: Seq[Line[PositionVector]] = for (verticalStep <- 0 until verticalResolution)
                    yield new Line(for (horizontalStep <- 0 until horizontalResolution)
                      yield bottomLeft +
                        (rightDifference * horizontalStep / horizontalResolution) +
                        (upDifference * verticalStep / verticalResolution))
}