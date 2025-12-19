package net.typho.town_of_trains.mixin.client;

import net.typho.town_of_trains.client.VibrancyCompat;
import net.typho.vibrancy.Vibrancy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Pseudo
@Mixin(Vibrancy.class)
public class VibrancyMixin {
    @ModifyArgs(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/typho/vibrancy/api/LightManager;<init>(Ljava/lang/Iterable;IIIII)V"
            )
    )
    private static void clinit(Args args) {
        args.set(1, 2);
        args.set(2, 8);
        args.set(3, 100);
        args.set(4, 50);
        args.set(5, 5);
    }

    @Inject(
            method = "render",
            at = @At("HEAD"),
            cancellable = true
    )
    private void render(CallbackInfo ci) {
        if (!VibrancyCompat.Companion.isRaytracingEnabled()) {
            ci.cancel();
        }
    }
}
