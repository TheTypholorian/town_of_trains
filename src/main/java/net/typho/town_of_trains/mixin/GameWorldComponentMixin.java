package net.typho.town_of_trains.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.trainmurdermystery.api.Role;
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.typho.town_of_trains.roles.AbstractRole;
import net.typho.town_of_trains.roles.ModRoles;
import net.typho.town_of_trains.roles.RoleAttacher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

@Mixin(GameWorldComponent.class)
public class GameWorldComponentMixin {
    @Shadow
    @Final
    private World world;

    @Shadow
    private GameWorldComponent.GameStatus gameStatus;

    @WrapOperation(
            method = "addRole(Ljava/util/UUID;Ldev/doctor4t/trainmurdermystery/api/Role;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/HashMap;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private <K, V> V addRole(HashMap<K, V> instance, K key, V value, Operation<V> original) {
        PlayerEntity player = world.getPlayerByUuid((UUID) key);
        V old = original.call(instance, key, value);

        if (value instanceof RoleAttacher attacher) {
            AbstractRole role = attacher.town_of_trains$getRole();

            if (role != null) {
                role.onAssigned(player);
            }
        }

        if (old instanceof RoleAttacher attacher) {
            AbstractRole role = attacher.town_of_trains$getRole();

            if (role != null) {
                role.onRemoved(player);
            }
        }

        return old;
    }

    @WrapOperation(
            method = "resetRole",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Set;removeIf(Ljava/util/function/Predicate;)Z"
            )
    )
    private boolean resetRole(Set<Map.Entry<UUID, Role>> values, Predicate<Map.Entry<UUID, Role>> predicate, Operation<Boolean> original) {
        return original.call(values, (Predicate<Map.Entry<UUID, Role>>) entry -> {
            boolean remove = predicate.test(entry);

            if (remove) {
                PlayerEntity player = world.getPlayerByUuid(entry.getKey());
                AbstractRole role = ModRoles.INSTANCE.getAttachedRole(entry.getValue());

                if (role != null) {
                    role.onRemoved(player);
                }
            }

            return remove;
        });
    }

    @Inject(
            method = "clientTick",
            at = @At("TAIL")
    )
    private void clientTick(CallbackInfo ci) {
        for (PlayerEntity player : world.getPlayers()) {
            AbstractRole role = ModRoles.INSTANCE.getRole(player);

            if (role != null) {
                role.onClientTick(player, (GameWorldComponent) (Object) this);
            }
        }
    }

    @Inject(
            method = "serverTick",
            at = @At("TAIL")
    )
    private void serverTick(CallbackInfo ci) {
        for (PlayerEntity player : world.getPlayers()) {
            AbstractRole role = ModRoles.INSTANCE.getRole(player);

            if (role != null) {
                role.onServerTick(player, (GameWorldComponent) (Object) this);
            }
        }
    }
}
