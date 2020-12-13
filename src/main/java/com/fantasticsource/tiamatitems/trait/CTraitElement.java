package com.fantasticsource.tiamatitems.trait;

import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public abstract class CTraitElement extends Component
{
    public boolean ignoreMultipliers = false;

    public abstract int requiredArgumentCount();

    public final void applyToItem(ItemStack stack, int[] baseArgs, double[] multipliedArgs)
    {
        if (ignoreMultipliers)
        {
            double[] mArgs = new double[baseArgs.length];
            int i = 0;
            for (int base : baseArgs)
            {
                multipliedArgs[i++] = ((double) base / (Integer.MAX_VALUE - 1));
            }
            applyToItemInternal(stack, baseArgs, mArgs);
        }
        else applyToItemInternal(stack, baseArgs, multipliedArgs);
    }

    /**
     * @param stack          The ItemStack we're applying the trait element to
     * @param baseArgs       A list of random integers ranging from 0 to (Integer.MAX_VALUE - 1).  Use these for absolute ranges, eg. choosing 1 of 3 different effects
     * @param multipliedArgs A list of random doubles ranging from 0 to 1 *and possibly beyond due to multipliers*.  Use these for relative ranges, eg. an amount for an attribute bonus
     */
    protected abstract void applyToItemInternal(ItemStack stack, int[] baseArgs, double[] multipliedArgs);


    public final String getDescription(ArrayList<Integer> baseArgs, double[] multipliedArgs)
    {
        if (ignoreMultipliers)
        {
            double[] mArgs = new double[baseArgs.size()];
            for (int i = 0; i < baseArgs.size(); i++) mArgs[i] = baseArgs.get(i);
            return getDescriptionInternal(baseArgs, mArgs);
        }

        return getDescriptionInternal(baseArgs, multipliedArgs);
    }

    /**
     * @param baseArgs       A list of random integers ranging from 0 to (Integer.MAX_VALUE - 1).  Use these for absolute ranges, eg. choosing 1 of 3 different effects
     * @param multipliedArgs A list of random doubles ranging from 0 to 1 *and possibly beyond due to multipliers*.  Use these for relative ranges, eg. an amount for an attribute bonus
     */
    public abstract String getDescriptionInternal(ArrayList<Integer> baseArgs, double[] multipliedArgs);

    public final String getDescription()
    {
        return getDescription(new ArrayList<>(), new double[0]);
    }


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
