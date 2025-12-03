package net.typho.town_of_trains.cca

import dev.doctor4t.trainmurdermystery.entity.PlayerBodyEntity
import net.typho.town_of_trains.TownOfTrains
import org.ladysnake.cca.api.v3.component.ComponentKey
import org.ladysnake.cca.api.v3.component.ComponentRegistry
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy

object ModComponents : EntityComponentInitializer {
    fun init() = Unit

    val PLAYER_BODY_INFO: ComponentKey<PlayerBodyInfoComponent> = ComponentRegistry.getOrCreate(
        TownOfTrains.id("player_body_info"),
        PlayerBodyInfoComponent::class.java
    )
    val PLAYER_ROLE_INFO: ComponentKey<PlayerRoleInfoComponent> = ComponentRegistry.getOrCreate(
        TownOfTrains.id("player_role_info"),
        PlayerRoleInfoComponent::class.java
    )

    override fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {
        registry.registerFor(
            PlayerBodyEntity::class.java,
            PLAYER_BODY_INFO,
            ::PlayerBodyInfoComponent
        )
        registry.registerForPlayers(
            PLAYER_ROLE_INFO,
            ::PlayerRoleInfoComponent,
            RespawnCopyStrategy.NEVER_COPY
        )
    }
}