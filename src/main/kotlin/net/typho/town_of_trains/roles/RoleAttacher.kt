package net.typho.town_of_trains.roles

interface RoleAttacher {
    fun `town_of_trains$getRole`(): TownOfTrainsRole?

    fun `town_of_trains$setRole`(role: TownOfTrainsRole)
}