package net.typho.town_of_trains.config

import net.minecraft.text.Text
import net.minecraft.util.Identifier

data class ConfigTab(
    var id: Identifier,
    var children: List<ConfigSection>
) : ConfigWidget {
    override fun getName(): Text {
        return Text.translatable(id.toTranslationKey("config.tab"))
    }

    override fun getDesc(): Text {
        return Text.translatable(id.toTranslationKey("config.tab", "desc"))
    }

    override fun getKey(): Identifier = id

    override fun mouseClicked(info: ConfigWidget.DrawInfo): Boolean {
        return false
    }

    override fun draw(info: ConfigWidget.DrawInfo) {
    }

    override fun postVisit(info: ConfigWidget.DrawInfo) {
    }
}
