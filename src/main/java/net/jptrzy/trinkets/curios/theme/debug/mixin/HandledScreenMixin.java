package net.jptrzy.trinkets.curios.theme.debug.mixin;

import dev.emi.trinkets.TrinketSlot;
import net.jptrzy.trinkets.curios.theme.Client;
import net.jptrzy.trinkets.curios.theme.config.ModConfig;
import net.jptrzy.trinkets.curios.theme.interfaces.TCTPlayerScreenHandlerInterface;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {

    private HandledScreenMixin(Text title) {
        super(title);
    }

    // Shadows variables

    @Shadow @Final public T handler;
    @Shadow protected int x;
    @Shadow protected int y;
    @Shadow protected int backgroundWidth;

    // Unique variables

    @Unique protected boolean correct = false;

    @Unique protected TexturedButtonWidget trinketsShowButton;
    @Unique protected TexturedButtonWidget trinketsHideButton;

    // TODO Update this old method of detection
    @Unique private boolean wasRecipeBookOpen;

    @Unique private boolean scrolling;

    // Static

    private static void setEnableButton(ClickableWidget b, boolean v){
        b.active = v;
        b.visible = v;
    }

    // Unique func

    protected RecipeBookWidget getBook(){
        return ((RecipeBookProvider) (Object) this).getRecipeBookWidget();
    }

    protected boolean isRecipeBookOpen(){
        return correct && ((HandledScreen) (Object) this) instanceof RecipeBookProvider && getBook().isOpen();
    }

    protected void updateButtonsPos(){
        this.trinketsShowButton.x = getBook().findLeftEdge(width, backgroundWidth)+28;
        this.trinketsHideButton.x = getBook().findLeftEdge(width, backgroundWidth)+28;
        this.trinketsShowButton.y = y+10;
        this.trinketsHideButton.y = y+10;
    }

    protected TCTPlayerScreenHandlerInterface getTPC(){
        return ((TCTPlayerScreenHandlerInterface) this.handler);
    }

    protected void onButtonClick(ButtonWidget button){
        onButtonClick(true);
    }

    protected void onButtonClick(boolean change){
        if (change) {
            getTPC().setTrinketsShow(!getTPC().getTrinketsShow());
            Client.updateScrollbar(handler.slots, getTPC(), 0);
        }

        setEnableButton(getTPC().getTrinketsShow()  ? trinketsShowButton : trinketsHideButton, true);
        setEnableButton(getTPC().getTrinketsShow()  ? trinketsHideButton : trinketsShowButton, false);
    }

    // Overwrite or Inject

    @Inject(at = @At("TAIL"), method = "init")
    protected void init(CallbackInfo ci) {
        correct = ((HandledScreen) (Object) this) instanceof InventoryScreen; // RecipeBookProvider

        if ( !correct ) { return; }

        this.trinketsShowButton = this.addDrawableChild(new TexturedButtonWidget(0, 0 , 12, 12, 0, 0, 12, Client.SOCIAL_INTERACTIONS_TEXTURE, 64, 64, this::onButtonClick));
        this.trinketsHideButton = this.addDrawableChild(new TexturedButtonWidget(0, 0, 12, 12, 12, 0, 12, Client.SOCIAL_INTERACTIONS_TEXTURE, 64, 64, this::onButtonClick));

        updateButtonsPos();
        onButtonClick(false);

        if (isRecipeBookOpen()) {
            setEnableButton(trinketsShowButton, false);
            setEnableButton(trinketsShowButton, false);
        }

        Client.updateScrollbar(handler.slots, getTPC(), 0f);
    }

//    @Inject(at = @At("TAIL"), method = "render")
    @Inject(method = "render", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true, at=@At(
            value="INVOKE", shift= At.Shift.AFTER,
            target="net/minecraft/client/gui/screen/ingame/HandledScreen.drawBackground (Lnet/minecraft/client/util/math/MatrixStack;FII)V"
    ))
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!correct) { return; }

        if (!isRecipeBookOpen() && getTPC().getTrinketsShow()) {
            Client.drawbackground(this, this.x, this.y, matrices, getTPC());
        }
    }

    @Inject(method="mouseClicked", at = @At("HEAD"))
    public void H_mouseClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (!correct) { return; }

        wasRecipeBookOpen = getBook().isOpen();
    }

    @Inject(method="mouseClicked", at = @At("TAIL"))
    public void T_mouseClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (!correct) { return; }

        if (wasRecipeBookOpen != getBook().isOpen()) {
            if (getBook().isOpen()) {
                setEnableButton(trinketsShowButton, false);
                setEnableButton(trinketsShowButton, false);
            } else {
                updateButtonsPos();
                onButtonClick(false);
            }
        }
    }

    @Inject(method="drawSlot", at = @At("HEAD"), cancellable = true)
    protected void drawSlot(MatrixStack matrices, Slot slot, CallbackInfo ci) {
        if (!correct) { return; }

        if (getBook().isOpen() && getTPC().getTrinketsShow() && slot instanceof TrinketSlot) {
            ci.cancel();
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!correct || !ModConfig.scrollbar || getBook().isOpen() || !getTPC().getTrinketsShow() ||
                !Client.isScrolledInTrinkets(getTPC(), mouseX, mouseY, this.x, this.y)) {
            return super.mouseScrolled(mouseX, mouseY, amount);
        }

        Client.updateScrollbar(this.handler.slots, getTPC(), amount);

        return true;
    }

    @Inject(at = @At("HEAD"), method = "mouseClicked")
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (!correct) { return; }

        if (ModConfig.scrollbar && getTPC().getTrinketsShow() && Client.isClickInScrollbar(getTPC(), mouseX, mouseY, this.x, this.y)) {
            this.scrolling = true;
        }
    }

    @Inject(at = @At("HEAD"), method = "mouseReleased")
    public void mouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (!correct) { return; }

        scrolling = false;
    }

    @Inject(at = @At("HEAD"), method = "mouseDragged", cancellable = true)
    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir) {
        if (!correct) { return; }

        int l = MathHelper.ceil((float) getTPC().getTrinketSlotInd() / ModConfig.min_width) - ModConfig.max_height;
        if (this.scrolling && l > 0) {
            int h = MathHelper.ceil((float) getTPC().getTrinketSlotInd() / ModConfig.min_width)
                    - ModConfig.max_height > 0 ? ModConfig.max_height : getTPC().getTrinketSlotInd();
            int i = this.y + 18;
            int j = i + 18*h;
            float scrollPosition = ((float)mouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
            getTPC().setScrollIndex(MathHelper.clamp( (int) (scrollPosition * l), 0, l) );
            Client.updateScrollbar(this.handler.slots, getTPC(), 0);
            cir.setReturnValue(true);
        }
    }
}
