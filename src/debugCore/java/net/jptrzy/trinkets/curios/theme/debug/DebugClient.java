package net.jptrzy.trinkets.curios.theme.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.jptrzy.trinkets.curios.theme.Client;
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

public class DebugClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Client.LOGGER.warn("DEBUG MOD LOADED");
	}
}
