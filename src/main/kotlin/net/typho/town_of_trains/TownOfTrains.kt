package net.typho.town_of_trains

import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent
import net.fabricmc.api.ModInitializer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.typho.town_of_trains.cca.ModComponents
import net.typho.town_of_trains.config.TownOfTrainsConfig
import net.typho.town_of_trains.roles.ModRoles
import org.slf4j.LoggerFactory

object TownOfTrains : ModInitializer {
    const val MOD_ID = "town_of_trains"

    @JvmField
    val LOGGER = LoggerFactory.getLogger("Town of Trains")

    //val TEST_ROLE = TMMRoles.registerRole(Role(id("test"), 0xFF7F3F, false, true, Role.MoodType.NONE, -1, true))

    override fun onInitialize() {
        TownOfTrainsConfig.init()
        ModComponents.init()
        ModRoles.init()
    }

    fun id(id: String) = Identifier.of(MOD_ID, id)

    fun PlayerEntity.getGameComponent(): GameWorldComponent = GameWorldComponent.KEY.get(world)
}
