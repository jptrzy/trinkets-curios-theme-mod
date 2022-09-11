package net.jptrzy.trinkets.curios.theme.debug.mixin;

import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.jptrzy.trinkets.curios.theme.Client;
import net.jptrzy.trinkets.curios.theme.config.AutoConfigManager;
import net.jptrzy.trinkets.curios.theme.config.ModConfig;
import net.jptrzy.trinkets.curios.theme.integrations.ScoutUtils;
import net.jptrzy.trinkets.curios.theme.interfaces.TCTPlayerScreenHandlerInterface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin implements TCTPlayerScreenHandlerInterface {


    @Unique protected int trinketSlotStart = -1;
    @Unique public int trinketSlotInd = 0;
    @Unique public int scrollbarIndex = 0;
    @Unique public Boolean trinketsShow = true;
    @Final @Shadow public DefaultedList<Slot> slots;

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

//        if (  ((ScreenHandler) (Object) this) instanceof TrinketPlayerScreenHandler) {
//            Client.LOGGER.warn("WORKS {}", ((TrinketPlayerScreenHandler) (Object) this).trinkets$getTrinketSlotEnd() );
//        }

        if(slot instanceof TrinketSlot){

            if(trinketSlotStart == -1 || trinketSlotStart >= slots.size()){
                trinketSlotStart = slots.size();
                trinketSlotInd = 0;
            }

            slot.x = -16 - (trinketSlotInd/ModConfig.max_height) * 18;
            slot.y = 17 + (trinketSlotInd%ModConfig.max_height) * 18;

            trinketSlotInd++;

//            TrinketsApi.getTrinketComponent(MinecraftClient.getInstance().player).ifPresent(trinkets -> {
//                trinkets.getTrackingUpdates();
//            });

            Optional<TrinketComponent> trinkets = TrinketsApi.getTrinketComponent(MinecraftClient.getInstance().player);
            // TODO sometimes trinkets isn't present
            if(trinkets.isPresent() && !ModConfig.always_update){
                int l = 1;
                for (Map<String, TrinketInventory> key : trinkets.get().getInventory().values()) {
                    l += key.size();
                }
                if ( l-trinketSlotInd <= 0 ) {
                    if(l-trinketSlotInd < 0){
                        Client.LOGGER.error("Something went wrong with counting trinkets slots, this shouldn't happened.");
                    }
                    if (Client.isScoutLoaded()) {
                        setScoutEquipped(ScoutUtils.haveLeftSatchel(trinkets.get()));
                    }
                    Client.updateScrollbar(this.slots, this, 0F);
                }
            }else{
                setScoutEquipped(Client.isScoutLoaded());
                Client.updateScrollbar(this.slots, this, 0F);
            }
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

    @Override public int getScrollIndex(){
        return this.scrollbarIndex;
    }
    @Override public void setScrollIndex(int index){
        this.scrollbarIndex = index;
    }

    @Unique public boolean scoutEquipped = false;
//    @Override public boolean getScoutEquipped(){
//        return this.scoutEquipped;
//    }
    @Override public void setScoutEquipped(boolean b){
        this.scoutEquipped = b;

        if (Client.isClothConfigLoaded()) {
            AutoConfigManager.updateSize(b);
        }
    }
}
