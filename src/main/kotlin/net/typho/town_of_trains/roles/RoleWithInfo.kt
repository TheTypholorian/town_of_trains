@file:Suppress("UNCHECKED_CAST")

package net.typho.town_of_trains.roles

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper
import net.typho.town_of_trains.cca.ModComponents
import net.typho.town_of_trains.cca.PlayerRoleInfoComponent.Companion.getRoleInfo

interface RoleWithInfo<T> {
    fun getInfo(player: PlayerEntity): T? = player.getRoleInfo().info as? T

    fun setInfo(player: PlayerEntity, value: T?) {
        player.getRoleInfo().info = value
    }

    fun sync(player: PlayerEntity) = ModComponents.PLAYER_ROLE_INFO.sync(player)

    fun readFromNbt(nbt: NbtCompound, lookup: RegistryWrapper.WrapperLookup): T?

    fun writeToNbt(nbt: NbtCompound, lookup: RegistryWrapper.WrapperLookup, value: T?)
}