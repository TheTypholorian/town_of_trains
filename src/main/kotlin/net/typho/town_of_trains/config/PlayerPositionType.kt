package net.typho.town_of_trains.config

import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.typho.town_of_trains.HasName
import net.typho.town_of_trains.TownOfTrains.getGameComponent
import java.util.function.Predicate

enum class PlayerPositionType : HasName {
    IN_LOBBY({ player -> player === null || player.getGameComponent().gameStatus == GameWorldComponent.GameStatus.INACTIVE }),
    GAME_ACTIVE({ player -> player?.getGameComponent()?.gameStatus == GameWorldComponent.GameStatus.ACTIVE }),
    DEAD({ player -> player?.isDead ?: true }),
    PARTICIPANT({ player -> !DEAD.test(player) && GAME_ACTIVE.test(player) }),
    NOT_PARTICIPANT({ player -> DEAD.test(player) || IN_LOBBY.test(player) }),
    ALWAYS({ player -> true }),
    NEVER({ player -> false });

    private var test: Predicate<PlayerEntity?>

    constructor(test: Predicate<PlayerEntity?>) {
        this.test = test
    }

    override fun getName(): Text = Text.translatable("gui.town_of_trains.player_position_type." + name.lowercase())

    override fun getDesc(): Text? = Text.translatable("gui.town_of_trains.player_position_type." + name.lowercase() + ".desc")

    fun test(player: PlayerEntity?): Boolean = test.test(player)
}