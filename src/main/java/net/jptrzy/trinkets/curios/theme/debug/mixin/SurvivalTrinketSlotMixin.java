package net.jptrzy.trinkets.curios.theme.debug.mixin;

import dev.emi.trinkets.SurvivalTrinketSlot;
import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.SlotGroup;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.TrinketInventory;
import net.jptrzy.trinkets.curios.theme.config.ModConfig;
import net.jptrzy.trinkets.curios.theme.interfaces.TCTPlayerScreenHandlerInterface;
import net.jptrzy.trinkets.curios.theme.interfaces.TCTSurvivalTrinketSlot;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SurvivalTrinketSlot.class, remap = false, priority = 1500)
public abstract class SurvivalTrinketSlotMixin extends Slot implements TrinketSlot, TCTSurvivalTrinketSlot {

    @Unique private boolean enabled = true;

    @Mutable @Final @Shadow private boolean alwaysVisible;

    @Shadow public abstract boolean isTrinketFocused();

    private SurvivalTrinketSlotMixin(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    public void init(TrinketInventory inventory, int index, int x, int y, SlotGroup group, SlotType type, int slotOffset, boolean _alwaysVisible, CallbackInfo ci){
        alwaysVisible = true;
    }

    @Override
    public boolean isEnabled() {
        MinecraftClient client = MinecraftClient.getInstance();
        Screen s = client.currentScreen;
        if(s instanceof InventoryScreen screen) {
            return (!screen.getRecipeBookWidget().isOpen() && ((TCTPlayerScreenHandlerInterface) screen.getScreenHandler()).getTrinketsShow() && (!ModConfig.scrollbar || enabled));
        } else if (s instanceof CreativeInventoryScreen) {
            return false;
        }
        return this.isTrinketFocused();
    }

    @Override public void setEnabled(boolean b) {
        this.enabled = b;
    }
}
