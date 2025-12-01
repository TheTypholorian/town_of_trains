package net.typho.town_of_trains

import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent
import net.fabricmc.api.ModInitializer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier

object TownOfTrains : ModInitializer {
    const val MOD_ID = "town_of_trains"

    //val TEST_ROLE = TMMRoles.registerRole(Role(id("test"), 0xFF7F3F, false, true, Role.MoodType.NONE, -1, true))

    override fun onInitialize() {
    }

    fun id(id: String) = Identifier.of(MOD_ID, id)

    fun PlayerEntity.getGameComponent(): GameWorldComponent = GameWorldComponent.KEY.get(world)
}
