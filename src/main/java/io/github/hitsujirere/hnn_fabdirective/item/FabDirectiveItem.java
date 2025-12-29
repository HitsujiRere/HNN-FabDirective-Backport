package io.github.hitsujirere.hnn_fabdirective.item;

import net.minecraft.world.item.Item;

/**
 * An item which holds a set of Loot Fabricator targets.
 * <p>
 * It can copy the targets from a fabricator and apply them to another. It can also be used to open a GUI to configure targets on-the-fly.
 */
public class FabDirectiveItem extends Item {

    public FabDirectiveItem(Properties properties) {
        super(properties);
    }

}