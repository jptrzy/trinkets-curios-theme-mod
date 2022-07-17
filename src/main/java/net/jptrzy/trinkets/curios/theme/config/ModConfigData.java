package net.jptrzy.trinkets.curios.theme.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.jptrzy.trinkets.curios.theme.Client;

@Config(name = Client.MOD_ID)
public class ModConfigData implements ConfigData {
//    @ConfigEntry.Category("Client")
    public boolean scrollbar = true;
}