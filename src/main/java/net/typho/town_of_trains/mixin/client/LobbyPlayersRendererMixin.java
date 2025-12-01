package net.typho.town_of_trains.mixin.client;

import dev.doctor4t.trainmurdermystery.client.gui.LobbyPlayersRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LobbyPlayersRenderer.class)
public class LobbyPlayersRendererMixin {
    @Inject(
            method = "renderHud",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
                    ordinal = 1
            ),
            cancellable = true
    )
    private static void renderCredits(TextRenderer renderer, ClientPlayerEntity player, DrawContext context, CallbackInfo ci) {
        ci.cancel();
    }
}
