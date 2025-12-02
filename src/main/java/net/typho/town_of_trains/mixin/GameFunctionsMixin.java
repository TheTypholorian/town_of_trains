package net.typho.town_of_trains.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.trainmurdermystery.entity.PlayerBodyEntity;
import dev.doctor4t.trainmurdermystery.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.typho.town_of_trains.cca.ModComponents;
import net.typho.town_of_trains.cca.PlayerBodyInfoComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameFunctions.class)
public class GameFunctionsMixin {
    @Inject(
            method = "killPlayer(Lnet/minecraft/entity/player/PlayerEntity;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Identifier;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/doctor4t/trainmurdermystery/entity/PlayerBodyEntity;setPlayerUuid(Ljava/util/UUID;)V"
            )
    )
    private static void killPlayer(PlayerEntity victim, boolean spawnBody, PlayerEntity killer, Identifier deathReason, CallbackInfo ci, @Local PlayerBodyEntity body) {
        PlayerBodyInfoComponent info = ModComponents.INSTANCE.getPLAYER_BODY_INFO().get(body);
        info.getSuspects().add(killer.getUuid());
        info.setReason(deathReason);
        info.sync();
    }
}
