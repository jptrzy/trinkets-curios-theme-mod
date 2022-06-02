package net.jptrzy.trinkets.curios.theme.mixin;

import dev.emi.trinkets.CreativeTrinketSlot;
import dev.emi.trinkets.SurvivalTrinketSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CreativeTrinketSlot.class,  remap = false, priority = 100)
public class CreativeTrinketSlotMixin {

    @Inject(at = @At("TAIL"), method = "<init>")
    public void init(SurvivalTrinketSlot original, int s, int x, int y, CallbackInfo ci) {
        ((CreativeTrinketSlot) (Object) this).x = original.x;
        ((CreativeTrinketSlot) (Object) this).y = original.y;
    }
}
