package net.jptrzy.trinkets.curios.theme.interfaces;

import org.spongepowered.asm.mixin.Unique;

public interface TCTPlayerScreenHandlerInterface {
//    Boolean trinketsShow = true;
    void setTrinketsShow(Boolean val);
    Boolean getTrinketsShow();
    int getTrinketSlotStart();
    int getTrinketSlotInd();
}
