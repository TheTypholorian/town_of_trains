package net.typho.town_of_trains.config

import com.mojang.serialization.Codec
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.typho.town_of_trains.HasName
import net.typho.town_of_trains.TownOfTrains
import java.util.function.BiFunction
import java.util.function.Function
import kotlin.enums.enumEntries

data class ConfigOption<T>(
    var id: Identifier,
    var codec: Codec<T>,
    var packetCodec: PacketCodec<*, T>,
    var toText: ToText<T>,
    var cycle: BiFunction<T, ConfigWidget.CycleType, T>,
    var value: T
) : ConfigWidget {
    override fun getName(): Text = Text.translatable(id.toTranslationKey("config.option"))

    override fun getDesc(): Text = Text.translatable(id.toTranslationKey("config.option", "desc"))

    fun isButtonHovered(info: ConfigWidget.DrawInfo): Boolean {
        val buttonX = info.x + info.contentWidth - info.buttonWidth - info.margin * 2
        val buttonY = info.y

        return info.mouseX >= buttonX && info.mouseX <= buttonX + info.buttonWidth &&
                info.mouseY >= buttonY && info.mouseY <= buttonY + info.buttonHeight
    }

    override fun mouseClicked(info: ConfigWidget.DrawInfo): Boolean {
        if (isButtonHovered(info)) {
            value = cycle.apply(value, ConfigWidget.CycleType.CLICK)
            info.client?.soundManager?.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1f))
            return true
        }

        return false
    }

    override fun draw(info: ConfigWidget.DrawInfo) {
        val name = getName()
        val textX = info.x + info.margin
        val textY = info.y + Math.ceilDiv(info.optionHeight - info.fontHeight, 2)

        info.context?.drawText(info.textRenderer, name, textX, textY, -1, true)

        val lineX = textX + (info.textRenderer?.getWidth(name) ?: 50) + info.margin
        val lineY = info.y + Math.floorDiv(info.optionHeight, 2)

        val buttonX = info.x + info.contentWidth - info.buttonWidth - info.margin * 2

        info.context?.fill(lineX, lineY, buttonX - info.margin, lineY + 1, 0xFFA0A0A0.toInt())

        val hovered = isButtonHovered(info)

        info.context?.drawGuiTexture(
            TownOfTrains.id(if (hovered) "echo_button_on" else "echo_button_off"),
            buttonX,
            info.y,
            info.buttonWidth,
            info.buttonHeight
        )

        info.context?.drawCenteredTextWithShadow(
            info.textRenderer,
            getValueText(),
            buttonX + info.buttonWidth / 2,
            textY,
            -1
        )

        if (hovered) {
            info.context?.drawTooltip(info.textRenderer, toText.toDesc(value), info.mouseX, info.mouseY)
        }
    }

    override fun postVisit(info: ConfigWidget.DrawInfo) {
        info.y += info.optionHeight + info.margin
    }

    override fun getKey(): Identifier = id

    fun getValueText(): Text = toText.toText(value)

    fun getValueDesc(): Text? = toText.toDesc(value)

    companion object {
        fun ofInt(id: Identifier, min: Int, max: Int, value: Int = min) = ConfigOption(
            id,
            Codec.INT,
            PacketCodecs.INTEGER,
            ToText.ToString(),
            { v, type ->
                when (type) {
                    ConfigWidget.CycleType.CLICK, ConfigWidget.CycleType.SCROLL_UP -> MathHelper.clamp(v + 1, min, max)
                    ConfigWidget.CycleType.SCROLL_DOWN -> MathHelper.clamp(v - 1, min, max)
                }
            },
            value
        )

        fun ofBool(id: Identifier, value: Boolean = true) = ConfigOption(
            id,
            Codec.BOOL,
            PacketCodecs.BOOL,
            ToText.ToString(),
            { b, type -> !b },
            value
        )

        fun ofFloat(id: Identifier, min: Float, max: Float, value: Float = 0f) = ConfigOption(
            id,
            Codec.FLOAT,
            PacketCodecs.FLOAT,
            ToText.ToString(),
            { v, type ->
                when (type) {
                    ConfigWidget.CycleType.CLICK, ConfigWidget.CycleType.SCROLL_UP -> MathHelper.clamp(v + 1, min, max)
                    ConfigWidget.CycleType.SCROLL_DOWN -> MathHelper.clamp(v - 1, min, max)
                }
            },
            value
        )

        inline fun <reified T : Enum<T>> ofEnum(id: Identifier, toText: ToText<T>, value: T): ConfigOption<T> {
            return ConfigOption(
                id,
                Codec.STRING.xmap(
                    { v -> enumValueOf<T>(v) },
                    { v -> v.name }
                ),
                PacketCodec.tuple(
                    PacketCodecs.INTEGER,
                    { v: T -> v.ordinal },
                    { v: Int -> enumEntries<T>()[v] }
                ),
                toText,
                { v, type ->
                    when (type) {
                        ConfigWidget.CycleType.CLICK, ConfigWidget.CycleType.SCROLL_UP -> enumEntries<T>()[(v.ordinal + 1) % enumEntries<T>().size]
                        ConfigWidget.CycleType.SCROLL_DOWN -> enumEntries<T>()[(v.ordinal - 1) % enumEntries<T>().size]
                    }
                },
                value
            )
        }

        inline fun <reified T> ofEnum(id: Identifier, value: T): ConfigOption<T> where T : Enum<T>, T : HasName {
            return ofEnum(id, ToText.HasName(), value)
        }
    }
}
