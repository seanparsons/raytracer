package com.futurenotfound.raytracer

import swing.{MainFrame, SimpleSwingApplication, Component}
import javax.swing.JPanel
import scala.swing._
import event.{MouseClicked, MouseMoved}
import java.awt.{Event, Color, Graphics, Panel}
import java.awt.event.{MouseMotionListener, MouseEvent}
import scala.concurrent.ops._
import java.util.concurrent.atomic.AtomicInteger
import javax.imageio.ImageIO
import java.awt.image.BufferedImage._
import java.awt.image.{Raster, BufferedImage}

object RayTracer extends SimpleSwingApplication {
  def toAwtColor(colour: Colour): Color = {
    return new Color(colour.red.toFloat, colour.green.toFloat, colour.blue.toFloat)
  }

  val sceneCount = new AtomicInteger(0)
  def top = new MainFrame {
    def updateFrame(image: BufferedImage) = {
      contents = new Component(){
        peer.setDoubleBuffered(true)
        override def paint(graphics: Graphics2D) = {
          graphics.drawImage(image, 0, 0, image.getWidth, image.getHeight, null)
        }
      }
      size = new Dimension(820, 620)
    }
    val button = new Button {
      text = "Start"
      action = Action("Start") {
        spawn {
          val start = System.nanoTime()
          val stepper = new SceneStepper()
          stepper.sceneStream.foreach{scene =>
            val renderedScene = scene.draw(5)
            val image = new BufferedImage(scene.viewport.horizontalResolution, scene.viewport.verticalResolution, TYPE_INT_RGB)
            val intValues = renderedScene.flatMap(line => line.points.flatMap(colour => Vector((colour.red * 255).toInt, (colour.green * 255).toInt, (colour.blue * 255).toInt))).toArray[Int]
            val raster = image.getData.createCompatibleWritableRaster()
            raster.setPixels(0, 0, image.getWidth, image.getHeight, intValues)
            image.setData(raster)
            updateFrame(image)
          }
          println("Time taken = %s".format(System.nanoTime() - start))
        }
      }
    }
    title = "Test Scene"
    contents = button
    size = new Dimension(820, 620)
    minimumSize = size
  }
}

case class SceneStepper() {
  val baseScene = new TestScene()

  def next(scene: Scene): Stream[Scene] = if (scene.camera.position.x > -5) Stream.empty else {
    val newScene = scene.copy(camera = new Camera(new PositionVector(scene.camera.position.x + 0.1, 0, 0), 10000))
      .copy(viewport = scene.viewport.add(new PositionVector(0.1, 0, 0)))
    Stream.cons(newScene, next(newScene))
  }

  def sceneStream: Stream[Scene] = next(baseScene.scene)
}

case class TestScene() {
  val silver = new Material(new Colour(0.7f, 0.7f, 0.7f), 0.2f, 0.6f)
  val blue = new Material(new Colour(0.05, 0.05, 1), 0.3f, 0.1f)
  val scene = new Scene(
    new Camera(new PositionVector(-15, 0, 0), 10000),
    new Viewport(
      new PositionVector(-10, -3, -4),  // Bottom right.
      new PositionVector(-10, -3, 4),   // Bottom left.
      new PositionVector(-10, 3, -4),   // Top left.
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
}