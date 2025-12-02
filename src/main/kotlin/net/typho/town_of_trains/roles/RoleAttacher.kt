package net.typho.town_of_trains.roles

interface RoleAttacher {
    fun `town_of_trains$getRole`(): AbstractRole?

    fun `town_of_trains$setRole`(role: AbstractRole)
}