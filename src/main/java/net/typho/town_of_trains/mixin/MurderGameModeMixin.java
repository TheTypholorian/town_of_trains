package net.typho.town_of_trains.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.trainmurdermystery.api.Role;
import dev.doctor4t.trainmurdermystery.api.TMMRoles;
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.game.MurderGameMode;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.typho.town_of_trains.TownOfTrains;
import net.typho.town_of_trains.roles.ModRoles;
import net.typho.town_of_trains.roles.RoleType;
import net.typho.town_of_trains.roles.TownOfTrainsRole;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
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
        TownOfTrainsRole role = ModRoles.INSTANCE.getRole(player);

        if (role == null) {
            return original.call(instance, player);
        }

        return role.hasIdleMoney(player);
    }

    @Inject(
            method = "assignRolesAndGetKillerCount",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void assignRolesAndGetKillerCount(@NotNull ServerWorld world, @NotNull List<ServerPlayerEntity> players, GameWorldComponent game, CallbackInfoReturnable<Integer> cir) {
        TownOfTrainsRole.RoleTypePicker picker = new TownOfTrainsRole.RoleTypePicker();

        cir.setReturnValue(
                players.stream().sorted(Comparator.comparingDouble(v -> Math.random()))
                        .mapToInt(player -> {
                            TownOfTrainsRole role = TownOfTrainsRole.Companion.pickRole(new TownOfTrainsRole.RoleChoiceContext(
                                    picker.pick(), world, players, game
                            ));
                            ModRoles.INSTANCE.setRole(player, role);

                            if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
                                TownOfTrains.LOGGER.info("Picked role {} for {}", role.getId(), player.getName().getString());
                            }

                            if (role.getType() == RoleType.KILLER) {
                                return 1;
                            } else {
                                return 0;
                            }
                        })
                        .sum()
        );
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

        TownOfTrainsRole attached = ModRoles.INSTANCE.getAttachedRole(role);

        if (attached == null) {
            return false;
        }

        return attached.getType() == (role == TMMRoles.KILLER ? RoleType.KILLER : RoleType.VIGILANTE);
    }
}
