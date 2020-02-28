package com.fantasticsource.tiamatitems.trait.unrecalculable;

import com.fantasticsource.tools.component.Component;
import net.minecraft.item.ItemStack;

public abstract class CUnrecalculableTraitElement extends Component
{
    /**
     * @return How lucky the rolls were during this particular application, eg. 1 for the best possible result, 0 for the worst, and 0.5 for a "perfectly average" result.  Return -1 for "no result"
     */
    public double applyToItem(ItemStack stack, double itemTypeAndLevelMultiplier)
    {
        return 1;
    }

    public abstract String getDescription();
}
