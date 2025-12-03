package net.typho.town_of_trains.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.cca.PlayerShopComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.typho.town_of_trains.roles.AbstractRole;
import net.typho.town_of_trains.roles.ModRoles;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(PlayerShopComponent.class)
public class PlayerShopComponentMixin {
    @Shadow
    @Final
    private PlayerEntity player;

    @WrapOperation(
            method = "tryBuy",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;get(I)Ljava/lang/Object;"
            )
    )
    private <E> E tryBuy(List<E> instance, int i, Operation<E> original) {
        AbstractRole role = ModRoles.INSTANCE.getRole(player);

        if (role == null) {
            return original.call(instance, i);
        }

        return original.call(role.getShopItems(player, GameWorldComponent.KEY.get(player.getWorld())), i);
    }
}
