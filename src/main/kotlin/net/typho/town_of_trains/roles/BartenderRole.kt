package net.typho.town_of_trains.roles

import dev.doctor4t.trainmurdermystery.api.Role
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class BartenderRole(id: Identifier, role: Role) : TownOfTrainsRole(id, role) {
    override fun canSeePoison(player: PlayerEntity, world: World, pos: BlockPos, state: BlockState): Boolean = true
}