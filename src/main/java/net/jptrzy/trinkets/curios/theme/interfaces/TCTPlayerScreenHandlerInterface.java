package net.jptrzy.trinkets.curios.theme.interfaces;

import org.spongepowered.asm.mixin.Unique;

public interface TCTPlayerScreenHandlerInterface {
    // TODO I could use dictionary to make them more...

    void setTrinketsShow(Boolean val);
    Boolean getTrinketsShow();
    int getTrinketSlotStart();
    int getTrinketSlotInd();

    int getScrollIndex();
    void setScrollIndex(int index);

    boolean getScoutEquipped();
    void setScoutEquipped(boolean val);
}
