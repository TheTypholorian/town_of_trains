package net.typho.town_of_trains.config

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Identifier
import net.typho.town_of_trains.HasName

interface ConfigWidget : HasName {
    fun getKey(): Identifier

    fun mouseClicked(info: DrawInfo): Boolean = false

    fun mouseScrolled(info: DrawInfo, horizontal: Double, vertical: Double): Boolean = false

    fun draw(info: DrawInfo)

    fun postVisit(info: DrawInfo)

    enum class CycleType {
        CLICK, SCROLL_UP, SCROLL_DOWN
    }

    data class DrawInfo(
        val client: MinecraftClient?,
        val context: DrawContext?,
        var x: Int,
        var y: Int,
        var mouseX: Int,
        var mouseY: Int,
        var margin: Int = 4,
        var fontHeight: Int = 9,
        var textRenderer: TextRenderer?,

        var optionHeight: Int = 16,
        var buttonWidth: Int = 100,
        var buttonHeight: Int = 16,
        var contentWidth: Int = 100
    )
}