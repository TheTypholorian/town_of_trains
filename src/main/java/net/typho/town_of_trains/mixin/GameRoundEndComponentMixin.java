package net.typho.town_of_trains.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameRoundEndComponent;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.typho.town_of_trains.roles.AbstractRole;
import net.typho.town_of_trains.roles.ModRoles;
import net.typho.town_of_trains.roles.RoleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRoundEndComponent.class)
public class GameRoundEndComponentMixin {
    @WrapOperation(
            method = "setRoundEndData",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/doctor4t/wathe/cca/GameWorldComponent;isRole(Lnet/minecraft/entity/player/PlayerEntity;Ldev/doctor4t/wathe/api/Role;)Z"
            )
    )
    private boolean setRoundEndData(GameWorldComponent instance, PlayerEntity player, Role role, Operation<Boolean> original) {
        if (original.call(instance, player, role)) {
            return true;
        }

        AbstractRole attached = ModRoles.INSTANCE.getAttachedRole(role);

        if (attached == null) {
            return false;
        }

        return attached.getType() == RoleType.VIGILANTE;
    }
}
