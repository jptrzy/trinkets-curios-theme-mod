package net.jptrzy.trinkets.curios.theme.debug.mixin;

import net.jptrzy.trinkets.curios.theme.debug.ImGuiManager;
import net.jptrzy.trinkets.curios.theme.config.ModConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class ScreenDebugMode<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {
    private ScreenDebugMode(Text title) {
        super(title);
    }

    @Unique protected ImGuiManager iGM = null;

    @Inject(at = @At("TAIL"), method = "init")
    protected void init(CallbackInfo ci) {
        if (ModConfig.DEBUG) {
            iGM = new ImGuiManager();
        }
    }

    @Override public boolean charTyped(char chr, int keyCode) {
        return (iGM == null || iGM.charTyped(chr, keyCode)) && super.charTyped(chr, keyCode);
    }
    @Inject(at = @At("TAIL"), method = "keyPressed", cancellable = true)
   public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue((iGM == null || iGM.keyPressed(keyCode, scanCode, modifiers)) && cir.getReturnValue());
    }
    @Override public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return (iGM == null || iGM.keyReleased(keyCode, scanCode, modifiers)) &&
                super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Inject(at = @At("HEAD"), method = "close")
    public void close(CallbackInfo ci) {
        if( iGM == null ) { return; }
        iGM.close();
    }

    @Inject(at = @At("TAIL"), method = "render")
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if( iGM == null ) { return; }
        iGM.render(matrices, mouseX, mouseY, delta);
    }
}
