package com.sbg.rpg.packing.common

import com.sbg.rpg.packing.common.extensions.readImage
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.awt.Color
import java.awt.Rectangle
import java.nio.file.Paths
import kotlin.test.assertEquals

object SpriteCutterSpec : Spek({
    given("A sprite drawer") {
        val spriteDrawer = SpriteCutter(SpriteDrawer())

        on("copying a smaller sprite from source") {
            val singleSpriteUrl = this.javaClass.classLoader.getResource("unpacker/SingleSprite.png")
            val singleSpriteImage = Paths.get(singleSpriteUrl.toURI()).readImage()

            it("creates a new BufferedImage of said sprite") {
                val sprite = spriteDrawer.cut(singleSpriteImage, Rectangle(443, 200, 108, 129), Color.WHITE)

                assertEquals(Color(0, 0, 0, 0).rgb, sprite.getRGB(0, 0), "Expected top-left corner to be transparent.")
                assertEquals(Color(115, 66, 16).rgb, sprite.getRGB(0, 119), "Expected right foot to be brown.")
                assertEquals(Color(115, 66, 16).rgb, sprite.getRGB(106, 116), "Expected left foot to be brown.")
                assertEquals(Color(247, 247, 247).rgb, sprite.getRGB(85, 1), "Expected left foot to be brown.")
            }
        }

        on("copying a perfectly-cut sprite") {
            val croppedUrl = this.javaClass.classLoader.getResource("unpacker/AlreadyCropped.png")
            val croppedImage = Paths.get(croppedUrl.toURI()).readImage()

            it("has identical output to the previous test") {
                val sprite = spriteDrawer.cut(croppedImage, Rectangle(0, 0, 108, 129), Color.WHITE)

                assertEquals(Color(0, 0, 0, 0).rgb, sprite.getRGB(0, 0), "Expected top-left corner to be transparent.")
                assertEquals(Color(115, 66, 16).rgb, sprite.getRGB(0, 119), "Expected right foot to be brown.")
                assertEquals(Color(115, 66, 16).rgb, sprite.getRGB(106, 116), "Expected left foot to be brown.")
                assertEquals(Color(247, 247, 247).rgb, sprite.getRGB(85, 1), "Expected left foot to be brown.")
            }
        }

        on("trying to copy a sprite larger than the source common") {
            val croppedUrl = this.javaClass.classLoader.getResource("unpacker/AlreadyCropped.png")
            val croppedImage = Paths.get(croppedUrl.toURI()).readImage()

            it("constrains area to source common dimensions and returns identical output") {
                val sprite = spriteDrawer.cut(croppedImage, Rectangle(0, 0, 108, 129), Color.WHITE)

                assertEquals(Color(0, 0, 0, 0).rgb, sprite.getRGB(0, 0), "Expected top-left corner to be transparent.")
                assertEquals(Color(115, 66, 16).rgb, sprite.getRGB(0, 119), "Expected right foot to be brown.")
                assertEquals(Color(115, 66, 16).rgb, sprite.getRGB(106, 116), "Expected left foot to be brown.")
                assertEquals(Color(247, 247, 247).rgb, sprite.getRGB(85, 1), "Expected left foot to be brown.")
            }
        }


        on("copying multiple sprites from source") {
            val multipleSpritesUrl = this.javaClass.classLoader.getResource("unpacker/ManySprites.png")
            val multipleSpritesImage = Paths.get(multipleSpritesUrl.toURI()).readImage()

            it("creates a list of three BufferedImages") {
                val sprites = spriteDrawer.cutMultiple(
                        multipleSpritesImage,
                        listOf(
                                Rectangle(42, 20, 123, 189),
                                Rectangle(215, 24, 177, 183),
                                Rectangle(428, 30, 111, 180)
                        ),
                        Color(109, 73, 138)
                )

                assertEquals(3, sprites.size, "Expected three sprites from SpriteCutter.cutMultiple")
            }
        }

        on("copying a sprite with a transparent common") {
            val multipleSpritesTransparentBackgroundUrl = this.javaClass.classLoader.getResource("unpacker/MultipleSprites_TransparentBackground.png")
            val multipleSpritesTransparentBackgroundImage = Paths.get(multipleSpritesTransparentBackgroundUrl.toURI()).readImage()

            it("preserves transparency for each sprite") {
                val sprites = spriteDrawer.cutMultiple(
                        multipleSpritesTransparentBackgroundImage,
                        listOf(
                                Rectangle(7, 0, 30, 40),
                                Rectangle(48, 0, 31, 39)
                        ),
                        Color(0, 0, 0, 0)
                )

                assertEquals(2, sprites.size, "Expected two sprites from SpriteCutter.cutMultiple")
            }
        }
    }
})