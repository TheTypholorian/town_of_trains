package net.typho.town_of_trains.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.trainmurdermystery.client.gui.RoleNameRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.typho.town_of_trains.roles.ModRoles;
import net.typho.town_of_trains.roles.TownOfTrainsRole;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RoleNameRenderer.class)
public class RoleNameRendererMixin {
    @WrapOperation(
            method = "renderHud",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getDisplayName()Lnet/minecraft/text/Text;"
            )
    )
    private static Text renderHud(PlayerEntity instance, Operation<Text> original, @Local(argsOnly = true) ClientPlayerEntity player) {
        Text text = original.call(instance);
        TownOfTrainsRole role = ModRoles.INSTANCE.getRole(player);

        if (role != null) {
            text = role.getNameTag(player, instance, text);
        }

        return text;
    }
}
