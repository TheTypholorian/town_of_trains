package net.typho.town_of_trains.roles

import dev.doctor4t.trainmurdermystery.api.Role
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent
import dev.doctor4t.trainmurdermystery.cca.PlayerMoodComponent
import dev.doctor4t.trainmurdermystery.cca.PlayerShopComponent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.typho.town_of_trains.TownOfTrains
import net.typho.town_of_trains.config.ConfigOption

open class TaskmasterRole(id: Identifier, type: RoleType, role: Role) : KillerRole(id, type, role) {
    val coinsPerTask = ConfigOption.ofInt(TownOfTrains.id("coins_per_task"), 5, 200, 5, 100)

    init {
        addChild(coinsPerTask)
    }

    override fun onTaskCompleted(player: PlayerEntity, task: PlayerMoodComponent.Task, game: GameWorldComponent) {
        PlayerShopComponent.KEY.get(player).addToBalance(coinsPerTask.value)
    }

    override fun hasIdleMoney(player: PlayerEntity, game: GameWorldComponent): Boolean = false
}