package net.typho.town_of_trains.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.typho.town_of_trains.config.TownOfTrainsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerEntity.class, priority = 1500)
public abstract class PlayerEntityMixin extends LivingEntity {
    public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @TargetHandler(
            mixin = "dev.doctor4t.wathe.mixin.PlayerEntityMixin",
            name = "wathe$limitSprint"
    )
    @Inject(
            method = "@MixinSquared:Handler",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void wathe$limitSprint(CallbackInfo ci, CallbackInfo ci1) {
        if (!TownOfTrainsConfig.INSTANCE.isSprintingEnabled((PlayerEntity) (Object) this)) {
            setSprinting(false);
            ci1.cancel();
        }
    }
}
