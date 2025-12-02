package net.typho.town_of_trains.roles

import net.typho.town_of_trains.config.ConfigTab
import net.typho.town_of_trains.config.TownOfTrainsConfig

enum class RoleType(val configTab: ConfigTab, val defaultRole: AbstractRole) {
    CIVILIAN(TownOfTrainsConfig.civilianRoles, ModRoles.CIVILIAN),
    VIGILANTE(TownOfTrainsConfig.vigilanteRoles, ModRoles.VIGILANTE),
    KILLER(TownOfTrainsConfig.killerRoles, ModRoles.KILLER),
    NEUTRAL(TownOfTrainsConfig.neutralRoles, ModRoles.CIVILIAN)
}