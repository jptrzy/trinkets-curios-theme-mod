package net.jptrzy.trinkets.curios.theme.mixin;

import dev.emi.trinkets.TrinketSlot;
import net.jptrzy.trinkets.curios.theme.Client;
import net.jptrzy.trinkets.curios.theme.config.ModConfig;
import net.jptrzy.trinkets.curios.theme.interfaces.TCTPlayerScreenHandlerInterface;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {

    @Unique private float scrollPosition;
    @Unique private boolean scrolling;

    @Unique protected TexturedButtonWidget trinketsShowButton;
    @Unique protected TexturedButtonWidget trinketsHideButton;

    @Final @Shadow private RecipeBookWidget recipeBook;

    private InventoryScreenMixin() { super(null, null, null); }

    @Inject(at = @At("TAIL"), method = "init")
    protected void init(CallbackInfo ci) {
        this.trinketsShowButton = this.addDrawableChild(new TexturedButtonWidget(0, 0 , 12, 12, 0, 0, 12, Client.SOCIAL_INTERACTIONS_TEXTURE, 64, 64, this::onClick));
        this.trinketsHideButton = this.addDrawableChild(new TexturedButtonWidget(0, 0, 12, 12, 12, 0, 12, Client.SOCIAL_INTERACTIONS_TEXTURE, 64, 64, this::onClick));


        updateButtonsPos();
        onClick(false);
        if(recipeBook.isOpen()) {
            this.trinketsShowButton.visible = false;
            this.trinketsHideButton.visible = false;
            this.trinketsShowButton.active = false;
            this.trinketsHideButton.active = false;
        }

        Client.updateScrollbar(handler.slots, getTPC(), 0f);
    }

    protected void updateButtonsPos(){
        this.trinketsShowButton.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth)+28;
        this.trinketsHideButton.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth)+28;
        this.trinketsShowButton.y = this.y+10;
        this.trinketsHideButton.y = this.y+10;
    }

    @Unique private boolean lastOpen;
    @Inject(method="mouseClicked", at = @At("HEAD"))
    public void H_mouseClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        lastOpen = recipeBook.isOpen();
    }

    @Inject(method="mouseClicked", at = @At("TAIL"))
    public void T_mouseClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if(lastOpen != recipeBook.isOpen()){
            if(recipeBook.isOpen()){
                this.trinketsShowButton.visible = false;
                this.trinketsHideButton.visible = false;
                this.trinketsShowButton.active = false;
                this.trinketsHideButton.active = false;
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
        (getTPC().getTrinketsShow()  ? trinketsShowButton : trinketsHideButton).active = true;
        (getTPC().getTrinketsShow()  ? trinketsHideButton : trinketsShowButton).active = false;
    }

    @Inject(at = @At("TAIL"), method = "drawBackground")
    private void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        if(!recipeBook.isOpen() && getTPC().getTrinketsShow()){
            Client.drawbackground(this, this.x, this.y, matrices, getTPC());
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

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!ModConfig.scrollbar || recipeBook.isOpen() || !getTPC().getTrinketsShow()) {
            return false;
        } else {
            Client.updateScrollbar(this.handler.slots, getTPC(), amount);

            return true;
        }
    }
}
