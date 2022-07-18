package net.jptrzy.trinkets.curios.theme.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.jptrzy.trinkets.curios.theme.Client;

@Config(name = Client.MOD_ID)
public class ModConfigData implements ConfigData {
    @ConfigEntry.Gui.PrefixText public static boolean always_update = false;

    public boolean scrollbar = true;
    @ConfigEntry.Gui.PrefixText public boolean scrolling_outside_boundary = false;

    @ConfigEntry.BoundedDiscrete(min = 2, max = 7)
    public int max_height = 7;
    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.BoundedDiscrete(min = 1, max = 4)
    public int min_width = 1;

//    @ConfigEntry.Category("Integrations")
    // TODO make other inputs gray color when scout detected and this option set to true
    @ConfigEntry.Gui.PrefixText public boolean scout_auto_resize = true;
}