package net.typho.town_of_trains.roles

import dev.doctor4t.trainmurdermystery.api.TMMRoles
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent
import net.minecraft.entity.player.PlayerEntity
import net.typho.town_of_trains.TownOfTrains

object ModRoles {
    val TASKMASTER = TaskmasterRole(TownOfTrains.id("taskmaster"), TMMRoles.KILLER)
    val TICKET_INSPECTOR = TicketInspectorRole(TownOfTrains.id("ticket_inspector"), TMMRoles.CIVILIAN)

    fun init() {
        (TMMRoles.KILLER as RoleAttacher).`town_of_trains$setRole`(TASKMASTER)
        (TMMRoles.CIVILIAN as RoleAttacher).`town_of_trains$setRole`(TICKET_INSPECTOR)
    }

    fun PlayerEntity.getRole(): TownOfTrainsRole? {
        return ((GameWorldComponent.KEY.get(this.world).roles[this.uuid] ?: return null) as RoleAttacher).`town_of_trains$getRole`()
    }

    fun PlayerEntity.setRole(role: TownOfTrainsRole) {
        ((GameWorldComponent.KEY.get(this.world).roles[this.uuid] ?: return) as RoleAttacher).`town_of_trains$setRole`(role)
    }
}