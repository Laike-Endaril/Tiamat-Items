package com.fantasticsource.tiamatitems.trait;

import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class CTraitElement extends Component
{
    public boolean ignoreMultipliers = false;

    public abstract int requiredArgumentCount();

    public final double getStandardAmount(int[] args, int argIndex, double minAmount, double maxAmount, double itemTypeAndLevelMultiplier)
    {
        double amount = minAmount + (maxAmount - minAmount) * args[argIndex] / (Integer.MAX_VALUE - 1);
        if (!ignoreMultipliers) amount *= itemTypeAndLevelMultiplier;
        return amount;
    }

    public final int getStandardCount(int[] args, int argIndex, int minCount, int maxCount, double itemTypeAndLevelMultiplier)
    {
        return (int) getStandardAmount(args, argIndex, minCount, maxCount, itemTypeAndLevelMultiplier);
    }

    /**
     * @param stack                      The ItemStack we're applying the trait element to
     * @param args                       A list of random integers ranging from 0 to (Integer.MAX_VALUE - 1).  Use these for absolute ranges, eg. choosing 1 of 3 different effects
     * @param itemTypeAndLevelMultiplier The combined multiplier for item type and item level (including contributions from rarity).  Use this in combination with baseArgs for relative ranges, eg. attribute modifiers
     */
    public abstract void applyToItem(ItemStack stack, int[] args, double itemTypeAndLevelMultiplier);


    public final String getDescription()
    {
        return getDescription(null, null, 1);
    }

    /**
     * @param args                       A list of random integers ranging from 0 to (Integer.MAX_VALUE - 1).  Use these for absolute ranges, eg. choosing 1 of 3 different effects
     * @param itemTypeAndLevelMultiplier The combined multiplier for item type and item level (including contributions from rarity).  Use this in combination with baseArgs for relative ranges, eg. attribute modifiers
     */
    public abstract String getDescription(ItemStack stack, int[] args, double itemTypeAndLevelMultiplier);


    @Override
    public CTraitElement write(ByteBuf buf)
    {
        buf.writeBoolean(ignoreMultipliers);

        return this;
    }

    @Override
    public CTraitElement read(ByteBuf buf)
    {
        ignoreMultipliers = buf.readBoolean();

        return this;
    }

    @Override
    public CTraitElement save(OutputStream stream)
    {
        new CBoolean().set(ignoreMultipliers).save(stream);

        return this;
    }

    @Override
    public CTraitElement load(InputStream stream)
    {
        ignoreMultipliers = new CBoolean().load(stream).value;

        return this;
    }
}
