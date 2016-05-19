package com.sbg.rpg.metadata

import com.sbg.rpg.packer.SpriteBounds
import org.apache.logging.log4j.LogManager

/**
 * Converts a list of SpriteBounds, essentially a pair of Frame Index and the location and area of the Rectangle,
 * into a yaml string representation. The string is guaranteed to have proper line breaks for each OS and a
 * readable format.
 *
 * <pre>
 *     Frames:
 *       - Index: 0
 *         Bounds: 0 0 50 50
 *       - Index: 1
 *         Bounds: 51 51 50 50
 *       ...
 * </pre>
 *
 * @param spriteBoundsList The SpriteBounds to convert
 * @return A yaml representation of the SpriteBounds or
 *         en empty ("") string if empty
 */
class YamlMetadataCreator: MetadataCreator {
    private val logger = LogManager.getLogger(YamlMetadataCreator::class.simpleName)

    override fun create(spriteBoundsList: List<SpriteBounds>): String {
        if (spriteBoundsList.isEmpty()) {
            logger.warn("No spriteBounds in list, returning empty string.")
            return ""
        }

        val yamlBuilder = StringBuilder()
        yamlBuilder.append("Frames:${System.lineSeparator()}")
        spriteBoundsList.forEach {
            yamlBuilder.append("  - Index: ${it.frame}${System.lineSeparator()}")
            yamlBuilder.append("    Bounds: [${it.bounds.x}, ${it.bounds.y}, ${it.bounds.width}, ${it.bounds.height}]${System.lineSeparator()}")
        }

        return yamlBuilder.toString()
    }
}