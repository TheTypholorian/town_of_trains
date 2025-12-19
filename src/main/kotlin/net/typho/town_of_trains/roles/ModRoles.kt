package net.typho.town_of_trains.roles

import dev.doctor4t.wathe.api.Role
import dev.doctor4t.wathe.api.WatheRoles
import dev.doctor4t.wathe.cca.GameWorldComponent
import net.minecraft.entity.player.PlayerEntity
import net.typho.town_of_trains.TownOfTrains

object ModRoles {
    fun init() = Unit

    val KILLER = KillerRole(WatheRoles.KILLER.identifier(), RoleType.KILLER, WatheRoles.KILLER)
    val TASKMASTER = TaskmasterRole(TownOfTrains.id("taskmaster"), RoleType.KILLER, WatheRoles.KILLER)
    val MERCENARY = MercenaryRole(TownOfTrains.id("mercenary"), RoleType.KILLER, WatheRoles.KILLER)

    val CIVILIAN = CivilianRole(WatheRoles.CIVILIAN.identifier(), RoleType.CIVILIAN, WatheRoles.CIVILIAN)
    val TICKET_INSPECTOR = TicketInspectorRole(TownOfTrains.id("ticket_inspector"), RoleType.CIVILIAN, WatheRoles.CIVILIAN)
    val BARTENDER = BartenderRole(TownOfTrains.id("bartender"), RoleType.CIVILIAN, WatheRoles.CIVILIAN)

    val VIGILANTE = VigilanteRole(WatheRoles.VIGILANTE.identifier(), RoleType.VIGILANTE, WatheRoles.VIGILANTE)

    fun PlayerEntity.getRole(): AbstractRole? {
        return (GameWorldComponent.KEY.get(this.world).roles[this.uuid] ?: return null).getAttachedRole()
    }

    fun PlayerEntity.setRole(role: AbstractRole) {
        setRole(role.info)
    }

    fun PlayerEntity.setRole(role: Role) {
        GameWorldComponent.KEY.get(this.world).roles[this.uuid] = role
    }

    fun Role.getAttachedRole(): AbstractRole? {
        return (this as RoleAttacher).`town_of_trains$getRole`()
    }

    fun Role.setAttachedRole(role: AbstractRole) {
        (this as RoleAttacher).`town_of_trains$setRole`(role)
    }
}