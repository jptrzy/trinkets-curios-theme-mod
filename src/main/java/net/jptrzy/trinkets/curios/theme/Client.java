package net.jptrzy.trinkets.curios.theme;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.widget.Bounds;
import me.shedaniel.math.Rectangle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.jptrzy.trinkets.curios.theme.config.AutoConfigManager;
import net.jptrzy.trinkets.curios.theme.config.ModConfig;
import net.jptrzy.trinkets.curios.theme.interfaces.TCTPlayerScreenHandlerInterface;
import net.jptrzy.trinkets.curios.theme.interfaces.TCTSurvivalTrinketSlot;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client implements ClientModInitializer {
	public static final String MOD_ID = "trinkets-curios-theme";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Identifier MORE_SLOTS = new Identifier("trinkets", "textures/gui/more_slots.png");
	public static final Identifier SOCIAL_INTERACTIONS_TEXTURE = new Identifier(Client.MOD_ID, "textures/gui/curios.png");

	public static void check(HandledScreen h) {
		Client.LOGGER.warn("Check {}", h instanceof InventoryScreen);
		
	}

	@Override
	public void onInitializeClient() {
		if(!FabricLoader.getInstance().isModLoaded("trinkets")){
			LOGGER.error("Trinkets mod isn't loaded/installed. Without it this mod can't work property.");
		}

		if(Client.isClothConfigLoaded()){
			AutoConfigManager.setup();
		}
	}

	public static boolean isModLoaded(String modID) {
		return FabricLoader.getInstance().isModLoaded(modID);
	}

	public static boolean isClothConfigLoaded() {
		return isModLoaded("cloth-config2");
	}

	public static boolean isScoutLoaded() {
		return isModLoaded("scout");
	}

	public static void drawbackground(DrawableHelper helper, int x, int y, MatrixStack matrices, TCTPlayerScreenHandlerInterface tcp) {
		int length = tcp.getTrinketSlotInd();



//		LOGGER.warn("length {}", length);

		RenderSystem.setShaderTexture(0, MORE_SLOTS);

		int width = (int) Math.ceil(length / (float) ModConfig.max_height);
		if (ModConfig.scrollbar) {
			width = ModConfig.min_width;
		}
		int height = Math.min(MathHelper.ceil((float) length/ModConfig.min_width), ModConfig.max_height);

		if (length < 1) {
			height = 0;
			width = 0;
		}

		for (int i = 0; i < width; i++) {
			//Top && Bottom
			helper.drawTexture(matrices, x - 17 - i * 18, y + 9, 7, 26, 18, 7);
			helper.drawTexture(matrices, x - 17 - i * 18, y + 18 * height + 16, 7, 51, 18, 7);
		}

//		for (int i = 0; i < length; i++) {
//			//Inner border around each slot
//			if (width == 1 && i >= ModConfig.max_height) {
//				break;
//			}
//
//			if (!isScrollbarVisable(length)) {
//				if (i >= ModConfig.min_width*ModConfig.max_height) {
//					break;
//				}
//				helper.drawTexture(matrices, x - 17 - (i % ModConfig.min_width) * 18, y + 18 * (i / ModConfig.min_width) + 16, 4, 4, 18, 18);
//			} else {
//				LOGGER.warn("TEST");
//				helper.drawTexture(matrices, x - 17 - (i / ModConfig.max_height) * 18, y + 18 * (i % ModConfig.max_height) + 16, 4, 4, 18, 18);
//			}
//		}

		if (!isScrollbarVisable(length)) {
			for (int i = 0; i < height; i++) {
				//Left Outer Border
				helper.drawTexture(matrices, x - 6 - width * 18, y + 18 * i + 16, 0, 33, 7, 18);
			}

			//Corner
			helper.drawTexture(matrices, x - 6 - width * 18, y + 9, 0, 26, 7, 7);
			helper.drawTexture(matrices, x - 6 - width * 18, y + 18 * height + 16, 0, 51, 7, 7);
		}

		RenderSystem.setShaderTexture(0, SOCIAL_INTERACTIONS_TEXTURE);

		for (int i = 0; i < width * height; i++) {
			DrawableHelper.drawTexture(matrices, x - 17 - (i % width) * 18, y + 18 * (i / width) + 16, 0, 32, 18, 18, 64, 64);
		}

		if (isScrollbarVisable(length)) {
			for (int i = 0; i < height; i++) {
				//Left Outer Border
				DrawableHelper.drawTexture(matrices, x - 8 - width * 18, y + 16 + i * 18, 19, 35, 9, 18, 64, 64);
			}

			//Corner
			DrawableHelper.drawTexture(matrices, x - 8 - width * 18, y + 9, 19, 28, 9, 7, 64, 64);
			DrawableHelper.drawTexture(matrices, x - 8 - width * 18, y + 18 * height + 16, 19, 53, 9, 7, 64, 64);

			//Scrollbar
			double amount = (height - 1) * 18 * ((double) tcp.getScrollIndex() /
					(MathHelper.ceil((float) tcp.getTrinketSlotInd()
							/ ModConfig.min_width) - ModConfig.max_height));
			DrawableHelper.drawTexture(matrices, x - 6 - 18 * width + 2, y + 16 + (int) amount, 28, 35, 4, 18, 64, 64);
		}

	}

	/*
	Update position and visibility of trinkets slots basing on current options.
	 */
	public static void updateSlots(DefaultedList<Slot> slots, TCTPlayerScreenHandlerInterface tcp){
//		LOGGER.warn("UPDATE SLOTS");
//		if (MinecraftClient.getInstance().currentScreen instanceof HandledScreen handled) {
//			LOGGER.warn("WORKS WORKS {}", handled.handler);
//		}

		int index = tcp.getScrollIndex() * ModConfig.min_width;
		for (int i=0; i<tcp.getTrinketSlotInd(); i++) {
			if (slots.size() <= tcp.getTrinketSlotStart() - 1 + i) {
				LOGGER.error("WIT");
				continue;
			}
			Slot slot = slots.get(tcp.getTrinketSlotStart() - 1 + i);

			if (ModConfig.scrollbar) {
				if (i >= index && i < index + ModConfig.max_height * ModConfig.min_width) {
					((TCTSurvivalTrinketSlot) slot).setEnabled(true);
					slot.x = -16 - ((i-index)%ModConfig.min_width) * 18;
					slot.y = 17 + ((i-index)/ModConfig.min_width) * 18;
				}else{
					((TCTSurvivalTrinketSlot) slot).setEnabled(false);
				}
			} else {
				slot.x = -16 - (i / ModConfig.max_height) * 18;
				slot.y = 17 + (i % ModConfig.max_height) * 18;
				((TCTSurvivalTrinketSlot) slot).setEnabled(true);
			}
		}
	}

	public static void updateScrollbar(DefaultedList<Slot> slots, TCTPlayerScreenHandlerInterface tcp, double amount){
		if(ModConfig.scrollbar){
			int l = MathHelper.ceil((float) tcp.getTrinketSlotInd() / ModConfig.min_width) - ModConfig.max_height;
			int index;
			if(l <= 0){
				index = 0;
			} else {
				index =  MathHelper.clamp(tcp.getScrollIndex() - (int) amount, 0, l);
			}
			tcp.setScrollIndex(index);
		}

		updateSlots(slots, tcp);
	}

	public static boolean isClickInScrollbar(TCTPlayerScreenHandlerInterface tcp, double mouseX, double mouseY, int x, int y) {
		int h = MathHelper.ceil((float) tcp.getTrinketSlotInd() / ModConfig.min_width)
				- ModConfig.max_height > 0 ? ModConfig.max_height : tcp.getTrinketSlotInd();
		x -= 3 + 18 * ModConfig.min_width;
		y += 16;
		int xx = x + 6;
		int yy = y + 18 * h;

		return mouseX >= (double)x && mouseY >= (double)y && mouseX < (double)xx && mouseY < (double)yy;
	}

	public static boolean isScrolledInTrinkets(TCTPlayerScreenHandlerInterface tcp, double mouseX, double mouseY, int x, int y) {
		if (ModConfig.scrolling_outside_boundary) {
			return true;
		}

		int h = MathHelper.ceil((float) tcp.getTrinketSlotInd() / ModConfig.min_width)
				- ModConfig.max_height > 0 ? ModConfig.max_height : tcp.getTrinketSlotInd();
		x -= 8 + 18 * ModConfig.min_width ;
		y += 10;
		int xx = x + 9 + 18 * ModConfig.min_width;
		int yy = y + 14 + 18 * h;
		return (mouseX >= (double)x && mouseY >= (double)y && mouseX < (double)xx && mouseY < (double)yy);
	}
	public static boolean isScrollbarVisable(int length){
		return ModConfig.scrollbar &&  MathHelper.ceil((float) length / ModConfig.min_width) > ModConfig.max_height;
	}

	public static Rectangle getTCTRectangle(TCTPlayerScreenHandlerInterface tcp, int x, int y){
		int ih = MathHelper.ceil((float) tcp.getTrinketSlotInd() / ModConfig.min_width)
				- ModConfig.max_height > 0 ? ModConfig.max_height : tcp.getTrinketSlotInd();
		x -= 8 + 18 * ModConfig.min_width ;
		y += 10;
		int w = 9 + 18 * ModConfig.min_width;
		int h = 14 + 18 * ih;

		return new Rectangle(x, y, w, h);
	}
}
