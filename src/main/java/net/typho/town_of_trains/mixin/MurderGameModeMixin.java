package net.typho.town_of_trains.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.game.MurderGameMode;
import net.minecraft.entity.player.PlayerEntity;
import net.typho.town_of_trains.roles.ModRoles;
import net.typho.town_of_trains.roles.TownOfTrainsRole;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MurderGameMode.class)
public class MurderGameModeMixin {
    @WrapOperation(
            method = "tickServerGameLoop",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/doctor4t/trainmurdermystery/cca/GameWorldComponent;canUseKillerFeatures(Lnet/minecraft/entity/player/PlayerEntity;)Z"
            )
    )
    private boolean tickServerGameLoop(GameWorldComponent instance, PlayerEntity player, Operation<Boolean> original) {
        TownOfTrainsRole role = ModRoles.INSTANCE.getRole(player);

        if (role == null) {
            return original.call(instance, player);
        }

        return role.hasIdleMoney(player);
    }
}
