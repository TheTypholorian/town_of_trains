package net.typho.town_of_trains.gui

import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.typho.town_of_trains.TownOfTrains
import net.typho.town_of_trains.config.ConfigWidget
import net.typho.town_of_trains.config.TownOfTrainsConfig
import java.util.function.BiConsumer

@Environment(EnvType.CLIENT)
class TownConfigScreen : Screen(Text.translatable("gui.town_of_trains.config")) {
    var tab = TownOfTrainsConfig.gameplay

    fun getWidth() = 250

    fun getHeight() = 500.coerceAtMost(height - 8)

    fun visitWidgets(info: ConfigWidget.DrawInfo, out: BiConsumer<ConfigWidget, ConfigWidget.DrawInfo>) {
        val x = info.x
        val y = info.y

        TownOfTrainsConfig.tabs.forEach { tab ->
            out.accept(tab, info)
            tab.postVisit(info)
        }

        info.x = x + info.margin * 2
        info.y = y + info.margin * 2

        tab.children.forEach { section ->
            out.accept(section, info)
            section.postVisit(info)

            section.children.forEach { option ->
                out.accept(option, info)
                option.postVisit(info)
            }
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val info = ConfigWidget.DrawInfo(
            screen = this,
            client = client,
            context = null,
            x = (this.width - getWidth()) / 2,
            y = (this.height - getHeight()) / 2,
            mouseX = mouseX.toInt(),
            mouseY = mouseY.toInt(),
            textRenderer = textRenderer
        )
        info.fontHeight = client?.textRenderer?.fontHeight ?: 9
        info.optionHeight = info.fontHeight.coerceAtLeast(info.buttonHeight)
        info.contentWidth = getWidth() - info.margin * 2

        var clicked = false

        visitWidgets(info) { widget, info1 ->
            if (widget.mouseClicked(info1)) {
                clicked = true
            }
        }

        return clicked
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontal: Double,
        vertical: Double
    ): Boolean {
        val info = ConfigWidget.DrawInfo(
            screen = this,
            client = client,
            context = null,
            x = (this.width - getWidth()) / 2,
            y = (this.height - getHeight()) / 2,
            mouseX = mouseX.toInt(),
            mouseY = mouseY.toInt(),
            textRenderer = textRenderer
        )
        info.fontHeight = client?.textRenderer?.fontHeight ?: 9
        info.optionHeight = info.fontHeight.coerceAtLeast(info.buttonHeight)
        info.contentWidth = getWidth() - info.margin * 2

        var scrolled = false

        visitWidgets(info) { widget, info1 ->
            if (widget.mouseScrolled(info1, horizontal, vertical)) {
                scrolled = true
            }
        }

        return scrolled
    }

    override fun renderBackground(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.renderBackground(context, mouseX, mouseY, delta)

        val info = ConfigWidget.DrawInfo(
            screen = this,
            client = client,
            context = context,
            x = (this.width - getWidth()) / 2,
            y = (this.height - getHeight()) / 2,
            mouseX = mouseX,
            mouseY = mouseY,
            textRenderer = textRenderer
        )
        info.fontHeight = client?.textRenderer?.fontHeight ?: 9
        info.optionHeight = info.fontHeight.coerceAtLeast(info.buttonHeight)
        info.contentWidth = getWidth() - info.margin * 2

        RenderSystem.enableBlend()

        info.context?.drawGuiTexture(TownOfTrains.id("echo_border"), info.x, info.y, getWidth(), getHeight())

        visitWidgets(info) { widget, info1 ->
            widget.draw(info1)
        }

        RenderSystem.disableBlend()
    }
}