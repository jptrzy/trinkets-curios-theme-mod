package net.jptrzy.trinkets.curios.theme;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
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
	}

	public static void drawbackground(DrawableHelper helper, int x, int y, MatrixStack matrices, int length) {
		RenderSystem.setShaderTexture(0, MORE_SLOTS);

		int width =  (int) Math.ceil(length / 7.0);
		int height = Math.min(length, 7);

		for (int i = 0; i < width; i++) {
			//Top && Bottom
			helper.drawTexture(matrices, x - 17 - i*18, y + 9, 7, 26, 18, 7);
			helper.drawTexture(matrices, x - 17 - i*18, y + 18 * height + 16, 7, 51, 18, 7);
		}

		//Corner
		helper.drawTexture(matrices, x-6-width*18, y+9,      0, 26, 7, 7);
		helper.drawTexture(matrices, x-6-width*18, y+18*height+16,      0, 51, 7, 7);

		for (int i = 0; i < height; i++) {
			//Left Outer Border
			helper.drawTexture(matrices, x-6-width*18, y+18*i+16, 0, 33, 7, 18);
		}

		for (int i = 0; i < length; i++) {
			//Inner border around each slot
			helper.drawTexture(matrices, x-17-(i/7)*18, y+18*(i%7)+16, 4, 4, 18, 18);
		}

		if(length > 7) {
			RenderSystem.setShaderTexture(0, SOCIAL_INTERACTIONS_TEXTURE);
			for (int i = length; i < width * 7; i++) {
				DrawableHelper.drawTexture(matrices, x - 17 - (i / 7) * 18, y + 18 * (i % 7) + 16, 0, 32, 18, 18, 64, 64);
			}
		}
	}
}
