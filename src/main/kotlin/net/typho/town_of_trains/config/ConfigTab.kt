package net.typho.town_of_trains.config

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.typho.town_of_trains.TownOfTrains

open class ConfigTab(
    var id: Identifier,
    var children: List<ConfigSection>,
    val env: EnvType = EnvType.SERVER
) : ConfigWidget {
    init {
        children.forEach { child -> child.parent = this }
    }

    fun addChild(section: ConfigSection) {
        if (section.parent != null && section.parent != this) {
            throw IllegalStateException("Can't add config section " + section.id + " to multiple tabs")
        }

        children += section
        section.parent = this
    }

    override fun getName(): Text {
        return Text.translatable(id.toTranslationKey("config.tab"))
    }

    override fun getDesc(): Text {
        return Text.translatable(id.toTranslationKey("config.tab", "desc"))
    }

    override fun getKey(): Identifier = id

    fun isButtonHovered(info: ConfigWidget.DrawInfo): Boolean {
        val buttonX = info.x - info.buttonWidth - info.margin
        val buttonY = info.y

        return info.mouseX >= buttonX && info.mouseX <= buttonX + info.buttonWidth &&
                info.mouseY >= buttonY && info.mouseY <= buttonY + info.tabHeight
    }

    @Environment(EnvType.CLIENT)
    override fun mouseClicked(info: ConfigWidget.DrawInfo): Boolean {
        if (isButtonHovered(info)) {
            info.screen.tab = this
            info.client?.soundManager?.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1f))
            return true
        }

        return false
    }

    @Environment(EnvType.CLIENT)
    override fun mouseScrolled(info: ConfigWidget.DrawInfo, horizontal: Double, vertical: Double): Boolean {
        if (vertical != 0.0 && isButtonHovered(info)) {
            var index = TownOfTrainsConfig.tabs.indexOf(info.screen.tab)

            if (vertical > 0) {
                index--

                if (index < 0) {
                    index = TownOfTrainsConfig.tabs.size - 1
                }
            } else {
                index++

                if (index >= TownOfTrainsConfig.tabs.size) {
                    index = 0
                }
            }

            info.screen.tab = TownOfTrainsConfig.tabs[index]
            info.client?.soundManager?.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1f))

            return true
        }

        return false
    }

    @Environment(EnvType.CLIENT)
    override fun draw(info: ConfigWidget.DrawInfo) {
        val textY = info.y + Math.ceilDiv(info.tabHeight - info.fontHeight, 2)

        val buttonX = info.x - info.buttonWidth - info.margin

        info.context?.drawGuiTexture(
            TownOfTrains.id(if (isButtonHovered(info) || info.screen.tab === this) "echo_button_on" else "echo_button_off"),
            buttonX,
            info.y,
            info.buttonWidth,
            info.tabHeight
        )

        info.context?.drawCenteredTextWithShadow(
            info.textRenderer,
            getName(),
            buttonX + info.buttonWidth / 2,
            textY,
            -1
        )
    }

    @Environment(EnvType.CLIENT)
    override fun postVisit(info: ConfigWidget.DrawInfo) {
        info.y += info.tabHeight + info.margin
    }
}
