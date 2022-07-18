package net.jptrzy.trinkets.curios.theme.integrations;

import dev.emi.trinkets.api.TrinketComponent;
import net.jptrzy.trinkets.curios.theme.Client;
import pm.c7.scout.item.BaseBagItem;

import java.util.Objects;

public abstract class ScoutUtils {
    public static boolean haveLeftSatchel(TrinketComponent trinkets){
        for (int i=0; i<trinkets.getAllEquipped().size(); i++) {
            if (trinkets.getAllEquipped().get(i).getRight().getItem() instanceof BaseBagItem &&
                    trinkets.getAllEquipped().get(i).getLeft().index() == 0 &&
                    Objects.equals(trinkets.getAllEquipped().get(i).getLeft().inventory().getSlotType().getName(), "pouch")) {
                return true;
            }
        }
        return false;
    }
}
