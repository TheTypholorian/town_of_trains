package net.typho.town_of_trains.roles

import dev.doctor4t.wathe.api.Role
import net.minecraft.util.Identifier

open class CivilianRole(id: Identifier, type: RoleType, role: Role) : AbstractRole(id, type, role)