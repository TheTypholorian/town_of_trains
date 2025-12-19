package net.typho.town_of_trains.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedInventoryScreen;
import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.typho.town_of_trains.roles.AbstractRole;
import net.typho.town_of_trains.roles.ModRoles;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(LimitedInventoryScreen.class)
public class LimitedInventoryScreenMixin {
    @Shadow
    @Final
    public ClientPlayerEntity player;

    @WrapOperation(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/doctor4t/wathe/cca/GameWorldComponent;canUseKillerFeatures(Lnet/minecraft/entity/player/PlayerEntity;)Z"
            )
    )
    private boolean init(GameWorldComponent instance, PlayerEntity player, Operation<Boolean> original) {
        AbstractRole role = ModRoles.INSTANCE.getRole(player);

        if (role == null) {
            return original.call(instance, player);
        }

        return role.canUseShop(player, GameWorldComponent.KEY.get(player.getWorld()));
    }

    @ModifyVariable(
            method = "init",
            at = @At(
                    value = "STORE"
            )
    )
    private List<ShopEntry> init(List<ShopEntry> value) {
        AbstractRole role = ModRoles.INSTANCE.getRole(player);

        if (role == null) {
            return value;
        }

        return role.getShopItems(player, GameWorldComponent.KEY.get(player.getWorld()));
    }
}
