package com.futurenotfound.raytracer

import akka.actor._
import akka.pattern._
import akka.util.duration._
import akka.util.Timeout
import java.util.concurrent.atomic.AtomicLong

case class Scene(final val camera: Camera,
                 final val viewport: Viewport,
                 final val lights: Seq[Light],
                 final val sceneContents: Seq[SceneObject]) {
  final val black = new Colour(0, 0, 0)
  final val allSceneObjects = lights ++ sceneContents

  def cameraRays: Seq[CameraRay] = camera.drawRays(viewport)
  
  def raytrace(maxDepth: Int, ray: Ray, trace: Boolean): Colour = {
    def tracePrint(message: => String) {
      if (trace) println(message)
    }

    tracePrint("Ray: " + ray)
    allSceneObjects
      .view
      .map(sceneObject => sceneObject.intersect(ray))
      .collect{case Some(intersection) => intersection}
      .filter(intersection => intersection.externalHit && intersection.distance(ray.origin) > Scene.epsilon)
      .sortBy(sceneObject => ray.origin.distance(sceneObject.location))
      .headOption
      .map{intersection =>
        tracePrint("Intersection: " + intersection)
        intersection.sceneObject match {
          case light: Light => light.material.colour
          case _ =>
            lights.foldLeft(black) {
              case (colour: Colour, light: Light) => {
                val lightNormal = (light.centre - intersection.location).normalized
                val intersectionNormal = intersection.sceneObject.normal(intersection.location)
                tracePrint("intersectionNormal = " + intersectionNormal)
                if (intersection.material.diffuse > 0f) {
                  val dotProduct = intersectionNormal.dot(lightNormal)
                  if (dotProduct > 0) {
                    val diffuseAmount = dotProduct * intersection.material.diffuse
                    val materialColour = colour + (intersection.material.colour * light.material.colour * diffuseAmount)
                    tracePrint("materialColour = " + materialColour)
                    if (ray.depth <= maxDepth && intersection.material.reflectivity > 0) {
                      val reflectionNormal = ray.direction.reflect(intersectionNormal)
                      tracePrint("reflectionNormal = " + reflectionNormal)
                      val originShift = intersectionNormal * Scene.epsilon
                      val newOrigin = intersection.location + originShift
                      tracePrint("newOrigin = " + newOrigin)
                      materialColour + raytrace(maxDepth,
                        new ReflectedRay(newOrigin,
                          reflectionNormal,
                          ray.depth + 1,
                          camera.distance,
                          intersection.sceneObject
                        ),
                        trace
                      )
                    } else materialColour
                  } else colour
                } else colour
              }
            }
        }
    }
    .getOrElse(black)
  }
}

case class SceneDraw(scene: Scene, maxDepth: Int, cameraRay: CameraRay)

case object FinishedDrawing

case object StartDrawing

case object NextScene

case class PositionColour(x: Int, y: Int, colour: Colour)

case class SceneCoordinatorActor(updateColour: (PositionColour) => Unit, workers: ActorRef, sceneIterator: Iterator[Scene]) extends Actor {
  def receive = {
    case StartDrawing => {
      self ! NextScene
    }
    case NextScene => {
      if (sceneIterator.hasNext) processScene(sceneIterator.next())
    }
  }
  
  def processScene(scene: Scene) {
    implicit val timeout = Timeout(5.minutes)
    val cameraRays = scene.cameraRays
    val pixelsRemaining: AtomicLong = new AtomicLong(cameraRays.size)
    cameraRays.foreach{cameraRay =>
      (workers ? SceneDraw(scene, 5, cameraRay)).onComplete{
        case Right(positionColour: PositionColour) => {
          //println("Receieved positionColour")
          updateColour(positionColour)
          if (pixelsRemaining.decrementAndGet() <= 0L) {
            Thread.sleep(2000)
            self ! NextScene
          }
        }
      }
    }
    println("Dispatched messages for scene.")
  }
}

case class SceneRaytraceActor() extends Actor {
  def receive = {
    case SceneDraw(scene, maxDepth, cameraRay) => {
      //println("Receieved SceneDraw")
      try {
        sender ! new PositionColour(cameraRay.screenPoint.x, cameraRay.screenPoint.y, scene.raytrace(maxDepth, cameraRay, false))
      } catch {
        case any => println("WTF?")
      }
    }
  }
}

object Scene{
  final val epsilon = 0.0001d
}