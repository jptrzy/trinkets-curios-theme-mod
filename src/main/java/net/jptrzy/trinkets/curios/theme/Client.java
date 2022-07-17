package net.jptrzy.trinkets.curios.theme;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.jptrzy.trinkets.curios.theme.config.AutoConfigManager;
import net.jptrzy.trinkets.curios.theme.config.ModConfig;
import net.jptrzy.trinkets.curios.theme.interfaces.TCTPlayerScreenHandlerInterface;
import net.jptrzy.trinkets.curios.theme.interfaces.TCTSurvivalTrinketSlot;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static java.lang.Math.min;

public class Client implements ClientModInitializer {
	public static final String MOD_ID = "trinkets-curios-theme";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Identifier MORE_SLOTS = new Identifier("trinkets", "textures/gui/more_slots.png");
	public static final Identifier SOCIAL_INTERACTIONS_TEXTURE = new Identifier(Client.MOD_ID, "textures/gui/curios.png");

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

	public static void drawbackground(DrawableHelper helper, int x, int y, MatrixStack matrices, TCTPlayerScreenHandlerInterface tcp) {
		int length = tcp.getTrinketSlotInd();

		RenderSystem.setShaderTexture(0, MORE_SLOTS);

		int width = (int) Math.ceil(length / 7.0);
		if (ModConfig.scrollbar) {
			width = 1;
		}
		int height = Math.min(length, 7);

		for (int i = 0; i < width; i++) {
			//Top && Bottom
			helper.drawTexture(matrices, x - 17 - i * 18, y + 9, 7, 26, 18, 7);
			helper.drawTexture(matrices, x - 17 - i * 18, y + 18 * height + 16, 7, 51, 18, 7);
		}

		for (int i = 0; i < length; i++) {
			//Inner border around each slot
			if (width == 1 && i >= 7) {
				break;
			}
			helper.drawTexture(matrices, x - 17 - (i / 7) * 18, y + 18 * (i % 7) + 16, 4, 4, 18, 18);
		}

		if (! (ModConfig.scrollbar && length > 7) ) {
			for (int i = 0; i < height; i++) {
				//Left Outer Border
				helper.drawTexture(matrices, x - 6 - width * 18, y + 18 * i + 16, 0, 33, 7, 18);
			}

			//Corner
			helper.drawTexture(matrices, x - 6 - width * 18, y + 9, 0, 26, 7, 7);
			helper.drawTexture(matrices, x - 6 - width * 18, y + 18 * height + 16, 0, 51, 7, 7);
		}

		if(length > 7 ) {
			RenderSystem.setShaderTexture(0, SOCIAL_INTERACTIONS_TEXTURE);
			for (int i = length; i < width * 7; i++) {
				DrawableHelper.drawTexture(matrices, x - 17 - (i / 7) * 18, y + 18 * (i % 7) + 16, 0, 32, 18, 18, 64, 64);
			}
		}

		if (ModConfig.scrollbar && length > 7) {
			for (int i = 0; i < height; i++) {
				//Left Outer Border
				DrawableHelper.drawTexture(matrices, x - 26, y + 16 + i * 18, 19, 35, 9, 18, 64, 64);
			}

			//Corner
			DrawableHelper.drawTexture(matrices, x - 8 - width * 18, y + 9, 19, 28, 9, 7, 64, 64);
			DrawableHelper.drawTexture(matrices, x - 8 - width * 18, y + 18 * height + 16, 19, 53, 9, 7, 64, 64);

			//Scrollbar
			double amount = (height - 1) * 18 * ((double) tcp.getScrollIndex() / (tcp.getTrinketSlotInd()-7));
			DrawableHelper.drawTexture(matrices, x - 6 - 18 + 2, y + 16 + (int) amount, 28, 35, 4, 18, 64, 64);
		}

	}

	public static void updateScrollbar(DefaultedList<Slot> slots, TCTPlayerScreenHandlerInterface tcp, double amount){

		int l = tcp.getTrinketSlotInd() - 7;
		if(l <= 0){
			return;
		}

		int index =  MathHelper.clamp(tcp.getScrollIndex() - (int) amount, 0, l);
		tcp.setScrollIndex(index);

		//Reload position on config change
		if(!ModConfig.scrollbar){
			for (int i=0; i<tcp.getTrinketSlotInd(); i++) {
				if (slots.size() <= tcp.getTrinketSlotStart() - 1 + i) {
					LOGGER.error("WIT");
					continue;
				}
				Slot slot = slots.get(tcp.getTrinketSlotStart() - 1 + i);

				slot.x = -16 - (i/7) * 18;
				slot.y = 17 + (i%7) * 18;

				((TCTSurvivalTrinketSlot) slot).setEnabled(true);
			}
			return;
		}

		 for (int i=0; i<tcp.getTrinketSlotInd(); i++) {
			 if (slots.size() <= tcp.getTrinketSlotStart() - 1 + i) {
				 LOGGER.error("WIT");
				 continue;
			 }
			 Slot slot = slots.get(tcp.getTrinketSlotStart() - 1 + i);

			 if (i >= index && i < index + 7) {
				 ((TCTSurvivalTrinketSlot) slot).setEnabled(true);
				 slot.x = -16 - ((i-index)/7) * 18;
				 slot.y = 17 + ((i-index)%7) * 18;
			 }else{
				 ((TCTSurvivalTrinketSlot) slot).setEnabled(false);
			 }
		 }


		 tcp.setScrollIndex(index);
	}

	public static boolean isClickInScrollbar(double mouseX, double mouseY, int x, int y) {
		int k = x - 21;
		int l = y + 16;
		int m = k + 6;
		int n = l + 126;
		return mouseX >= (double)k && mouseY >= (double)l && mouseX < (double)m && mouseY < (double)n;
	}

	public static boolean isScrolledInTrinkets(double mouseX, double mouseY, int x, int y) {
		int k = x - 26;
		int l = y + 10;
		int m = k + 27;
		int n = l + 140;
		return (mouseX >= (double)k && mouseY >= (double)l && mouseX < (double)m && mouseY < (double)n) || ModConfig.scrolling_outside_boundary;
	}
}
