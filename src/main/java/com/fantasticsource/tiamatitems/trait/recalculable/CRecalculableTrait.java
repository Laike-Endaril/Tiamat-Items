package com.fantasticsource.tiamatitems.trait.recalculable;

import com.fantasticsource.tiamatitems.nbt.TraitTags;
import com.fantasticsource.tiamatitems.trait.CTrait;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.component.CInt;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;

public final class CRecalculableTrait extends CTrait
{
    public HashSet<CRecalculableTraitElement> elements = new HashSet<>();


    /**
     * @return The monetary value of the resulting trait
     */
    public double generateNBT(ItemStack stack, String poolSetName, CRecalculableTraitPool pool, double itemTypeAndLevelMultiplier, int... baseArgs)
    {
        ArrayList<Integer> baseArguments = new ArrayList<>();
        for (int base : baseArgs) baseArguments.add(base);


        int requiredArgumentCount = 0;
        for (CRecalculableTraitElement element : elements)
        {
            requiredArgumentCount = Tools.max(requiredArgumentCount, element.requiredArgumentCount());
        }
        while (baseArguments.size() < requiredArgumentCount)
        {
            baseArguments.add(Tools.random(Integer.MAX_VALUE));
        }


        TraitTags.addTraitTag(stack, poolSetName, pool, this, baseArguments);


        double multipliedTotal = 0;
        double[] multipliedArgs = new double[baseArguments.size()];
        int i = 0;
        for (int base : baseArguments)
        {
            double multiplied = ((double) base / (Integer.MAX_VALUE - 1) * itemTypeAndLevelMultiplier);
            multipliedArgs[i++] = multiplied;
            multipliedTotal += multiplied;
        }


        if (multipliedArgs.length == 0) return (minValue + maxValue) / 2;

        return minValue + (maxValue - minValue) * multipliedTotal / multipliedArgs.length / (Integer.MAX_VALUE - 1);
    }


    public void applyToItem(ItemStack stack, double itemTypeAndLevelMultiplier, int... baseArguments)
    {
        int requiredArgumentCount = 0;
        for (CRecalculableTraitElement element : elements)
        {
            requiredArgumentCount = Tools.max(requiredArgumentCount, element.requiredArgumentCount());
        }
        if (baseArguments.length < requiredArgumentCount) throw new IllegalStateException("Tried to apply recalculable trait without enough arguments: " + name + " (" + baseArguments.length + "/" + requiredArgumentCount + ")");


        double[] multipliedArgs = new double[baseArguments.length];
        int i = 0;
        for (int base : baseArguments)
        {
            double multiplied = ((double) base / (Integer.MAX_VALUE - 1) * itemTypeAndLevelMultiplier);
            multipliedArgs[i++] = multiplied;
        }


        for (CRecalculableTraitElement element : elements) element.applyToItem(stack, baseArguments, multipliedArgs);
    }


    @Override
    public CRecalculableTrait write(ByteBuf buf)
    {
        super.write(buf);

        buf.writeInt(elements.size());
        for (CRecalculableTraitElement element : elements) writeMarked(buf, element);

        return this;
    }

    @Override
    public CRecalculableTrait read(ByteBuf buf)
    {
        super.read(buf);

        elements.clear();
        for (int i = buf.readInt(); i > 0; i--) elements.add((CRecalculableTraitElement) readMarked(buf));

        return this;
    }

    @Override
    public CRecalculableTrait save(OutputStream stream)
    {
        super.save(stream);

        new CInt().set(elements.size()).save(stream);
        for (CRecalculableTraitElement element : elements) saveMarked(stream, element);

        return this;
    }

    @Override
    public CRecalculableTrait load(InputStream stream)
    {
        super.load(stream);

        CInt ci = new CInt();

        elements.clear();
        for (int i = ci.load(stream).value; i > 0; i--) elements.add((CRecalculableTraitElement) loadMarked(stream));

        return this;
    }
}
