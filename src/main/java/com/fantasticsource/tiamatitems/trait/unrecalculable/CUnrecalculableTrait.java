package com.fantasticsource.tiamatitems.trait.unrecalculable;

import com.fantasticsource.tiamatitems.trait.CTrait;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.component.CInt;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashSet;

public class CUnrecalculableTrait extends CTrait
{
    public LinkedHashSet<CUnrecalculableTraitElement> elements = new LinkedHashSet<>();


    /**
     * @return The monetary value of the resulting trait
     */
    public double applyToItem(ItemStack stack, double itemTypeAndLevelMultiplier)
    {
        int requiredArgumentCount = 0;
        for (CUnrecalculableTraitElement element : elements)
        {
            requiredArgumentCount = Tools.max(requiredArgumentCount, element.requiredArgumentCount());
        }


        int[] baseArguments = new int[requiredArgumentCount];
        double[] decimalArgs = new double[requiredArgumentCount];
        for (int i = 0; i < requiredArgumentCount; i++)
        {
            int base = Tools.random(Integer.MAX_VALUE);
            baseArguments[i] = base;
            decimalArgs[i] = (double) base / (Integer.MAX_VALUE - 1);
        }


        double averagedRoll = 0, rollUsageCount = 0;
        for (CUnrecalculableTraitElement element : elements)
        {
            element.applyToItem(stack, baseArguments, itemTypeAndLevelMultiplier);

            for (int i = 0; i < element.requiredArgumentCount(); i++)
            {
                averagedRoll += element.ignoreMultipliers ? 1 : decimalArgs[i];
                rollUsageCount++;
            }
        }


        if (requiredArgumentCount == 0) return (minValue + maxValue) * 0.5 * itemTypeAndLevelMultiplier;

        averagedRoll /= rollUsageCount;
        return (minValue + (maxValue - minValue) * averagedRoll) * itemTypeAndLevelMultiplier;
    }


    @Override
    public CUnrecalculableTrait write(ByteBuf buf)
    {
        super.write(buf);

        buf.writeInt(elements.size());
        for (CUnrecalculableTraitElement element : elements) writeMarked(buf, element);

        return this;
    }

    @Override
    public CUnrecalculableTrait read(ByteBuf buf)
    {
        super.read(buf);

        elements.clear();
        for (int i = buf.readInt(); i > 0; i--) elements.add((CUnrecalculableTraitElement) readMarked(buf));

        return this;
    }

    @Override
    public CUnrecalculableTrait save(OutputStream stream)
    {
        super.save(stream);

        new CInt().set(elements.size()).save(stream);
        for (CUnrecalculableTraitElement element : elements) saveMarked(stream, element);

        return this;
    }

    @Override
    public CUnrecalculableTrait load(InputStream stream)
    {
        super.load(stream);

        CInt ci = new CInt();

        elements.clear();
        for (int i = ci.load(stream).value; i > 0; i--) elements.add((CUnrecalculableTraitElement) loadMarked(stream));

        return this;
    }
}
