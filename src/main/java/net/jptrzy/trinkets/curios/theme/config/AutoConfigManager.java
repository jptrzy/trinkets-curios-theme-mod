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

        ModConfig.always_update = data.always_update;

        ModConfig.scrollbar = data.scrollbar;
        ModConfig.scrolling_outside_boundary = data.scrolling_outside_boundary;

        ModConfig.scout_auto_resize = data.scout_auto_resize;

        ModConfig.max_height = data.max_height;
        ModConfig.min_width = data.min_width;

//        if (Client.isScoutLoaded() && ModConfig.scout_auto_resize) {
//            ModConfig.max_height = 3;
//            ModConfig.min_width = 2;
//        } else {
//            ModConfig.max_height = data.max_height;
//            ModConfig.min_width = data.min_width;
//        }

        return ActionResult.SUCCESS;
    }

    public static void updateSize(boolean scoutEquipped){
        ModConfigData config = AutoConfig.getConfigHolder(ModConfigData.class).getConfig();
        if (scoutEquipped && ModConfig.scout_auto_resize) {
            ModConfig.max_height = 3;
            if( config.min_width == 1 ) {
                ModConfig.min_width = 2;
            }
        } else {
            ModConfig.max_height = config.max_height;
            ModConfig.min_width = config.min_width;
        }
    }
}