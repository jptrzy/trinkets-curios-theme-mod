package net.jptrzy.trinkets.curios.theme.mixin;

import dev.emi.trinkets.TrinketSlot;
import net.jptrzy.trinkets.curios.theme.Main;
import net.jptrzy.trinkets.curios.theme.interfaces.TCTPlayerScreenHandlerInterface;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin implements TCTPlayerScreenHandlerInterface {


    @Unique
    protected int trinketSlotStart = -1;
    @Unique
    public int trinketSlotInd = 0;

    @Unique public Boolean trinketsShow = true;

    @Final
    @Shadow
    public DefaultedList<Slot> slots;

    @Shadow
    public abstract void internalOnSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player);

    @Redirect(method="onSlotClick", at=@At(value="INVOKE", target="Lnet/minecraft/screen/ScreenHandler;internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V"))
    public void onSlotClick(ScreenHandler instance, int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if(actionType.equals(SlotActionType.THROW ) && slotIndex >= trinketSlotStart  && slotIndex <= trinketSlotStart+trinketSlotInd){
            actionType = SlotActionType.PICKUP;
        }
        internalOnSlotClick(slotIndex, button, actionType, player);
    }

    @Inject(at = @At("RETURN"), method = "addSlot", cancellable = true)
    public void addSlot(Slot slot, CallbackInfoReturnable<Slot> cir) {
        // Disable creative slots
        if( ((ScreenHandler) (Object) this) instanceof  PlayerScreenHandler ){
            cir.setReturnValue(slot);
        }

        if(slot instanceof TrinketSlot){
            if(trinketSlotStart == -1 || trinketSlotStart >= slots.size()){
                trinketSlotStart = slots.size();
                trinketSlotInd = 0;
            }

            slot.x = -16 - (trinketSlotInd/7) * 18;
            slot.y = 17 + (trinketSlotInd%7) * 18;

            trinketSlotInd++;
        }

        cir.setReturnValue(slot);
    }

    @Override
    public void setTrinketsShow(Boolean val){
        this.trinketsShow = val;
    }

    @Override
    public Boolean getTrinketsShow(){
        return this.trinketsShow;
    }

    @Override public int getTrinketSlotStart(){
        return this.trinketSlotStart;
    }
    @Override public int getTrinketSlotInd(){
        return this.trinketSlotInd;
    }
}
