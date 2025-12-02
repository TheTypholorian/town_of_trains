package net.typho.town_of_trains.roles

import dev.doctor4t.trainmurdermystery.api.Role
import dev.doctor4t.trainmurdermystery.api.TMMRoles
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent
import dev.doctor4t.trainmurdermystery.cca.PlayerMoodComponent
import dev.doctor4t.trainmurdermystery.client.gui.RoleAnnouncementTexts
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.typho.town_of_trains.HasName
import net.typho.town_of_trains.TownOfTrains
import net.typho.town_of_trains.config.ConfigOption
import net.typho.town_of_trains.config.ConfigSection
import net.typho.town_of_trains.roles.ModRoles.setAttachedRole
import java.util.*

abstract class TownOfTrainsRole : ConfigSection, HasName {
    val type: RoleType
    val info: Role
    val announcementText: RoleAnnouncementTexts.RoleAnnouncementText
    val isEnabled = ConfigOption.ofBool(TownOfTrains.id("is_enabled"), true)
    val weight = ConfigOption.ofInt(TownOfTrains.id("weight"), 0, 100, 5, 10)
    var config: ConfigSection? = null

    constructor(id: Identifier, type: RoleType, role: Role) : super(id, listOf()) {
        this.type = type
        this.info = if (role.identifier().equals(id)) role else Role(
            id,
            role.color(),
            role.isInnocent,
            role.canUseKiller(),
            role.moodType,
            role.maxSprintTime,
            role.canSeeTime()
        )
        announcementText = when (this.info) {
            TMMRoles.KILLER -> RoleAnnouncementTexts.KILLER
            TMMRoles.VIGILANTE -> RoleAnnouncementTexts.VIGILANTE
            TMMRoles.CIVILIAN -> RoleAnnouncementTexts.CIVILIAN
            else -> RoleAnnouncementTexts.RoleAnnouncementText(id.toTranslationKey(), info.color())
        }
        TMMRoles.registerRole(info)
        ROLE_MAP[info] = this
        info.setAttachedRole(this)
        addChild(isEnabled)
        addChild(weight)
        type.configTab.addChild(this)
    }

    open fun onTaskCompleted(player: PlayerEntity, task: PlayerMoodComponent.Task) = Unit

    open fun hasIdleMoney(player: PlayerEntity) = info.canUseKiller()

    open fun canSeePoison(player: PlayerEntity, world: World, pos: BlockPos, state: BlockState) = info.canUseKiller()

    open fun getNameTag(withRole: PlayerEntity, lookTarget: PlayerEntity, original: Text): Text = original

    open fun canBeChosen(context: RoleChoiceContext): Boolean = context.type == type && isEnabled.value && weight.value > 0

    override fun getName(): Text = Text.translatable(getKey().toTranslationKey("role"))

    override fun getDesc(): Text = Text.translatable(getKey().toTranslationKey("role", "desc"))

    companion object {
        val ROLE_MAP = HashMap<Role, TownOfTrainsRole>()

        fun pickRole(context: RoleChoiceContext): TownOfTrainsRole {
            val list = LinkedList<TownOfTrainsRole>()

            for (role in ROLE_MAP.values) {
                if (role.canBeChosen(context)) {
                    repeat(role.weight.value) {
                        list.add(role)
                    }
                }
            }

            if (list.isEmpty()) {
                return context.type.defaultRole
            }

            return list.random()
        }
    }

    data class RoleChoiceContext(
        val type: RoleType,
        val world: ServerWorld,
        val players: List<ServerPlayerEntity>,
        val game: GameWorldComponent
    )

    data class RoleTypePicker(var i: Int = 0) {
        fun pick(): RoleType = when ((i++) % 6) {
            0 -> RoleType.KILLER
            1 -> RoleType.VIGILANTE
            else -> RoleType.CIVILIAN
        }
    }
}