package com.futurenotfound.raytracer

import scala.swing._
import java.awt.Color
import java.util.concurrent.atomic.AtomicInteger
import java.awt.image.BufferedImage._
import java.awt.image.BufferedImage
import akka.actor._
import com.typesafe.config.ConfigFactory
import akka.routing._
import akka.util.duration._

object RayTracer extends SimpleSwingApplication {
  val config = ConfigFactory.load("akka.conf")
  val actorSystem = ActorSystem("actorSystem", config)

  val sceneCount = new AtomicInteger(0)
  def top = new MainFrame {
    val button = new Button {
      text = "Start"
      action = Action("Start") {
        val customPaintComponent = new CustomPaintComponent(800, 600)
        val stepper = new SceneStepper()
        contents = customPaintComponent
        val resizer = DefaultResizer(lowerBound = 10, upperBound = 100)
        val customComponentActor = actorSystem.actorOf(Props(new CustomPaintComponentActor(customPaintComponent)).withDispatcher("swing-dispatcher"))
        val workers = actorSystem.actorOf(Props[SceneRaytraceActor].withRouter(RoundRobinRouter(resizer = Some(resizer))))
        val coordinatorActor = actorSystem.actorOf(Props(new SceneCoordinatorActor((positionColour) => customComponentActor ! positionColour, workers, stepper.sceneStream.iterator)).withDispatcher("isolated-dispatcher"), name = "coordinatorActor")
        actorSystem.scheduler.schedule(0.seconds, 100.milliseconds, customComponentActor, UpdateCustomPaintComponent)
        coordinatorActor ! StartDrawing
      }
    }
    title = "Test Scene"
    contents = button
    size = new Dimension(820, 620)
    minimumSize = size
  }

  override def shutdown() {
    super.shutdown()
    actorSystem.shutdown()
  }
}

case class CustomPaintComponent(width: Int, height: Int) extends Component {
  peer.setDoubleBuffered(true)
  val image = new BufferedImage(width, height, TYPE_INT_RGB)

  override protected def paintComponent(graphics: Graphics2D) {
    super.paintComponent(graphics)
    println("paintComponent")
    graphics.drawImage(image, 0, 0, image.getWidth, image.getHeight, null)
  }
}

case class CustomPaintComponentActor(customPaintComponent: CustomPaintComponent) extends Actor {
  def receive = {
    case positionColour: PositionColour => {
      val color = new Color((positionColour.colour.red * 255).toInt, (positionColour.colour.green * 255).toInt, (positionColour.colour.blue * 255).toInt)
      customPaintComponent.image.setRGB(positionColour.x, positionColour.y, color.getRGB)
    }
    case UpdateCustomPaintComponent => {
      customPaintComponent.repaint()
    }
  }
}

object UpdateCustomPaintComponent

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