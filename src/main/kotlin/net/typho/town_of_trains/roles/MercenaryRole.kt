package net.typho.town_of_trains.roles

import dev.doctor4t.trainmurdermystery.api.Role
import dev.doctor4t.trainmurdermystery.util.ShopEntry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.typho.town_of_trains.TownOfTrains
import net.typho.town_of_trains.config.ConfigOption

open class MercenaryRole(id: Identifier, type: RoleType, role: Role) : KillerRole(id, type, role) {
    val initialSize = ConfigOption.ofInt(TownOfTrains.id("initial_shop_size"), 1, 12, 1, 2)
    val perKill = ConfigOption.ofInt(TownOfTrains.id("per_kill"), 1, 12, 1, 1)

    init {
        addChild(initialSize)
        addChild(perKill)
    }

    override fun getShopItems(player: PlayerEntity): List<ShopEntry> {
        return super.getShopItems(player).subList(0, 5)
    }
}