/*
 *  Copyright 2016 Christian Broomfield
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.sbg.rpg.ui

import com.sbg.rpg.image.probableBackgroundColor
import com.sbg.rpg.image.readImage
import com.sbg.rpg.ui.model.AnnotatedSpriteSheet
import com.sbg.rpg.unpacker.SpriteSheetUnpacker
import org.apache.logging.log4j.LogManager
import tornadofx.Controller
import java.io.File
import java.nio.file.Paths

class SpriteSheetProcessorController: Controller() {
    private val logger = LogManager.getLogger(SpriteSheetProcessorController::class.simpleName)

    private val view: SpriteSheetProcessorView by inject()

    private val spriteSheetUnpacker: SpriteSheetUnpacker

    init {
        spriteSheetUnpacker = SpriteSheetUnpacker()
    }

    fun unpackSpriteSheets(spriteSheets: List<File>): List<AnnotatedSpriteSheet> {
        logger.debug("Loading files $spriteSheets")

        val annotatedSpriteSheets = spriteSheets.map { spriteSheet ->
            val spriteSheet = readImage(Paths.get(spriteSheet.absolutePath))
            val spriteBoundsList = spriteSheetUnpacker.calculateSpriteBounds(spriteSheet)

            AnnotatedSpriteSheet(
                    spriteSheet,
                    spriteBoundsList
            )
        }

        return annotatedSpriteSheets
    }
}
