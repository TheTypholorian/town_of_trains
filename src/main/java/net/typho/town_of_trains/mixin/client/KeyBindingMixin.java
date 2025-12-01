package net.typho.town_of_trains.mixin.client;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.typho.town_of_trains.config.TownOfTrainsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = KeyBinding.class, priority = 1500)
public class KeyBindingMixin {
    @TargetHandler(
            mixin = "dev.doctor4t.trainmurdermystery.mixin.client.restrictions.KeyBindingMixin",
            name = "shouldSuppressKey"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/KeyBinding;equals(Lnet/minecraft/client/option/KeyBinding;)Z"
            )
    )
    private boolean shouldSuppressKey(KeyBinding ts, KeyBinding that, Operation<Boolean> original) {
        MinecraftClient client = MinecraftClient.getInstance();
        GameOptions options = client.options;

        if ((that == options.chatKey || that == options.commandKey) && TownOfTrainsConfig.INSTANCE.isChatEnabled(client.player)) {
            return false;
        }

        if (that == options.jumpKey && TownOfTrainsConfig.INSTANCE.isJumpingEnabled(client.player)) {
            return false;
        }

        if (that == options.togglePerspectiveKey && TownOfTrainsConfig.INSTANCE.isPerspectiveEnabled(client.player)) {
            return false;
        }

        return original.call(ts, that);
    }
}
