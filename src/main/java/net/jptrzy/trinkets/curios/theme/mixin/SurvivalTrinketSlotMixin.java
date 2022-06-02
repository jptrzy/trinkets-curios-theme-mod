package net.jptrzy.trinkets.curios.theme.mixin;

import dev.emi.trinkets.SurvivalTrinketSlot;
import dev.emi.trinkets.api.SlotGroup;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.TrinketInventory;
import net.jptrzy.trinkets.curios.theme.Main;
import net.jptrzy.trinkets.curios.theme.interfaces.TCTPlayerScreenHandlerInterface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = SurvivalTrinketSlot.class, remap = false, priority = 100)
public class SurvivalTrinketSlotMixin {

    @Mutable @Final @Shadow private boolean alwaysVisible;

    @Inject(at = @At("TAIL"), method = "<init>")
    public void init(TrinketInventory inventory, int index, int x, int y, SlotGroup group, SlotType type, int slotOffset, boolean _alwaysVisible, CallbackInfo ci){
        alwaysVisible = true;
    }


//    @Inject(at = @At("HEAD"), method = "isTrinketFocused", cancellable = true)
//    public void isTrinketFocused(CallbackInfoReturnable<Boolean> ci) {
//        ci.setReturnValue(true);
//    }

    @Inject(method = "isEnabled", at = @At(value="INVOKE",target="Lnet/minecraft/client/MinecraftClient;getInstance()Lnet/minecraft/client/MinecraftClient;",shift=At.Shift.BEFORE), cancellable = true)
    public void isEnabled(CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        Screen s = client.currentScreen;
        if(s instanceof InventoryScreen screen && screen.getRecipeBookWidget().isOpen() || s instanceof CreativeInventoryScreen){
            cir.setReturnValue(false);
        }
        if(s instanceof TCTPlayerScreenHandlerInterface tcp){
            cir.setReturnValue(tcp.getTrinketsShow());
        }
    }
}
