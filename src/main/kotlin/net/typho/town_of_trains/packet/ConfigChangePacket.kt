package net.typho.town_of_trains.packet

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier
import net.typho.town_of_trains.TownOfTrains
import net.typho.town_of_trains.config.ConfigOption
import java.util.*

data class ConfigChangePacket(
    var display: Boolean,
    var changes: List<ConfigOption<*>>
) : CustomPayload {
    override fun getId(): CustomPayload.Id<out CustomPayload?>? = ID

    companion object {
        val ID = CustomPayload.Id<ConfigChangePacket>(TownOfTrains.id("config_change"))
        val CODEC: PacketCodec<ByteBuf, ConfigChangePacket> = object : PacketCodec<ByteBuf, ConfigChangePacket> {
            override fun decode(buf: ByteBuf): ConfigChangePacket? {
                val list = LinkedList<ConfigOption<*>>()
                val display = buf.readBoolean()
                val size = buf.readInt()

                repeat(size) {
                    var option = ConfigOption.ALL_OPTIONS.get(Identifier.PACKET_CODEC.decode(buf))!!
                    option.decode(buf)
                    list.add(option)
                }

                return ConfigChangePacket(display, list)
            }

            override fun encode(buf: ByteBuf, packet: ConfigChangePacket) {
                buf.writeBoolean(packet.display)
                buf.writeInt(packet.changes.size)

                packet.changes.forEach { option ->
                    Identifier.PACKET_CODEC.encode(buf, option.id)
                    option.encode(buf)
                }
            }
        }
    }
}