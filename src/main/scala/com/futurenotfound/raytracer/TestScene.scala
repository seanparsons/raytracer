package com.futurenotfound.raytracer

import swing.{MainFrame, SimpleSwingApplication, Component}
import javax.swing.JPanel
import scala.swing._
import event.{MouseClicked, MouseMoved}
import java.awt.{Event, Color, Graphics, Panel}
import java.awt.event.{MouseMotionListener, MouseEvent}

object TestScene extends SimpleSwingApplication {
  def toAwtColor(colour: Colour): Color = {
    return new Color(colour.red.toFloat, colour.green.toFloat, colour.blue.toFloat)
  }
  val silver = new Material(new Colour(0.7f, 0.7f, 0.7f), 0.2f, 0.6f)
  val blue = new Material(new Colour(0.05, 0.05, 1), 0.3f, 0.1f)
  val scene = new Scene(
    new Camera(new PositionVector(-10, 0, 0), 10000),
    new Viewport(
      new PositionVector(-5, -3, -4),  // Bottom right.
      new PositionVector(-5, -3, 4),   // Bottom left.
      new PositionVector(-5, 3, -4),   // Top left.
      800,
      600
    ),
    Vector(
      //new Light(new PositionVector(0, -10, -10), 0.5f, new Material(new Colour(1, 1, 1), 1f, 1f)),
      //new Light(new PositionVector(0, -10, 10), 0.5f, new Material(new Colour(1, 1, 1), 1f, 1f)),
      new Light(new PositionVector(0, -10, 0), 0.5f, new Material(new Colour(1, 1, 1), 1f, 1f))
    ),
    Vector(
      new Sphere(new PositionVector(0, 0, 0), 2, silver),
      new Sphere(new PositionVector(0, 0, -6), 2, silver),
      new Sphere(new PositionVector(0, 0, 6), 2, silver),
      new Plane(new PositionVector(-1, -2, 0).normalized, new PositionVector(0, 10, 0), blue)
    )
  )
  val start = System.nanoTime()
  val renderedScene = scene.draw(5)
  val coordinatesAndColours = (for(x <- 0 until scene.viewport.horizontalResolution;
                                   y <- 0 until scene.viewport.verticalResolution)
          yield (x, y, toAwtColor(renderedScene(y).points(x)))).toList
  println("Time taken = %s".format(System.nanoTime() - start))
  def top = new MainFrame {
    title = "Test Scene"
    contents = new Component(){
      override def paint(graphics: Graphics2D) = {
        coordinatesAndColours.foreach{entry =>
          graphics.setColor(entry._3)
          graphics.drawRect(entry._1, entry._2, 1, 1)
        }
      }
      listenTo(mouse.clicks)
      reactions += {
        case MouseClicked(source, point, modifiers, clicks, triggersPopup) =>
          scene.debugDraw(5, point.x, point.y)  
      }
    }
    size = new Dimension(820, 620)
  }
}