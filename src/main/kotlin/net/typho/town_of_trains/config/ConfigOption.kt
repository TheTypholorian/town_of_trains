package net.typho.town_of_trains.config

import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import io.netty.buffer.ByteBuf
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.typho.town_of_trains.HasName
import net.typho.town_of_trains.TownOfTrains
import net.typho.town_of_trains.packet.ConfigChangePacket
import java.util.function.BiFunction
import kotlin.enums.enumEntries

open class ConfigOption<T>(
    var id: Identifier,
    var codec: Codec<T>,
    var packetCodec: PacketCodec<ByteBuf, T>,
    var toText: ToText<T>,
    var cycle: BiFunction<T, ConfigWidget.CycleType, T>,
    var value: T,
    var parent: ConfigSection? = null,
    val env: EnvType = EnvType.SERVER
) : ConfigWidget {
    override fun getName(): Text = Text.translatable(id.toTranslationKey("config.option"))

    override fun getDesc(): Text = Text.translatable(id.toTranslationKey("config.option", "desc"))

    fun isButtonHovered(info: ConfigWidget.DrawInfo): Boolean {
        val buttonX = info.x + info.contentWidth - info.buttonWidth - info.margin * 2
        val buttonY = info.y

        return info.mouseX >= buttonX && info.mouseX <= buttonX + info.buttonWidth &&
                info.mouseY >= buttonY && info.mouseY <= buttonY + info.buttonHeight
    }

    @Environment(EnvType.CLIENT)
    fun cycle(type: ConfigWidget.CycleType, client: MinecraftClient?) {
        value = cycle.apply(value, type)
        client?.soundManager?.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1f))
        client?.networkHandler?.sendPacket(CustomPayloadC2SPacket(ConfigChangePacket(false, listOf(this))))
    }

    @Environment(EnvType.CLIENT)
    override fun mouseClicked(info: ConfigWidget.DrawInfo): Boolean {
        if (isButtonHovered(info)) {
            cycle(ConfigWidget.CycleType.CLICK, info.client)
            return true
        }

        return false
    }

    @Environment(EnvType.CLIENT)
    override fun mouseScrolled(info: ConfigWidget.DrawInfo, horizontal: Double, vertical: Double): Boolean {
        if (vertical != 0.0 && isButtonHovered(info)) {
            cycle(if (vertical > 0.0) ConfigWidget.CycleType.SCROLL_UP else ConfigWidget.CycleType.SCROLL_DOWN, info.client)
            return true
        }

        return false
    }

    @Environment(EnvType.CLIENT)
    override fun draw(info: ConfigWidget.DrawInfo) {
        val name = getName()
        val textX = info.x + info.margin * 2
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
            val desc = getValueDesc()

            if (desc != null) {
                info.context?.drawTooltip(info.textRenderer, desc, info.mouseX, info.mouseY)
            }
        }
    }

    @Environment(EnvType.CLIENT)
    override fun postVisit(info: ConfigWidget.DrawInfo) {
        info.y += info.optionHeight + info.margin
    }

    override fun getKey(): Identifier = id

    fun getValueText(): Text = toText.toText(value)

    fun getValueDesc(): Text? = toText.toDesc(value)

    fun getChangeText(): Text = Text.translatable(
        "config.option.changed_text",
        if (parent == null) getName()
        else Text.translatable("config.option.parented", parent!!.getName(), getName()),
        getValueText()
    )

    fun encode(buf: ByteBuf) {
        packetCodec.encode(buf, value)
    }

    fun decode(buf: ByteBuf) {
        value = packetCodec.decode(buf)
    }

    fun encode(): JsonElement {
        return codec.encodeStart(JsonOps.INSTANCE, value).orThrow
    }

    fun decode(json: JsonElement) {
        value = codec.decode(JsonOps.INSTANCE, json).orThrow.first
    }

    companion object {
        fun ofInt(id: Identifier, min: Int, max: Int, inc: Int, value: Int) = ConfigOption(
            id,
            Codec.INT,
            PacketCodecs.INTEGER,
            ToText.ToString(),
            { v, type ->
                when (type) {
                    ConfigWidget.CycleType.CLICK, ConfigWidget.CycleType.SCROLL_UP -> MathHelper.clamp(v + inc, min, max)
                    ConfigWidget.CycleType.SCROLL_DOWN -> MathHelper.clamp(v - inc, min, max)
                }
            },
            value
        )

        fun ofBool(id: Identifier, value: Boolean) = ConfigOption(
            id,
            Codec.BOOL,
            PacketCodecs.BOOL,
            ToText.ToString(),
            { b, type -> !b },
            value
        )

        fun ofFloat(id: Identifier, min: Float, max: Float, inc: Float, value: Float) = ConfigOption(
            id,
            Codec.FLOAT,
            PacketCodecs.FLOAT,
            ToText.ToString(),
            { v, type ->
                when (type) {
                    ConfigWidget.CycleType.CLICK, ConfigWidget.CycleType.SCROLL_UP -> MathHelper.clamp(v + inc, min, max)
                    ConfigWidget.CycleType.SCROLL_DOWN -> MathHelper.clamp(v - inc, min, max)
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
                        ConfigWidget.CycleType.CLICK, ConfigWidget.CycleType.SCROLL_UP -> {
                            val entries = enumEntries<T>()
                            if (v.ordinal == entries.size - 1) entries.first() else entries[v.ordinal + 1]
                        }
                        ConfigWidget.CycleType.SCROLL_DOWN -> {
                            val entries = enumEntries<T>()
                            if (v.ordinal == 0) entries.last() else entries[v.ordinal - 1]
                        }
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
