package net.jptrzy.trinkets.curios.theme.integrations;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.widget.Bounds;
import me.shedaniel.math.Rectangle;
import net.jptrzy.trinkets.curios.theme.Client;
import net.jptrzy.trinkets.curios.theme.config.ModConfig;
import net.jptrzy.trinkets.curios.theme.interfaces.TCTPlayerScreenHandlerInterface;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class EMIIntegration implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addExclusionArea(InventoryScreen.class, (screen, consumer) -> {
            TCTPlayerScreenHandlerInterface tcp = ((TCTPlayerScreenHandlerInterface) screen.handler);

            Rectangle r = Client.getTCTRectangle(tcp, screen.x, screen.y);

            consumer.accept(new Bounds(r.x, r.y, r.width, r.height));
        });
    }
}
