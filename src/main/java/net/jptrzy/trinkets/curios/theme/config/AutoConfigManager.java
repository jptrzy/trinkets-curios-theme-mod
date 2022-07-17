package net.jptrzy.trinkets.curios.theme.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.jptrzy.trinkets.curios.theme.Client;
import net.jptrzy.trinkets.curios.theme.interfaces.TCTPlayerScreenHandlerInterface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;

public class AutoConfigManager {

    //TODO reload config on save using "ConfigSerializeEvent" somehow
    public static void setup(){
        ModConfigData data =
                AutoConfig.register(ModConfigData.class, GsonConfigSerializer::new).getConfig();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> reloadModConfig(null, data));
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success) -> reloadModConfig(null, data));

        AutoConfig.getConfigHolder(ModConfigData.class).registerLoadListener((manager, _data) -> {
            return reloadModConfig(manager, _data);
        });
        AutoConfig.getConfigHolder(ModConfigData.class).registerSaveListener((manager, _data) -> {
            return reloadModConfig(manager, _data);
        });
    }

    public static Screen getConfigScreen(Screen screen){
        return AutoConfig.getConfigScreen(ModConfigData.class, screen).get();
    }

    public static ActionResult reloadModConfig(@Nullable ConfigHolder<ModConfigData> manager, ModConfigData data){
        Client.LOGGER.info("Reload Config");

        ModConfig.scrollbar = data.scrollbar;

        return ActionResult.SUCCESS;
    }
}