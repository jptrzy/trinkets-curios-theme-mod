package net.jptrzy.trinkets.curios.theme.mixin;

import dev.emi.trinkets.TrinketScreenManager;
import dev.emi.trinkets.TrinketSlot;
import net.jptrzy.trinkets.curios.theme.Main;
import net.jptrzy.trinkets.curios.theme.interfaces.TCTPlayerScreenHandlerInterface;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {

    @Unique protected TexturedButtonWidget trinketsShowButton;
    @Unique protected TexturedButtonWidget trinketsHideButton;

    @Final @Shadow private RecipeBookWidget recipeBook;

    private InventoryScreenMixin() { super(null, null, null); }

    @Inject(at = @At("TAIL"), method = "init")
    protected void init(CallbackInfo ci) {
        this.trinketsShowButton = this.addDrawableChild(new TexturedButtonWidget(0, 0 , 12, 12, 0, 0, 12, Main.SOCIAL_INTERACTIONS_TEXTURE, 64, 64, this::onClick));
        this.trinketsHideButton = this.addDrawableChild(new TexturedButtonWidget(0, 0, 12, 12, 12, 0, 12, Main.SOCIAL_INTERACTIONS_TEXTURE, 64, 64, this::onClick));


        updateButtonsPos();
        onClick(false);
        if(recipeBook.isOpen()) {
            this.trinketsShowButton.visible = false;
            this.trinketsHideButton.visible = false;
        }
    }

    protected void updateButtonsPos(){
        this.trinketsShowButton.x = this.x+28;
        this.trinketsHideButton.x = this.x+28;
        this.trinketsShowButton.y = this.y+10;
        this.trinketsHideButton.y = this.y+10;
    }

    @Unique private boolean lastOpen;
    @Inject(method="onMouseClick", at = @At("HEAD"))
    protected void H_onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        lastOpen = recipeBook.isOpen();
    }

    @Inject(method="onMouseClick", at = @At("TAIL"))
    protected void T_onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        if(lastOpen != recipeBook.isOpen()){
            if(recipeBook.isOpen()){
                this.trinketsShowButton.visible = false;
                this.trinketsHideButton.visible = false;
            }else{
                updateButtonsPos();
                onClick(false);
            }
        }
    }

    protected void onClick(ButtonWidget button){
        onClick(true);
    }

    protected void onClick(boolean change){
        if(change){
            getTPC().setTrinketsShow(!getTPC().getTrinketsShow());
        }
        (getTPC().getTrinketsShow()  ? trinketsShowButton : trinketsHideButton).visible = true;
        (getTPC().getTrinketsShow()  ? trinketsHideButton : trinketsShowButton).visible = false;
    }

    @Inject(at = @At("TAIL"), method = "drawBackground")
    private void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        if(!recipeBook.isOpen() && getTPC().getTrinketsShow()){
            Main.drawbackground(this, this.x, this.y, matrices, getTPC().getTrinketSlotInd());
        }
    }

    protected TCTPlayerScreenHandlerInterface getTPC(){
        return ((TCTPlayerScreenHandlerInterface) this.handler);
    }

    @Override
    protected void drawSlot(MatrixStack matrices, Slot slot) {
        if(!recipeBook.isOpen() && getTPC().getTrinketsShow() || !(slot instanceof TrinketSlot)){
            super.drawSlot(matrices, slot);
        }
    }
}
