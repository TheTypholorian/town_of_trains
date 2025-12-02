package net.typho.town_of_trains.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.trainmurdermystery.api.Role;
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.cca.ScoreboardRoleSelectorComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.typho.town_of_trains.roles.AbstractRole;
import net.typho.town_of_trains.roles.ModRoles;
import net.typho.town_of_trains.roles.RoleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(ScoreboardRoleSelectorComponent.class)
public class ScoreboardRoleSelectorComponentMixin {
    @WrapOperation(
            method = "assignVigilantes",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/doctor4t/trainmurdermystery/cca/GameWorldComponent;isRole(Lnet/minecraft/entity/player/PlayerEntity;Ldev/doctor4t/trainmurdermystery/api/Role;)Z"
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

        return attached.getType() == RoleType.KILLER;
    }

    @ModifyArg(
            method = "assignKillers",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/doctor4t/trainmurdermystery/cca/GameWorldComponent;addRole(Ljava/util/UUID;Ldev/doctor4t/trainmurdermystery/api/Role;)V"
            ),
            index = 1
    )
    private Role assignKillers(Role role, @Local(argsOnly = true) ServerWorld world, @Local(argsOnly = true) List<ServerPlayerEntity> players, @Local(argsOnly = true) GameWorldComponent game) {
        return AbstractRole.Companion.pickRole(new AbstractRole.RoleChoiceContext(
                RoleType.KILLER,
                world,
                players,
                game
        )).getInfo();
    }

    @ModifyArg(
            method = "assignVigilantes",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/doctor4t/trainmurdermystery/cca/GameWorldComponent;addRole(Lnet/minecraft/entity/player/PlayerEntity;Ldev/doctor4t/trainmurdermystery/api/Role;)V"
            ),
            index = 1
    )
    private Role assignVigilantes(Role role, @Local(argsOnly = true) ServerWorld world, @Local(argsOnly = true) List<ServerPlayerEntity> players, @Local(argsOnly = true) GameWorldComponent game) {
        return AbstractRole.Companion.pickRole(new AbstractRole.RoleChoiceContext(
                RoleType.VIGILANTE,
                world,
                players,
                game
        )).getInfo();
    }
}
