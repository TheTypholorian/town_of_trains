package net.typho.town_of_trains.roles

import dev.doctor4t.trainmurdermystery.api.Role
import dev.doctor4t.trainmurdermystery.api.TMMRoles
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent
import net.minecraft.entity.player.PlayerEntity
import net.typho.town_of_trains.TownOfTrains

object ModRoles {
    fun init() = Unit

    val KILLER = KillerRole(TMMRoles.KILLER.identifier(), RoleType.KILLER, TMMRoles.KILLER)
    val TASKMASTER = TaskmasterRole(TownOfTrains.id("taskmaster"), RoleType.KILLER, TMMRoles.KILLER)

    val CIVILIAN = CivilianRole(TMMRoles.CIVILIAN.identifier(), RoleType.CIVILIAN, TMMRoles.CIVILIAN)
    val TICKET_INSPECTOR = TicketInspectorRole(TownOfTrains.id("ticket_inspector"), RoleType.CIVILIAN, TMMRoles.CIVILIAN)
    val BARTENDER = BartenderRole(TownOfTrains.id("bartender"), RoleType.CIVILIAN, TMMRoles.CIVILIAN)

    val VIGILANTE = VigilanteRole(TMMRoles.VIGILANTE.identifier(), RoleType.VIGILANTE, TMMRoles.VIGILANTE)

    fun PlayerEntity.getRole(): TownOfTrainsRole? {
        return (GameWorldComponent.KEY.get(this.world).roles[this.uuid] ?: return null).getAttachedRole()
    }

    fun PlayerEntity.setRole(role: TownOfTrainsRole) {
        (GameWorldComponent.KEY.get(this.world).roles[this.uuid] ?: return).setAttachedRole(role)
    }

    fun Role.getAttachedRole(): TownOfTrainsRole? {
        return (this as RoleAttacher).`town_of_trains$getRole`()
    }

    fun Role.setAttachedRole(role: TownOfTrainsRole) {
        (this as RoleAttacher).`town_of_trains$setRole`(role)
    }
}