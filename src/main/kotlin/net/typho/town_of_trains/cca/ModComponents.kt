package net.typho.town_of_trains.cca

import dev.doctor4t.trainmurdermystery.entity.PlayerBodyEntity
import net.typho.town_of_trains.TownOfTrains
import org.ladysnake.cca.api.v3.component.ComponentKey
import org.ladysnake.cca.api.v3.component.ComponentRegistry
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer

object ModComponents : EntityComponentInitializer {
    fun init() = Unit

    val PLAYER_BODY_INFO: ComponentKey<PlayerBodyInfoComponent> = ComponentRegistry.getOrCreate(
        TownOfTrains.id("player_body_info"),
        PlayerBodyInfoComponent::class.java
    )

    override fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {
        registry.registerFor(PlayerBodyEntity::class.java, PLAYER_BODY_INFO) { holder -> PlayerBodyInfoComponent(holder) }
    }
}