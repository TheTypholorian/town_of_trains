package net.typho.town_of_trains.roles

import dev.doctor4t.trainmurdermystery.api.TMMRoles
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent
import net.minecraft.entity.player.PlayerEntity
import net.typho.town_of_trains.TownOfTrains

object ModRoles {
    fun init() = Unit

    val TASKMASTER = TaskmasterRole(TownOfTrains.id("taskmaster"), TMMRoles.KILLER)

    fun PlayerEntity.getRole(): TownOfTrainsRole? {
        return (GameWorldComponent.KEY.get(world).roles.get(uuid) as RoleAttacher).`town_of_trains$getRole`()
    }

    fun PlayerEntity.setRole(role: TownOfTrainsRole) {
        (GameWorldComponent.KEY.get(world).roles.get(uuid) as RoleAttacher).`town_of_trains$setRole`(role)
    }
}