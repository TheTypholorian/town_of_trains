package net.typho.town_of_trains.config

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.typho.town_of_trains.TownOfTrains
import net.typho.town_of_trains.packet.ConfigChangePacket
import java.nio.file.Files
import java.nio.file.Path

object TownOfTrainsConfig {
    const val FILE_NAME = "town_of_trains.json"

    val chat = ConfigOption.ofEnum<PlayerPositionType>(TownOfTrains.id("chat"), PlayerPositionType.NOT_PARTICIPANT)
    val perspective = ConfigOption.ofEnum<PlayerPositionType>(TownOfTrains.id("perspective"), PlayerPositionType.NOT_PARTICIPANT)
    val debugScreen = ConfigOption.ofEnum<PlayerPositionType>(TownOfTrains.id("debug_screen"), PlayerPositionType.NOT_PARTICIPANT)
    val nameTags = ConfigOption.ofEnum<PlayerPositionType>(TownOfTrains.id("name_tags"), PlayerPositionType.NEVER)
    val sprinting = ConfigOption.ofEnum<PlayerPositionType>(TownOfTrains.id("sprinting"), PlayerPositionType.ALWAYS)
    val jumping = ConfigOption.ofEnum<PlayerPositionType>(TownOfTrains.id("jumping"), PlayerPositionType.NEVER)

    val restrictions = ConfigSection(TownOfTrains.id("restrictions"), listOf(
        chat, perspective, debugScreen, nameTags, sprinting, jumping
    ))

    val gameplay = ConfigTab(TownOfTrains.id("gameplay"), listOf(
        restrictions
    ))
    val killerRoles = ConfigTab(TownOfTrains.id("killer_roles"), listOf())
    val vigilanteRoles = ConfigTab(TownOfTrains.id("vigilante_roles"), listOf())
    val civilianRoles = ConfigTab(TownOfTrains.id("civilian_roles"), listOf())
    val neutralRoles = ConfigTab(TownOfTrains.id("neutral_roles"), listOf())

    val tabs = listOf(
        gameplay,
        killerRoles,
        vigilanteRoles,
        civilianRoles,
        neutralRoles
    )

    fun init() {
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

            save()
        }
        ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->
            updateToClient(handler.player, false)
        }
        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            load()

            for (player in server.playerManager.playerList) {
                updateToClient(player, false)
            }
        }
        ServerLifecycleEvents.SERVER_STOPPING.register { save() }
    }

    fun getConfigFile(): Path {
        val path = FabricLoader.getInstance().configDir.resolve(FILE_NAME)

        if (Files.notExists(path)) {
            Files.createFile(path)
        }

        return path
    }

    fun updateToClient(player: ServerPlayerEntity, display: Boolean) {
        ServerPlayNetworking.send(
            player,
            ConfigChangePacket(
                display,
                tabs.stream()
                    .flatMap { tab -> tab.children.stream() }
                    .flatMap { section -> section.children.stream() }
                    .toList()
            )
        )
    }

    fun save() {
        val path = getConfigFile()
        Files.newBufferedWriter(path).use { writer ->
            val json = JsonObject()

            tabs.forEach { tab ->
                val tabJson = JsonObject()

                tab.children.forEach { section ->
                    val sectionJson = JsonObject()

                    section.children.forEach { option ->
                        sectionJson.add(option.id.toString(), option.encode())
                    }

                    tabJson.add(section.id.toString(), sectionJson)
                }

                json.add(tab.id.toString(), tabJson)
            }

            writer.write(GsonBuilder().setPrettyPrinting().create().toJson(json))
        }
    }

    fun load() {
        val path = getConfigFile()
        Files.newBufferedReader(path).use { reader ->
            val json = JsonParser.parseReader(reader)?.asJsonObject ?: return

            tabs.forEach { tab ->
                val tabJson = json.getAsJsonObject(tab.id.toString())

                if (tabJson != null) {
                    tab.children.forEach { section ->
                        val sectionJson = json.getAsJsonObject(section.id.toString())

                        if (sectionJson != null) {
                            section.children.forEach { option ->
                                val optionJson = json.get(option.id.toString())

                                if (optionJson != null) {
                                    option.decode(optionJson)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun isChatEnabled(player: PlayerEntity?): Boolean = chat.value.test(player)

    fun isPerspectiveEnabled(player: PlayerEntity?): Boolean = perspective.value.test(player)

    fun isDebugScreenEnabled(player: PlayerEntity?): Boolean = debugScreen.value.test(player)

    fun areNameTagsEnabled(player: PlayerEntity?): Boolean = nameTags.value.test(player)

    fun isSprintingEnabled(player: PlayerEntity?): Boolean = sprinting.value.test(player)

    fun isJumpingEnabled(player: PlayerEntity?): Boolean = jumping.value.test(player)
}