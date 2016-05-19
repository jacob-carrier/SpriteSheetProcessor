package com.sbg.rpg.image

import com.sbg.rpg.map.max
import java.awt.*
import java.awt.image.BufferedImage
import java.util.*

operator fun BufferedImage.iterator(): Iterator<Pixel> {
    return object : Iterator<Pixel> {
        var currentX = 0
        var currentY = 0

        override fun hasNext(): Boolean {
            return currentY != height
        }

        override fun next(): Pixel {
            val point = Point(currentX, currentY)
            val color = Color(getRGB(currentX, currentY))

            if (currentX == width - 1) {
                currentX = 0
                currentY += 1
            } else {
                currentX += 1
            }

            return Pixel(point, color)
        }
    }
}

fun BufferedImage.copy(): BufferedImage {
    val colorMode = colorModel

    return BufferedImage(colorMode,
            copyData(null),
            colorMode.isAlphaPremultiplied,
            null)
}

fun BufferedImage.copySubImage(area: Rectangle): BufferedImage {
    require(area.x >= 0 && area.y >= 0) { "Rectangle outside of image bounds; x=${area.x}, y=${area.y}" }
    require(area.width > 0 && area.height > 0) { "Rectangle must have positive, non-zero width and height; width=${area.width}, height=${area.height}" }

    val subImage = getSubimage(area.x, area.y, area.width, area.height)
    val target = BufferedImage(subImage.width, subImage.height, subImage.type)

    for (pixel in subImage)
        target.setRGB(pixel.point.x, pixel.point.y, pixel.color.rgb)

    return target
}

fun BufferedImage.copyWithBorder(dimensions: Dimension, borderColor: Color): BufferedImage {
    require(dimensions.width > width) { "Expected a width larger than current image to be copied; width=${dimensions.width}" }
    require(dimensions.height > height) { "Expected a height larger than current image to be copied; height=${dimensions.height}" }

    val target = BufferedImage(dimensions.width,
            dimensions.height,
            type)

    val widthDifference  = (dimensions.width - width) / 2
    val heightDifference = (dimensions.height - height) / 2

    for (pixel in this) {
        val (point, color) = pixel

        target.setRGB(point.x + widthDifference,
                point.y + heightDifference,
                color.rgb)
    }

    for (x in 0..target.width - 1) {
        target.setRGB(x, 0, borderColor.rgb)
        target.setRGB(x, target.height - 1, borderColor.rgb)
    }

    for (y in 0..target.height - 1) {
        target.setRGB(0, y, borderColor.rgb)
        target.setRGB(target.width - 1, y, borderColor.rgb)
    }

    return target
}

fun BufferedImage.eraseSprite(withColor: Color, points: List<Point>) {
    points.forEach { setRGB(it.x, it.y, withColor.rgb) }
}

fun BufferedImage.determineProbableBackgroundColor(): Color {
    require(width > 0 && height > 0) { "Image must have positive, non-zero width and height; width=${width}, height=${height}" }

    val colorMap = HashMap<Color, Int>()

    for (x in 0..(width - 1)) {
        for (y in 0..(height - 1)) {
            val colorAtXY = Color(getRGB(x, y))

            colorMap[colorAtXY] = colorMap.getOrDefault(colorAtXY, 0) + 1
        }
    }

    return colorMap.max()!!.first
}

fun Image.toBufferedImage(imageType: Int = BufferedImage.TYPE_INT_RGB): BufferedImage {
    if (this is BufferedImage)
        return this

    val bufferedImage = BufferedImage(getWidth(null),
            getHeight(null),
            imageType)
    val graphics = bufferedImage.createGraphics()
    graphics.drawImage(this, 0, 0, null)
    graphics.dispose()

    return bufferedImage
}