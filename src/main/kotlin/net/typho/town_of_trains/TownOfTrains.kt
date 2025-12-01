package net.typho.town_of_trains

import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.typho.town_of_trains.config.ConfigOption
import net.typho.town_of_trains.config.TownOfTrainsConfig
import net.typho.town_of_trains.packet.ConfigChangePacket

object TownOfTrains : ModInitializer {
    const val MOD_ID = "town_of_trains"

    //val TEST_ROLE = TMMRoles.registerRole(Role(id("test"), 0xFF7F3F, false, true, Role.MoodType.NONE, -1, true))

    override fun onInitialize() {
        PayloadTypeRegistry.playC2S().register(ConfigChangePacket.ID, ConfigChangePacket.CODEC)
        PayloadTypeRegistry.playS2C().register(ConfigChangePacket.ID, ConfigChangePacket.CODEC)
        ServerPlayNetworking.registerGlobalReceiver(ConfigChangePacket.ID) { packet, context ->
            if (packet.display) {
                packet.changes.forEach { option ->
                    context.server().sendMessage(option.getChangeText())
                }
            }

            context.server().playerManager.playerList.forEach { player ->
                ServerPlayNetworking.send(player, ConfigChangePacket(true, packet.changes))
            }

            TownOfTrainsConfig.save()
        }
        ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->
            ServerPlayNetworking.send(handler.player, ConfigChangePacket(false, ConfigOption.ALL_OPTIONS.values.toList()))
        }
        ServerLifecycleEvents.SERVER_STARTED.register { TownOfTrainsConfig.load() }
        ServerLifecycleEvents.SERVER_STOPPING.register { TownOfTrainsConfig.save() }
    }

    fun id(id: String) = Identifier.of(MOD_ID, id)

    fun PlayerEntity.getGameComponent(): GameWorldComponent = GameWorldComponent.KEY.get(world)
}
