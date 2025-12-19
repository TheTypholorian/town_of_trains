package net.typho.town_of_trains

import dev.doctor4t.wathe.cca.GameWorldComponent
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.typho.town_of_trains.cca.ModComponents
import net.typho.town_of_trains.config.TownOfTrainsConfig
import net.typho.town_of_trains.event.handleStandardEvents
import net.typho.town_of_trains.roles.ModRoles
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object TownOfTrains : ModInitializer {
    const val MOD_ID = "town_of_trains"

    @JvmField
    val LOGGER: Logger = LoggerFactory.getLogger("Town of Trains")!!
    val HAS_VIBRANCY = FabricLoader.getInstance().isModLoaded("vibrancy")

    override fun onInitialize() {
        TownOfTrainsConfig.init()
        ModComponents.init()
        ModRoles.init()
        handleStandardEvents()
    }

    fun id(id: String): Identifier = Identifier.of(MOD_ID, id)

    fun PlayerEntity.getGameComponent(): GameWorldComponent = GameWorldComponent.KEY.get(world)
}
