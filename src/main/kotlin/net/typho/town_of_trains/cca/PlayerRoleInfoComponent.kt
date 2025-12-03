@file:Suppress("UNCHECKED_CAST")

package net.typho.town_of_trains.cca

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper
import net.typho.town_of_trains.roles.ModRoles.getRole
import net.typho.town_of_trains.roles.RoleWithInfo
import org.ladysnake.cca.api.v3.component.ComponentV3
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent

class PlayerRoleInfoComponent(var holder: PlayerEntity) : ComponentV3, AutoSyncedComponent {
    var info: Any? = null

    fun sync() = ModComponents.PLAYER_ROLE_INFO.sync(holder)

    private fun <T> read(role: RoleWithInfo<T>, nbt: NbtCompound, lookup: RegistryWrapper.WrapperLookup) {
        info = role.readFromNbt(nbt, lookup, info as? T)
    }

    override fun readFromNbt(nbt: NbtCompound, lookup: RegistryWrapper.WrapperLookup) {
        val role = holder.getRole()

        if (role is RoleWithInfo<*>) {
            read(role, nbt, lookup)
        }
    }

    private fun <T> write(role: RoleWithInfo<T>, nbt: NbtCompound, lookup: RegistryWrapper.WrapperLookup) {
        role.writeToNbt(nbt, lookup, info as? T)
    }

    override fun writeToNbt(nbt: NbtCompound, lookup: RegistryWrapper.WrapperLookup) {
        val role = holder.getRole()

        if (role is RoleWithInfo<*>) {
            write(role, nbt, lookup)
        }
    }

    companion object {
        fun PlayerEntity.getRoleInfo(): PlayerRoleInfoComponent = ModComponents.PLAYER_ROLE_INFO.get(this)
    }
}