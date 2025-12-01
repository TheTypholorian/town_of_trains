package net.typho.town_of_trains.mixin.client;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.typho.town_of_trains.config.TownOfTrainsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerEntityRenderer.class, priority = 1500)
public class PlayerEntityRendererMixin {
    @TargetHandler(
            mixin = "dev.doctor4t.trainmurdermystery.mixin.client.restrictions.PlayerEntityRendererMixin",
            name = "tmm$disableNameTags"
    )
    @Inject(
            method = "@MixinSquared:Handler",
            at = @At("HEAD"),
            remap = false
    )
    private void disableNameTags(AbstractClientPlayerEntity player, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f, Operation<Void> original, CallbackInfo ci) {
        if (TownOfTrainsConfig.INSTANCE.areNameTagsEnabled(player)) {
            original.call(player, text, matrixStack, vertexConsumerProvider, i, f);
        }
    }
}
