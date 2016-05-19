 package com.sbg.rpg.unpacker

import com.sbg.rpg.image.*
import java.nio.file.Path
import java.nio.file.Files
import java.awt.image.BufferedImage
import java.awt.Image
import java.util.ArrayList
import java.awt.Color
import java.awt.Rectangle
import java.awt.Point
import java.util.LinkedList
import java.util.HashSet
import com.sbg.rpg.util.spanRectangleFrom
import org.apache.logging.log4j.LogManager

class SpriteSheetUnpacker {
    private val logger = LogManager.getLogger(SpriteSheetUnpacker::class.simpleName)

    /**
     * Given a valid path to a sprite sheet, detects and returns every individual sprite. The method may not be perfect and
     * return individual sprites if they're not contiguous. Adjust the distance value and see if that helps.
     *
     * @param spriteSheet path to sprite sheet
     * @return list of extracted sprite images
     * @throws IllegalArgumentException if the file could not be found
     */
    fun unpack(spriteSheet: Path): List<Image> {
        require(Files.exists(spriteSheet)) { "The file ${spriteSheet.fileName} does not exist" }

        logger.debug("Loading sprite sheet.")
        // TODO: Convert to png so we have an alpha layer to work with
        val spriteSheetImage = readImage(spriteSheet).toBufferedImage()

        logger.debug("Determining most probable background color.")
        val backgroundColor  = spriteSheetImage.determineProbableBackgroundColor()
        logger.debug("The most probable background color is $backgroundColor")

        return findSprites(spriteSheetImage, backgroundColor).map { subImage -> spriteSheetImage.copySubImage(subImage) }
    }

    private fun findSprites(image: BufferedImage,
                            backgroundColor: Color): List<Rectangle> {
        val workingImage = image.copy()

        val spriteRectangles = ArrayList<Rectangle>()
        for (pixel in workingImage) {
            val (point, color) = pixel

            if (color != backgroundColor) {
                logger.debug("Found a sprite starting at (${point.x}, ${point.y})")
                val spritePlot = findContiguous(workingImage, point) { it != backgroundColor }
                val spriteRectangle = spanRectangleFrom(spritePlot, image)

                logger.debug("The identified sprite has an area of ${spriteRectangle.width}x${spriteRectangle.height}")

                spriteRectangles.add(spriteRectangle)
                workingImage.eraseSprite(backgroundColor, spritePlot)
            }
        }

        logger.info("Found ${spriteRectangles.size} sprites.")
        return spriteRectangles
    }

    private fun findContiguous(image: BufferedImage, point: Point, predicate: (Color) -> Boolean): List<Point> {
        val unvisited = LinkedList<Point>()
        val visited   = HashSet<Point>()

        unvisited.addAll(neighbors(point, image).filter { predicate(Color(image.getRGB(it.x, it.y))) })

        while (unvisited.isNotEmpty()) {
            val currentPoint = unvisited.pop()
            val currentColor = Color(image.getRGB(currentPoint.x, currentPoint.y))

            if (predicate(currentColor)) {
                unvisited.addAll(neighbors(currentPoint, image).filter {
                    !visited.contains(it) && !unvisited.contains(it) &&
                            predicate(Color(image.getRGB(it.x, it.y)))
                })

                visited.add(currentPoint)
            }
        }

        return visited.toList()
    }

    private fun neighbors(point: Point, image: Image): List<Point> {
        val points = ArrayList<Point>()

        if (point.x > 0)
            points.add(Point(point.x - 1, point.y))
        if (point.x < image.getWidth(null) - 1)
            points.add(Point(point.x + 1, point.y))

        if (point.y > 0)
            points.add(Point(point.x, point.y - 1))
        if (point.y < image.getHeight(null) - 1)
            points.add(Point(point.x, point.y + 1))

        return points
    }
}