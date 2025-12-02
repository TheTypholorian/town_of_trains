package net.typho.town_of_trains.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import de.maxhenkel.voicechat.voice.client.AudioChannel;
import de.maxhenkel.voicechat.voice.common.SoundPacket;
import net.typho.town_of_trains.voice.AmplifierAttacher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AudioChannel.class)
public abstract class AudioChannelMixin {
    @ModifyVariable(
            method = "writeToSpeaker",
            at = @At("STORE"),
            name = "channelVolume"
    )
    private float amplifyStuff(float original, @Local(argsOnly = true, name = "arg1") SoundPacket<?> packet) {
        return ((AmplifierAttacher) packet).town_of_trains$getAmplifier() * original;
    }
}
