package net.typho.town_of_trains.config

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Identifier
import net.typho.town_of_trains.HasName
import net.typho.town_of_trains.gui.TownConfigScreen

interface ConfigWidget : HasName {
    fun getKey(): Identifier

    @Environment(EnvType.CLIENT)
    fun mouseClicked(info: DrawInfo): Boolean = false

    @Environment(EnvType.CLIENT)
    fun mouseScrolled(info: DrawInfo, horizontal: Double, vertical: Double): Boolean = false

    @Environment(EnvType.CLIENT)
    fun draw(info: DrawInfo)

    @Environment(EnvType.CLIENT)
    fun postVisit(info: DrawInfo)

    @Environment(EnvType.CLIENT)
    enum class CycleType {
        CLICK, SCROLL_UP, SCROLL_DOWN
    }

    @Environment(EnvType.CLIENT)
    data class DrawInfo(
        val screen: TownConfigScreen,
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
        var tabHeight: Int = 20,
        var contentWidth: Int = 100
    )
}