package net.jptrzy.trinkets.curios.theme.mixin;

import dev.emi.trinkets.TrinketScreen;
import dev.emi.trinkets.TrinketScreenManager;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = TrinketScreenManager.class, remap = false, priority=1500)
public class TrinketScreenManagerMixin {

    @Shadow public static TrinketScreen currentScreen;

    @Inject(at = @At("HEAD"), method = "update(FF)V", cancellable = true)
    private static void update(float mouseX, float mouseY, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "drawActiveGroup", cancellable = true)
    private static void drawActiveGroup(DrawableHelper helper, MatrixStack matrices, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "drawExtraGroups", cancellable = true)
    private static void drawExtraGroups(DrawableHelper helper, MatrixStack matrices, CallbackInfo ci) {
        ci.cancel();
    }
}
