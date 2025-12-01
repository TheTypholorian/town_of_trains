package net.typho.town_of_trains.config

import net.minecraft.entity.player.PlayerEntity
import net.typho.town_of_trains.TownOfTrains

object TownOfTrainsConfig {
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

    fun isChatEnabled(player: PlayerEntity?): Boolean = chat.value.test(player)

    fun isPerspectiveEnabled(player: PlayerEntity?): Boolean = perspective.value.test(player)

    fun isDebugScreenEnabled(player: PlayerEntity?): Boolean = debugScreen.value.test(player)

    fun areNameTagsEnabled(player: PlayerEntity?): Boolean = nameTags.value.test(player)

    fun isSprintingEnabled(player: PlayerEntity?): Boolean = sprinting.value.test(player)

    fun isJumpingEnabled(player: PlayerEntity?): Boolean = jumping.value.test(player)
}