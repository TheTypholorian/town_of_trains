package net.typho.town_of_trains.roles

import dev.doctor4t.trainmurdermystery.api.Role
import dev.doctor4t.trainmurdermystery.api.TMMRoles
import dev.doctor4t.trainmurdermystery.cca.PlayerMoodComponent
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.typho.town_of_trains.HasName

abstract class TownOfTrainsRole(val info: Role) : HasName {
    constructor(id: Identifier, role: Role) : this(
        if (role.identifier().equals(id)) role else Role(
            id,
            role.color(),
            role.isInnocent,
            role.canUseKiller(),
            role.moodType,
            role.maxSprintTime,
            role.canSeeTime()
        )
    )

    init {
        TMMRoles.registerRole(info)
        ROLE_MAP[info] = this
        (info as RoleAttacher).`town_of_trains$setRole`(this)
    }

    open fun onTaskCompleted(player: PlayerEntity, task: PlayerMoodComponent.Task) {
    }

    open fun hasIdleMoney(player: PlayerEntity) = info.canUseKiller()

    open fun canSeePoison(player: PlayerEntity, world: World, pos: BlockPos, state: BlockState) = info.canUseKiller()

    open fun getNameTag(withRole: PlayerEntity, lookTarget: PlayerEntity, original: Text): Text = original

    fun getKey(): Identifier = info.identifier()

    override fun getName(): Text = Text.translatable(getKey().toTranslationKey("role"))

    override fun getDesc(): Text = Text.translatable(getKey().toTranslationKey("role", "desc"))

    companion object {
        val ROLE_MAP = HashMap<Role, TownOfTrainsRole>()
    }
}