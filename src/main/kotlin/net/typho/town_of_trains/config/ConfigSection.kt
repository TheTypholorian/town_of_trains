package net.typho.town_of_trains.config

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

open class ConfigSection(
    var id: Identifier,
    var children: List<ConfigOption<*>>,
    var parent: ConfigTab? = null
) : ConfigWidget {
    init {
        children.forEach { child -> child.parent = this }
    }

    fun addChild(option: ConfigOption<*>) {
        if (option.parent != null && option.parent != this) {
            throw IllegalStateException("Can't add config option " + option.id + " to multiple sections")
        }

        children += option
        option.parent = this
    }

    override fun getName(): Text {
        return Text.translatable(id.toTranslationKey("config.section"))
    }

    override fun getDesc(): Text {
        return Text.translatable(id.toTranslationKey("config.section", "desc"))
    }

    override fun getKey(): Identifier = id

    @Environment(EnvType.CLIENT)
    override fun draw(info: ConfigWidget.DrawInfo) {
        info.context?.drawText(info.textRenderer, getName().copy().formatted(Formatting.BOLD), info.x, info.y, -1, true)
    }

    @Environment(EnvType.CLIENT)
    override fun postVisit(info: ConfigWidget.DrawInfo) {
        info.y += info.fontHeight
    }
}
