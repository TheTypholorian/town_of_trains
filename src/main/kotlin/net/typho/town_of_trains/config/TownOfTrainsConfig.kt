package net.typho.town_of_trains.config

import net.minecraft.entity.player.PlayerEntity
import net.typho.town_of_trains.TownOfTrains

object TownOfTrainsConfig {
    var chatEnabled = ConfigOption.ofEnum<PlayerPositionType>(TownOfTrains.id("chat_enabled"), PlayerPositionType.NOT_PARTICIPANT)
    var perspectiveLocked = ConfigOption.ofEnum<PlayerPositionType>(TownOfTrains.id("perspective_locked"), PlayerPositionType.GAME_ACTIVE)

    var restrictions = ConfigSection(TownOfTrains.id("restrictions"), listOf(
        chatEnabled, perspectiveLocked
    ))

    var gameplay = ConfigTab(TownOfTrains.id("gameplay"), listOf(
        restrictions
    ))

    var tabs = listOf(
        gameplay
    )

    fun isChatEnabled(player: PlayerEntity?): Boolean = chatEnabled.value.test(player)

    fun isPerspectiveLocked(player: PlayerEntity?): Boolean = perspectiveLocked.value.test(player)
}