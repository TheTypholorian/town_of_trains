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

    var tabs = listOf(
        gameplay
    )

    fun isChatEnabled(player: PlayerEntity?): Boolean = chat.value.test(player)

    fun isPerspectiveEnabled(player: PlayerEntity?): Boolean = perspective.value.test(player)

    fun isDebugScreenEnabled(player: PlayerEntity?): Boolean = debugScreen.value.test(player)

    fun areNameTagsEnabled(player: PlayerEntity?): Boolean = nameTags.value.test(player)

    fun isSprintingEnabled(player: PlayerEntity?): Boolean = sprinting.value.test(player)

    fun isJumpingEnabled(player: PlayerEntity?): Boolean = jumping.value.test(player)
}