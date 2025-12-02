package net.typho.town_of_trains.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.trainmurdermystery.api.Role;
import dev.doctor4t.trainmurdermystery.api.TMMRoles;
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.game.MurderGameMode;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.typho.town_of_trains.TownOfTrains;
import net.typho.town_of_trains.roles.AbstractRole;
import net.typho.town_of_trains.roles.ModRoles;
import net.typho.town_of_trains.roles.RoleType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

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
        AbstractRole role = ModRoles.INSTANCE.getRole(player);

        if (role == null) {
            return original.call(instance, player);
        }

        return role.hasIdleMoney(player);
    }

    @WrapOperation(
            method = "initializeGame",
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

        return attached.getType() == (role == TMMRoles.KILLER ? RoleType.KILLER : RoleType.VIGILANTE);
    }

    @ModifyArg(
            method = "assignRolesAndGetKillerCount",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/doctor4t/trainmurdermystery/cca/GameWorldComponent;addRole(Lnet/minecraft/entity/player/PlayerEntity;Ldev/doctor4t/trainmurdermystery/api/Role;)V"
            ),
            index = 1
    )
    private static Role assignRolesAndGetKillerCount(Role role, @Local(argsOnly = true) ServerWorld world, @Local(argsOnly = true) List<ServerPlayerEntity> players, @Local(argsOnly = true) GameWorldComponent game) {
        return AbstractRole.Companion.pickRole(new AbstractRole.RoleChoiceContext(
                RoleType.CIVILIAN,
                world,
                players,
                game
        )).getInfo();
    }

    @Inject(
            method = "assignRolesAndGetKillerCount",
            at = @At("TAIL")
    )
    private static void assignRolesAndGetKillerCount(@NotNull ServerWorld world, @NotNull List<ServerPlayerEntity> players, GameWorldComponent game, CallbackInfoReturnable<Integer> cir) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            for (ServerPlayerEntity player : players) {
                TownOfTrains.LOGGER.info("Assigned role {} to {}", game.getRole(player), player.getName().getString());
            }
        }
    }
}
