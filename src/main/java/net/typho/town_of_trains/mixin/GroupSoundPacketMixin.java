package net.typho.town_of_trains.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.maxhenkel.voicechat.voice.common.GroupSoundPacket;
import net.minecraft.network.PacketByteBuf;
import net.typho.town_of_trains.voice.AmplifierAttacher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GroupSoundPacket.class)
public abstract class GroupSoundPacketMixin {
    @ModifyReturnValue(
            method = "fromBytes(Lnet/minecraft/network/PacketByteBuf;)Lde/maxhenkel/voicechat/voice/common/GroupSoundPacket;",
            at = @At("RETURN")
    )
    private GroupSoundPacket parseAmplificationFromThePacketBuffer(GroupSoundPacket original, @Local(argsOnly = true) PacketByteBuf buf) {
        ((AmplifierAttacher) original).town_of_trains$setAmplifier(buf.readFloat());
        return original;
    }

    @Inject(
            method = "toBytes",
            at = @At("TAIL")
    )
    private void addAmplificationToThePacketBuffer(PacketByteBuf buf, CallbackInfo ci) {
        buf.writeFloat(((AmplifierAttacher) this).town_of_trains$getAmplifier());
    }
}
