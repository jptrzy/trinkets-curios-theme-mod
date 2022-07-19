package net.jptrzy.trinkets.curios.theme.integrations;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import net.jptrzy.trinkets.curios.theme.Client;
import net.jptrzy.trinkets.curios.theme.config.ModConfig;
import net.jptrzy.trinkets.curios.theme.interfaces.TCTPlayerScreenHandlerInterface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

import me.shedaniel.math.Rectangle;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class REIIntegration implements REIClientPlugin {
    @Override
    public void registerScreens(ScreenRegistry registry) {
        ExclusionZones zones = registry.exclusionZones();
        zones.register(InventoryScreen.class, screen -> {
            List<Rectangle> bounds = new ArrayList<>();

            TCTPlayerScreenHandlerInterface tcp = ((TCTPlayerScreenHandlerInterface) screen.handler);
            bounds.add(Client.getTCTRectangle(tcp, screen.x, screen.y));

            return bounds;
        });
    }
}
