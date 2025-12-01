package net.typho.town_of_trains.config

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.entity.player.PlayerEntity
import net.typho.town_of_trains.TownOfTrains
import java.nio.file.Files
import java.nio.file.Path

object TownOfTrainsConfig {
    const val FILE_NAME = "town_of_trains.json"

    var chat = ConfigOption.ofEnum<PlayerPositionType>(TownOfTrains.id("chat"), PlayerPositionType.NOT_PARTICIPANT)
    var perspective = ConfigOption.ofEnum<PlayerPositionType>(TownOfTrains.id("perspective"), PlayerPositionType.NOT_PARTICIPANT)
    var debugScreen = ConfigOption.ofEnum<PlayerPositionType>(TownOfTrains.id("debug_screen"), PlayerPositionType.NOT_PARTICIPANT)
    var nameTags = ConfigOption.ofEnum<PlayerPositionType>(TownOfTrains.id("name_tags"), PlayerPositionType.NEVER)
    var sprinting = ConfigOption.ofEnum<PlayerPositionType>(TownOfTrains.id("sprinting"), PlayerPositionType.ALWAYS)
    var jumping = ConfigOption.ofEnum<PlayerPositionType>(TownOfTrains.id("jumping"), PlayerPositionType.NEVER)

    var restrictions = ConfigSection(TownOfTrains.id("restrictions"), listOf(
        chat, perspective, debugScreen, nameTags, sprinting, jumping
    ))

    var gameplay = ConfigTab(TownOfTrains.id("gameplay"), listOf(
        restrictions
    ))
    var killerRoles = ConfigTab(TownOfTrains.id("killer_roles"), listOf())
    var vigilanteRoles = ConfigTab(TownOfTrains.id("vigilante_roles"), listOf())
    var civilianRoles = ConfigTab(TownOfTrains.id("civilian_roles"), listOf())

    var tabs = listOf(
        gameplay,
        killerRoles,
        vigilanteRoles,
        civilianRoles
    )

    fun getConfigFile(): Path {
        val path = FabricLoader.getInstance().configDir.resolve(FILE_NAME)

        if (Files.notExists(path)) {
            Files.createFile(path)
        }

        return path
    }

    fun save() {
        Files.newBufferedWriter(getConfigFile()).use { writer ->
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
        Files.newBufferedReader(getConfigFile()).use { reader ->
            val json = JsonParser.parseReader(reader)?.asJsonObject ?: return

            tabs.forEach { tab ->
                val tabJson = json.getAsJsonObject(tab.id.toString())

                if (tabJson != null) {
                    tab.children.forEach { section ->
                        val sectionJson = json.getAsJsonObject(section.id.toString())

                        if (sectionJson != null) {
                            section.children.forEach { option ->
                                val optionJson = json.get(section.id.toString())

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