package com.fantasticsource.tiamatitems.trait.unrecalculable;

import com.fantasticsource.tools.component.Component;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public abstract class CUnrecalculableTraitElement extends Component
{
    public abstract int requiredArgumentCount();

    /**
     * @param stack          The ItemStack we're applying the trait element to
     * @param baseArgs       A list of random integers ranging from 0 to (Integer.MAX_VALUE - 1).  Use these for absolute ranges, eg. choosing 1 of 3 different effects
     * @param multipliedArgs A list of random doubles ranging from 0 to 1 *and possibly beyond due to multipliers*.  Use these for relative ranges, eg. an amount for an attribute bonus
     */
    public abstract void applyToItem(ItemStack stack, int[] baseArgs, double[] multipliedArgs);

    /**
     * @param baseArgs       A list of random integers ranging from 0 to (Integer.MAX_VALUE - 1).  Use these for absolute ranges, eg. choosing 1 of 3 different effects
     * @param multipliedArgs A list of random doubles ranging from 0 to 1 *and possibly beyond due to multipliers*.  Use these for relative ranges, eg. an amount for an attribute bonus
     */
    public abstract String getDescription(ArrayList<Integer> baseArgs, double[] multipliedArgs);

    public final String getDescription()
    {
        return getDescription(new ArrayList<>(), new double[0]);
    }
}
