package com.futurenotfound.raytracer

import collection.GenSeq

case class Scene(final val camera: Camera,
                 final val viewport: Viewport,
                 final val lights: Seq[Light],
                 final val sceneContents: Seq[SceneObject]) {
  final val black = new Colour(0, 0, 0)
  final val allSceneObjects = lights ++ sceneContents

  def draw(maxDepth: Int): GenSeq[Line[Colour]] = {
    camera.drawRays(viewport).par.map{line =>
      new Line(line.points.par.map{ray => raytrace(maxDepth, ray, false)})
    }
  }

  def debugDraw(maxDepth: Int, x: Int, y: Int) = {
    raytrace(maxDepth, camera.drawRays(viewport)(y).points(x), true)
  }
  
  private def raytrace(maxDepth: Int, ray: Ray, trace: Boolean): Colour = {
    if (trace) println("Ray: " + ray)
    allSceneObjects
      .view
      .map{sceneObject => sceneObject.intersect(ray)}
      .collect{case Some(intersection) => intersection}
      .filter(intersection => intersection.externalHit && intersection.distance(ray.origin) > Scene.epsilon)
      .sortBy(ray.origin distance _.location)
      .headOption
      .map{intersection =>
        if (trace) println("Intersection: " + intersection)
        intersection.sceneObject match {
          case light: Light => light.material.colour
          case _ =>
            lights.foldLeft(black) {
              case (colour: Colour, light: Light) => {
                val lightNormal = (light.centre - intersection.location).normalized
                val intersectionNormal = intersection.sceneObject.normal(intersection.location)
                if (trace) println("intersectionNormal = " + intersectionNormal)
                if (intersection.material.diffuse > 0f) {
                  val dotProduct = intersectionNormal.dot(lightNormal)
                  if (dotProduct > 0) {
                    val diffuseAmount = dotProduct * intersection.material.diffuse
                    val materialColour = colour + (intersection.material.colour * light.material.colour * diffuseAmount)
                    if (trace) println("materialColour = " + materialColour)
                    if (ray.depth <= maxDepth && intersection.material.reflectivity > 0) {
                      val reflectionNormal = ray.direction.reflect(intersectionNormal)
                      if (trace) println("reflectionNormal = " + reflectionNormal)
                      val originShift = intersectionNormal * Scene.epsilon
                      val newOrigin = intersection.location + originShift
                      if (trace) println("newOrigin = " + newOrigin)
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

object Scene{
  final val epsilon = 0.0001d
}