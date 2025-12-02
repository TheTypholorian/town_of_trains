package net.typho.town_of_trains.roles

import net.typho.town_of_trains.config.ConfigTab
import net.typho.town_of_trains.config.TownOfTrainsConfig

enum class RoleType(val configTab: ConfigTab) {
    CIVILIAN(TownOfTrainsConfig.civilianRoles),
    VIGILANTE(TownOfTrainsConfig.vigilanteRoles),
    KILLER(TownOfTrainsConfig.killerRoles),
    NEUTRAL(TownOfTrainsConfig.neutralRoles)
}