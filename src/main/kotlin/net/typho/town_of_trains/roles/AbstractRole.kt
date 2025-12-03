package net.typho.town_of_trains.roles

import dev.doctor4t.trainmurdermystery.api.Role
import dev.doctor4t.trainmurdermystery.api.TMMRoles
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent
import dev.doctor4t.trainmurdermystery.cca.PlayerMoodComponent
import dev.doctor4t.trainmurdermystery.client.gui.RoleAnnouncementTexts
import dev.doctor4t.trainmurdermystery.game.GameConstants
import dev.doctor4t.trainmurdermystery.util.ShopEntry
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

abstract class AbstractRole : ConfigSection, HasName {
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

    open fun onAssigned(player: PlayerEntity?) = Unit

    open fun onRemoved(player: PlayerEntity?) {
        if (this is RoleWithInfo<*> && player != null) {
            setInfo(player, null)
        }
    }

    open fun onGameStart(player: PlayerEntity, game: GameWorldComponent = GameWorldComponent.KEY.get(player.world)) = Unit

    open fun onGameEnd(player: PlayerEntity, game: GameWorldComponent = GameWorldComponent.KEY.get(player.world)) = Unit

    open fun onClientTick(player: PlayerEntity, game: GameWorldComponent = GameWorldComponent.KEY.get(player.world)) = Unit

    open fun onServerTick(player: PlayerEntity, game: GameWorldComponent = GameWorldComponent.KEY.get(player.world)) = Unit

    open fun onTaskCompleted(player: PlayerEntity, task: PlayerMoodComponent.Task, game: GameWorldComponent = GameWorldComponent.KEY.get(player.world)) = Unit

    open fun onKill(killer: PlayerEntity, victim: PlayerEntity, game: GameWorldComponent = GameWorldComponent.KEY.get(killer.world)) = Unit

    open fun onKilled(killer: PlayerEntity, victim: PlayerEntity, game: GameWorldComponent = GameWorldComponent.KEY.get(killer.world)) = Unit

    open fun canUseShop(player: PlayerEntity, game: GameWorldComponent = GameWorldComponent.KEY.get(player.world)) = info.canUseKiller()

    open fun getShopItems(player: PlayerEntity, game: GameWorldComponent = GameWorldComponent.KEY.get(player.world)): List<ShopEntry> = GameConstants.SHOP_ENTRIES

    open fun hasIdleMoney(player: PlayerEntity, game: GameWorldComponent = GameWorldComponent.KEY.get(player.world)) = info.canUseKiller()

    open fun canSeePoison(player: PlayerEntity, world: World, pos: BlockPos, state: BlockState, game: GameWorldComponent = GameWorldComponent.KEY.get(player.world)) = info.canUseKiller()

    open fun getNameTag(withRole: PlayerEntity, lookTarget: PlayerEntity, original: Text, game: GameWorldComponent = GameWorldComponent.KEY.get(withRole.world)): Text = original

    open fun canBeChosen(context: RoleChoiceContext): Boolean = context.type == type && isEnabled.value && weight.value > 0

    override fun getName(): Text = Text.translatable(getKey().toTranslationKey("role"))

    override fun getDesc(): Text = announcementText.goalText.apply(5) //Text.translatable(getKey().toTranslationKey("role", "desc"))

    companion object {
        val ROLE_MAP = HashMap<Role, AbstractRole>()

        fun pickRole(context: RoleChoiceContext): AbstractRole {
            val list = LinkedList<AbstractRole>()

            for (role in ROLE_MAP.values) {
                if (role.canBeChosen(context)) {
                    repeat(role.weight.value) {
                        list.add(role)
                    }
                }
            }

            if (list.isEmpty()) {
                return context.fallback
            }

            return list.random()
        }
    }

    data class RoleChoiceContext(
        val type: RoleType,
        val fallback: AbstractRole,
        val world: ServerWorld,
        val players: List<ServerPlayerEntity>,
        val game: GameWorldComponent
    )
}