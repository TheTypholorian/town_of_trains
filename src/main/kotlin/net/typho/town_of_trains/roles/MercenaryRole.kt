package net.typho.town_of_trains.roles

import dev.doctor4t.trainmurdermystery.api.Role
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent
import dev.doctor4t.trainmurdermystery.util.ShopEntry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtInt
import net.minecraft.nbt.NbtList
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
        if (player != null && !player.world.isClient) {
            setInfo(player, MercenaryInfo(listOf()))
        }
    }

    override fun onGameStart(player: PlayerEntity, game: GameWorldComponent) {
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
    }

    override fun onKill(killer: PlayerEntity, victim: PlayerEntity, game: GameWorldComponent) {
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
    }

    override fun getShopItems(player: PlayerEntity, game: GameWorldComponent): List<ShopEntry> {
        val items = super.getShopItems(player, game)
        return getInfo(player)!!.unlocked.stream()
            .map{ i -> items[i] }
            .toList()
    }

    override fun readFromNbt(
        nbt: NbtCompound,
        lookup: RegistryWrapper.WrapperLookup,
        old: MercenaryInfo?
    ): MercenaryInfo? {
        val info = old ?: MercenaryInfo(listOf())
        val list = nbt.getList("Unlocked", NbtElement.INT_TYPE.toInt())

        if (list != null) {
            info.unlocked = listOf()
            repeat(list.size) { i ->
                info.unlocked += list.getInt(i)
            }
        }

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

        nbt.put("Unlocked", list)
    }

    data class MercenaryInfo(
        var unlocked: List<Int>
    ) {
        override fun toString(): String = unlocked.toString()
    }
}