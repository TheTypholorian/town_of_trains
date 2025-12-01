package net.typho.town_of_trains.roles

import dev.doctor4t.trainmurdermystery.api.Role
import dev.doctor4t.trainmurdermystery.cca.PlayerMoodComponent
import dev.doctor4t.trainmurdermystery.cca.PlayerShopComponent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.typho.town_of_trains.config.ConfigOption

class TaskmasterRole(id: Identifier, role: Role) : TownOfTrainsRole(id, role) {
    val coinsPerTask = ConfigOption.ofInt(info.identifier().withSuffixedPath("/coins_per_task"), 5, 200, 5, 100)

    override fun onTaskCompleted(player: PlayerEntity, task: PlayerMoodComponent.Task) {
        PlayerShopComponent.KEY.get(player).addToBalance(coinsPerTask.value)
    }

    override fun hasIdleMoney(player: PlayerEntity): Boolean = false
}