package net.typho.town_of_trains.roles

import dev.doctor4t.trainmurdermystery.api.Role
import dev.doctor4t.trainmurdermystery.api.TMMRoles
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.typho.town_of_trains.HasName

open class TownOfTrainsRole(val info: Role) : HasName {
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
        ROLE_MAP.put(info, this)
    }

    open fun onTaskCompleted(player: PlayerEntity) {
    }

    open fun hasIdleMoney(player: PlayerEntity) = info.canUseKiller()

    fun getKey(): Identifier = info.identifier()

    override fun getName(): Text = Text.translatable(getKey().toTranslationKey("role"))

    override fun getDesc(): Text = Text.translatable(getKey().toTranslationKey("role", "desc"))

    companion object {
        val ROLE_MAP = HashMap<Role, TownOfTrainsRole>()
    }
}