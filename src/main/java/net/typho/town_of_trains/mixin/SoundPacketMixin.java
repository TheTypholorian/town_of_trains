package net.typho.town_of_trains.mixin;

import de.maxhenkel.voicechat.voice.common.SoundPacket;
import net.typho.town_of_trains.voice.AmplifierAttacher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SoundPacket.class)
public class SoundPacketMixin implements AmplifierAttacher {
    @Unique
    private float amplifier = 1.0F;

    @Override
    public float town_of_trains$getAmplifier() {
        return this.amplifier;
    }

    @Override
    public void town_of_trains$setAmplifier(float value) {
        this.amplifier = value;
    }
}
