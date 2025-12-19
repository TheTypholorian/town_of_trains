package net.typho.town_of_trains.mixin.client;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.player.PlayerEntity;
import net.typho.town_of_trains.config.TownOfTrainsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = GameOptions.class, priority = 1500)
public class GameOptionsMixin {
    @TargetHandler(
            mixin = "dev.doctor4t.wathe.mixin.client.restrictions.GameOptionsMixin",
            name = "getPerspective"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/doctor4t/wathe/game/GameFunctions;isPlayerAliveAndSurvival(Lnet/minecraft/entity/player/PlayerEntity;)Z"
            ),
            remap = false
    )
    private boolean getPerspective(PlayerEntity player, Operation<Boolean> original) {
        return !TownOfTrainsConfig.INSTANCE.isPerspectiveEnabled(player);
    }
}
