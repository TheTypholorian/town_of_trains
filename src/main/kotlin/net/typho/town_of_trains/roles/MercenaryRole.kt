package net.typho.town_of_trains.roles

import dev.doctor4t.trainmurdermystery.api.Role
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent
import dev.doctor4t.trainmurdermystery.util.ShopEntry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.*
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import net.typho.town_of_trains.TownOfTrains
import net.typho.town_of_trains.config.ConfigOption

open class MercenaryRole(id: Identifier, type: RoleType, role: Role) : KillerRole(id, type, role), RoleWithInfo<MercenaryRole.MercenaryInfo> {
    val initialSize = ConfigOption.ofInt(TownOfTrains.id("initial_shop_size"), 1, 12, 1, 2)
    val perKill = ConfigOption.ofInt(TownOfTrains.id("per_kill"), 1, 12, 1, 1)

    init {
        addChild(initialSize)
        addChild(perKill)
    }

    override fun onAssigned(player: PlayerEntity?) {
        if (player != null) {
            setInfo(player, MercenaryInfo(listOf()))
        }
    }

    override fun onGameStart(player: PlayerEntity, game: GameWorldComponent) {
        TownOfTrains.LOGGER.info("game start")
        var info = getInfo(player)

        if (info == null) {
            info = MercenaryInfo(listOf())
        }

        if (initialSize.value > 0) {
            val possibleItems = super.getShopItems(player, game).withIndex().toMutableList()
            val weapon = possibleItems.stream()
                .filter { entry -> entry.value.type() == ShopEntry.Type.WEAPON }
                .toList()
                .random()

            info.unlocked += weapon.index
            possibleItems.remove(weapon)

            repeat(initialSize.value - 1) {
                val entry = possibleItems.random()
                info.unlocked += entry.index
                possibleItems.remove(entry)
            }
        }

        setInfo(player, info)
        TownOfTrains.LOGGER.info("\t${info.unlocked}")
    }

    override fun onKill(killer: PlayerEntity, victim: PlayerEntity, game: GameWorldComponent) {
        TownOfTrains.LOGGER.info("on kill")
        val info = getInfo(killer)!!

        if (perKill.value > 0) {
            val possibleItems = super.getShopItems(killer, game).withIndex().toMutableList()
            possibleItems.removeIf { entry -> info.unlocked.contains(entry.index) }

            repeat(perKill.value) {
                val entry = possibleItems.random()
                info.unlocked += entry.index
                possibleItems.remove(entry)
            }
            sync(killer)
        }
        TownOfTrains.LOGGER.info("\t${info.unlocked}")
    }

    override fun getShopItems(player: PlayerEntity, game: GameWorldComponent): List<ShopEntry> {
        val items = super.getShopItems(player, game)
        val r = getInfo(player)!!.unlocked.stream()
            .map { i -> items[i] }
            .toList()
        TownOfTrains.LOGGER.info("Get shop items $r")
        return r
    }

    override fun readFromNbt(
        nbt: NbtCompound,
        lookup: RegistryWrapper.WrapperLookup
    ): MercenaryInfo? {
        val list = nbt.getList("Unlocked", NbtElement.INT_TYPE.toInt())
        val info = MercenaryInfo(listOf())

        if (list != null) {
            for (element in list) {
                info.unlocked += (element as AbstractNbtNumber).intValue()
            }
        }

        TownOfTrains.LOGGER.info("Reading from nbt, unlocked: ${info.unlocked}, list: $list")

        return info
    }

    override fun writeToNbt(
        nbt: NbtCompound,
        lookup: RegistryWrapper.WrapperLookup,
        value: MercenaryInfo?
    ) {
        val list = NbtList()

        if (value != null) {
            for (i in value.unlocked) {
                list.add(NbtInt.of(i))
            }
        }

        TownOfTrains.LOGGER.info("Writing to nbt, unlocked: ${value?.unlocked}, list: $list")

        nbt.put("Unlocked", list)
    }

    data class MercenaryInfo(
        var unlocked: List<Int>
    )
}