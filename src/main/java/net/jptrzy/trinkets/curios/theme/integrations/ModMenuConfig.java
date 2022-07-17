package net.jptrzy.trinkets.curios.theme.integrations;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.jptrzy.trinkets.curios.theme.Client;
import net.jptrzy.trinkets.curios.theme.config.AutoConfigManager;

public class ModMenuConfig implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return Client.isClothConfigLoaded() ? AutoConfigManager::getConfigScreen : screen -> null;
    }
}
